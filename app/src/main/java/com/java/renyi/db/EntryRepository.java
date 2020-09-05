package com.java.renyi.db;

import android.app.Application;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import androidx.lifecycle.MutableLiveData;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.java.renyi.db.Entry;
import com.java.renyi.db.EntryDao;
import com.java.renyi.db.EntryRoomDatabase;
import com.java.renyi.db.PandemicStatus;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Set;

import static com.java.renyi.db.EntryRoomDatabase.databaseWriteExecutor;

class EntryRepository {
    static private EntryDao mEntryDao;
//    private LiveData<List<Entry>> mAllEntrys;

    static private MutableLiveData<List<Entry>> mNewsEntries;
    static private MutableLiveData<List<Entry>> mPaperEntries;
    static private MutableLiveData<List<Entry>> mSearchResult;
    static private MutableLiveData<List<SearchEntity>> searchEntiryList;
    static private MutableLiveData<List<PandemicStatus>> globalStatus;
    static private MutableLiveData<List<PandemicStatus>> domesticStatus;

    static private  Application app;
    public static String lastNewsDate;
    public static String lastPaperDate;

    public static int newsLimitNumber;
    public static int paperLimitNumber;

    private static int nowNewsPage = 2;
    private  static int nowPaperPage = 2;

    private static final int PAGE_SIZE = 10;
    private static final int ADD_SIZE = 5;

    static public MutableLiveData<List<Entry>> getCurrentNewsEntrys() {
        if (mNewsEntries == null) {
            mNewsEntries = new MutableLiveData<>();
        }
        return mNewsEntries;
    }

    static public MutableLiveData<List<Entry>> getCurrentPaperEntrys() {
        if (mPaperEntries == null) {
            mPaperEntries = new MutableLiveData<>();
        }
        return mPaperEntries;
    }

    static public MutableLiveData<List<Entry>> getmSearchResult() {
        if (mSearchResult == null) {
            mSearchResult = new MutableLiveData<>();
        }
        return mSearchResult;
    }

    static public MutableLiveData<List<SearchEntity>> getSearchEntityList() {
        if (searchEntiryList == null) {
            searchEntiryList = new MutableLiveData<>();
        }
        return searchEntiryList;
    }

    static public MutableLiveData<List<PandemicStatus>> getGlobalStatus() {
        if (globalStatus == null) {
            globalStatus = new MutableLiveData<>();
        }
        return globalStatus;
    }

    static public MutableLiveData<List<PandemicStatus>> getDomesticStatus() {
        if (domesticStatus == null) {
            domesticStatus = new MutableLiveData<>();
        }
        return domesticStatus;
    }

    EntryRepository(Application application) {
        Log.e("before db", "Before");
        EntryRoomDatabase db = EntryRoomDatabase.getDatabase(application);
        Log.e("after db", "Before");
        app = application;
        mEntryDao = db.entryDao();
        databaseWriteExecutor.execute(() -> {
            // TODO : if first init no net, no get
            // ADD_SIZE - 1 for refresh
            addMoreNews(ADD_SIZE);
            if (checkNetwork()) {
                Log.e("AddNews", "Online");
                refreshNews(true);
            } else {
                Looper.prepare();
                Toast toast = Toast.makeText(app, "You Do not Have Network When First Entered", Toast.LENGTH_SHORT);
                toast.show();
                Looper.loop();

                Log.e("AddNews", "Offline");
            }
        });

        databaseWriteExecutor.execute(() -> {
            // TODO : if first init no net, no get
            // ADD_SIZE - 1 for refresh
            addMorePaper(ADD_SIZE);
            if (checkNetwork()) {
                Log.e("AddPaper", "Online");
                refreshPaper(true);
            } else {
                Log.e("AddPaper", "Offline");
            }
        });

//        mAllEntrys = mEntryDao.getAlphabetizedEntrys();
    }

    public void setViewed(String _id, String type) {
        databaseWriteExecutor.execute(() -> {
            mEntryDao.setViewed(true, _id);
            if ("news".equals(type)) {
                addMoreNews(0);
            } else {
                addMorePaper(0);
            }
        });
    }
    public void search(String target) {
        databaseWriteExecutor.execute(() -> {
            mSearchResult.postValue(mEntryDao.search("%"+target+"%"));
        });
    }
    public static void repoAddMoreNews() {
        databaseWriteExecutor.execute(()->{
            addMoreNews(ADD_SIZE);
        });
    }

    public static void repoAddMorePaper() {
        databaseWriteExecutor.execute(()->{
            addMorePaper(ADD_SIZE);
        });
    }

    public void repoSearchEntity(String target) {
        databaseWriteExecutor.execute(()->{
            searchEntiryList.postValue(searchEntity(target));
        });
    }

    public void repoAskGlobalStatus() {
        databaseWriteExecutor.execute(()->{
            globalStatus.postValue(askGlobalStatus());
        });
    }
    public void repoAskDomesticStatus() {
        databaseWriteExecutor.execute(()->{
            domesticStatus.postValue(askDomesticStatus());
        });
    }

    private static Entry[] getEntries(String naive_url, int page, int size, String type) {
        String url = naive_url + "?type=" + type + "&page=" + page + "&size=" + size;
        String result = GetHTML.getHTML(url);
        Log.e("after get HTML", "in getEntries");
        try {  // when no net, getJSONArray is on a null Object, which is error
            JSONArray jsonarr = JSON.parseObject(result).getJSONArray("data");
            List<Entry> list = com.java.renyi.db.EntryRepository.parseJSONArray(type, jsonarr);
            return list.toArray(new Entry[list.size()]);
        } catch (Exception e) {
            Log.e("in getEntries error", e.toString());
            List<Entry> list = new ArrayList<>();
            return list.toArray(new Entry[list.size()]);
        }
    }


    private static void getMoreNews() {
        String url = "https://covid-dashboard.aminer.cn/api/events/list";
        Entry[] newPageEntry = getEntries(url, nowNewsPage, PAGE_SIZE, "news");
        if (newPageEntry.length > 0) { // == 0 when getEntries Failed
            mEntryDao.insert(newPageEntry);
            nowNewsPage += 1;
            Log.e("now news page", nowNewsPage+"");
        }
    }
    private static void getMorePaper() {
        String url = "https://covid-dashboard.aminer.cn/api/events/list";
        Entry[] newPageEntry = getEntries(url, nowPaperPage, PAGE_SIZE, "paper");
        if (newPageEntry.length > 0) { // == 0 when getEntries Failed
            mEntryDao.insert(newPageEntry);
            nowPaperPage += 1;
        }
    }



    static private void refreshNews(boolean replace) {
        String url = "https://covid-dashboard.aminer.cn/api/events/list";
        Entry[] refreshed = getEntries(url, 1, PAGE_SIZE, "news");
        if (replace) {
            mEntryDao.insert_replace(refreshed);
        } else {
            mEntryDao.insert(refreshed);
        }
        lastNewsDate = null;
        newsLimitNumber = 0;
        addMoreNews(ADD_SIZE);
    }

    static private void refreshPaper(boolean replace) {
        String url = "https://covid-dashboard.aminer.cn/api/events/list";
        Entry[] refreshed = getEntries(url, 1, PAGE_SIZE, "paper");
        if (replace) {
            mEntryDao.insert_replace(refreshed);
        } else {
            mEntryDao.insert(refreshed);
        }
        lastPaperDate = null;
        paperLimitNumber = 0;
        addMorePaper(ADD_SIZE);
    }

//    // Room executes all queries on a separate thread.
//    // Observed LiveData will notify the observer when the data has changed.
//    LiveData<List<Entry>> getAllEntrys() {
//        return mAllEntrys;
//    }

    // You must call this on a non-UI thread or your app will throw an exception. Room ensures
    // that you're not doing any long running operations on the main thread, blocking the UI.
    void insert(Entry... entry) {
        databaseWriteExecutor.execute(() -> {
            mEntryDao.insert(entry);
        });
    }

    public void repoRefresh(String type) {
        databaseWriteExecutor.execute(() -> {
            if (checkNetwork()) {
                refreshNews(false);
                refreshPaper(false);
            } else {
                Looper.prepare();
                Toast toast = Toast.makeText(app, "You Do not Have Network When Refreshing", Toast.LENGTH_SHORT);
                toast.show();
                Looper.loop();
                Log.e("no net", "unable to refresh");
            }
        });
    }



    // return the list of entry based on the given JSONArray
    private static List<Entry> parseJSONArray(String type, JSONArray j) {
        List<Entry> res = new ArrayList<>();
        for (Object o: j) {
            JSONObject json = (JSONObject)(o);
            res.add(new Entry(type, json));
        }
        return res;
    }


    static private void addMoreNews(int offset) {
        if (lastNewsDate == null) {
            Log.e("in date null", "it is null");
            lastNewsDate = getLatestDate();
        }
        newsLimitNumber += offset;
        Log.e("want more news", newsLimitNumber +"");
        List<Entry> update = mEntryDao.selectData("news", lastNewsDate, newsLimitNumber);
        Log.e("update size = ", update.size() + "");
        // TODO: CHANGE LOGIC HERE!
        if (update.size() < newsLimitNumber) {
            // TODO: first init no network?
            if (update.size() != 0 ) {  // enter this branch when no data in db
                if (checkNetwork()) {
                    getMoreNews();
                    update = mEntryDao.selectData("news", lastNewsDate, newsLimitNumber);
                } else {
                    Looper.prepare();
                    Toast toast = Toast.makeText(app, "You Do not Have Network When Getting More News", Toast.LENGTH_SHORT);
                    toast.show();
                    Looper.loop();

                    Log.e("no net", "cannot getMore in news");
                }
            }
        }

        Log.e("show size", update.size()+"");
        getCurrentNewsEntrys().postValue(update);
    }

    static private void addMorePaper(int offset) {
        if (lastPaperDate == null) {
            lastPaperDate = getLatestDate();
        }
        paperLimitNumber += offset;
        List<Entry> update = mEntryDao.selectData("paper", lastPaperDate, paperLimitNumber);
        if (update.size() < paperLimitNumber) {
            if (update.size() != 0) {
                if (checkNetwork()) {
                    // TODO: from no net to with net, cannot get more
                    getMorePaper();
                    update = mEntryDao.selectData("paper", lastPaperDate, paperLimitNumber);
                } else {
                    Looper.prepare();
                    Toast toast = Toast.makeText(app, "You Do not Have Network When Getting More Paper", Toast.LENGTH_SHORT);
                    toast.show();
                    Looper.loop();
                    Log.e("no net", "cannot getMore in paper");
                }
            }
        }
        getCurrentPaperEntrys().postValue(update);

    }

    static private String getLatestDate() {
        Calendar calendar= Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd/HH:mm:ss");
        calendar.add(Calendar.HOUR_OF_DAY, 8);
        String nowTime = sdf.format(calendar.getTime());
        Log.e("nowTime:", nowTime);
        return nowTime;
    }
    static private boolean checkNetwork() {
        if (NetworkUtil.isNetworkAvailable(app))
            return true;
        return false;
    }

    private List<SearchEntity> searchEntity(String target) {
        String url = "https://innovaapi.aminer.cn/covid/api/v1/pneumonia/entityquery?entity="+target;
        String result = GetHTML.getHTML(url);
        JSONObject jsonobject = JSON.parseObject(result);
        List<SearchEntity> searchEntityResult = new ArrayList<SearchEntity>();
        if ("success".equals(jsonobject.getString("msg"))) {
            JSONArray arr = jsonobject.getJSONArray("data");
            int arrLength = arr.size();
            for (int i = 0; i < arrLength; i++) {
                searchEntityResult.add(new SearchEntity(arr.getJSONObject(i)));
            }
        }
        return searchEntityResult;
    }

    private List<PandemicStatus> askGlobalStatus() {
        List<PandemicStatus> global = new ArrayList<PandemicStatus>();
        String url = "https://covid-dashboard.aminer.cn/api/dist/epidemic.json";
        String result = GetHTML.getHTML(url);
        JSONObject jsonobject = JSON.parseObject(result);
        Set<String> keys = jsonobject.keySet();
        for (String s : keys) {
            String [] regionInfo = s.split("\\|");
            int regionInfoLength = regionInfo.length;
            if (regionInfoLength == 1) {  // a country
                global.add(new PandemicStatus(regionInfo[0], getLatestPandemic(jsonobject, s)));
            }
        }
        return global;
    }
    private  List<PandemicStatus> askDomesticStatus() {
        List<PandemicStatus> domestic = new ArrayList<PandemicStatus>();
        String url = "https://covid-dashboard.aminer.cn/api/dist/epidemic.json";
        String result = GetHTML.getHTML(url);
        JSONObject jsonobject = JSON.parseObject(result);
        Set<String> keys = jsonobject.keySet();
        for (String s : keys) {
            String [] regionInfo = s.split("\\|");
            int regionInfoLength = regionInfo.length;
            if (regionInfoLength == 2) {
                if ("China".equals(regionInfo[0])) {  // China Province
                    domestic.add(new PandemicStatus(regionInfo[1], getLatestPandemic(jsonobject, s)));
                }
            }
        }
        return domestic;
    }

    private String getLatestPandemic(JSONObject jsonobject, String key) {
        JSONArray dataArr = jsonobject.getJSONObject(key).getJSONArray("data");
        return dataArr.get(dataArr.size()- 1).toString();
    }
}
