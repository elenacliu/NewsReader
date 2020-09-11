package com.java.renyi.db;

import android.app.Application;
import android.content.res.AssetManager;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.alibaba.fastjson.*;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import com.java.renyi.LDA.me.xiaosheng.lda.LDAModel;
import com.java.renyi.LDA.me.xiaosheng.lda.HanLDA;

import static com.java.renyi.db.EntryRoomDatabase.databaseWriteExecutor;
import static com.java.renyi.db.GetHTML.getHTML;

class EntryRepository {
    static private EntryDao mEntryDao;
//    private LiveData<List<Entry>> mAllEntrys;

    static private LDAModel hanLDA;
    static public final Integer lockInt = 0;
    static private MutableLiveData<List<Entry>> mNewsEntries;
    static private MutableLiveData<List<Entry>> mPaperEntries;
    static private MutableLiveData<List<Entry>> mSearchResult;
    static private MutableLiveData<List<SearchEntity>> searchEntiryList;
    static private MutableLiveData<List<PandemicStatus>> globalStatus;
    static private MutableLiveData<List<PandemicStatus>> domesticStatus;
    static private MutableLiveData<List<Scholar>> livingScholar;
    static private MutableLiveData<List<Scholar>> passedAwayScholar;

    private LiveData<List<Entry>> researchCluster;
    private LiveData<List<Entry>> medicineCluster;
    private LiveData<List<Entry>> pandemicCluster;
    private LiveData<List<Entry>> treatmentCluster;

    static private boolean newsFirstPageReady = false;
    static private boolean paperFirstPageReady = false;


    static private  Application app;
    public static String lastNewsDate;
    public static String lastPaperDate;

    public static int newsLimitNumber;
    public static int paperLimitNumber;

    private static int nowNewsPage = 2;
    private  static int nowPaperPage = 2;
    private static int nowEventPage = 1;
    private static final int eventPageSize = 50;

    private static final int PAGE_SIZE = 100;
    private static final int ADD_SIZE = 10;

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

    static public MutableLiveData<List<Scholar>> getLivingScholar() {
        if (livingScholar == null) {
            livingScholar = new MutableLiveData<>();
        }
        return livingScholar;
    }

    static public MutableLiveData<List<Scholar>> getPassedAwayScholar() {
        if (passedAwayScholar == null) {
            passedAwayScholar = new MutableLiveData<>();
        }
        return passedAwayScholar;
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
        Log.e("entering repo init", getTimeMilli());
        app = application;
        Log.e("before db", "Before");
        EntryRoomDatabase db = EntryRoomDatabase.getDatabase(application);
        Log.e("after db", "Before");
        mEntryDao = db.entryDao();
        researchCluster = mEntryDao.getResearchCluster();
        medicineCluster = mEntryDao.getMedicineCluster();
        pandemicCluster = mEntryDao.getPandemicCluster();
        treatmentCluster = mEntryDao.getTreatmentCluster();

        databaseWriteExecutor.execute(() -> {
            // TODO : if first init no net, no get
            addMoreNews(ADD_SIZE);
            if (checkNetwork()) {
                Log.e("AddNews", "Online");
                refreshNews(false);
            } else {
                Looper.prepare();
                Toast toast = Toast.makeText(app, "You Do not Have Network When First Entered", Toast.LENGTH_SHORT);
                toast.show();
                Looper.loop();
                Log.e("AddNews", "Offline");
            }
            Log.e("inInitCluster", "initing");
        });

        databaseWriteExecutor.execute(() -> {
            // TODO : if first init no net, no get
            // ADD_SIZE - 1 for refresh
            addMorePaper(ADD_SIZE);
            if (checkNetwork()) {
                Log.e("AddPaper", "Online");
                refreshPaper(false);
            } else {
                Log.e("AddPaper", "Offline");
            }
        });

        databaseWriteExecutor.execute(()->{
            // TODO: insert and parse 699 events in one shot is too slow, consider insert multiple times or use multiple thread to insert
            if (checkNetwork())
                insertEvents();
        });



//        databaseWriteExecutor.execute(()->{
//            // initialization for
//            Log.e("InitSplit", getTimeMilli());
//            List<Term> terms = ToAnalysis.parse("今天阳光普照，万物生辉。刘畅的白色衬衫很好看。").getTerms();
//            Log.e("SplitComplete", terms.toString());
//            Log.e("SplitComplete", getTimeMilli());
//            Log.e("AnthoerSplit", ToAnalysis.parse("今天阳光普照，万物生辉。").getTerms().toString());
//            Log.e("AnotherSplitComplete", getTimeMilli());
//        });

        databaseWriteExecutor.execute(()->{
            synchronized (lockInt) {
                AssetManager assetManager = app.getAssets();
                Log.e("begin getting LDA", "getting LDA" + getTimeMilli());
                try {
//                    hanLDA = new LDAModel(assetManager.open("mylda5141-3.model"));
                    hanLDA = new LDAModel(assetManager.open("event-5.model"));

//                    hanLDA = new LDAModel(assetManager.open("myldaAll-4.model"));
                } catch (IOException e) {
                    Log.e("repoInit","LoadModelFailed");
                    e.printStackTrace();
                }
                Log.e("after getting LDA", "getting LDA"+getTimeMilli());
            }
        });
//        mAllEntrys = mEntryDao.getAlphabetizedEntrys();


    }



    private static void cluster() {
        Log.e("beginCluster", getTimeMilli());
        Entry[] entries = mEntryDao.getNotClustered();
        int length = entries.length;
        for (Entry entry : entries) {
            entry.modifyCluster();
        }
        mEntryDao.update(entries);
        Log.e("finishCluster", getTimeMilli());
//        Toast.makeText(app, "ClusteredComplete!", Toast.LENGTH_SHORT).show();
    }

    public static LDAModel getHDA() {
        if (hanLDA == null) {
            Log.e("gettingLDAWaiting", "waiting"+getTimeMilli());
            synchronized (lockInt) {
                Log.e("gettingLDAOK", "OK"+getTimeMilli());
                return hanLDA;
            }
        } else {
            return hanLDA;
        }
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
            if (checkNetwork()) {
                searchEntiryList.postValue(searchEntity(target));
            }
            else {
                Looper.prepare();
                Toast.makeText(app,"You Do Not Have Network When Getting Search Entity", Toast.LENGTH_SHORT).show();
                Looper.loop();
            }
        });
    }

    public void repoAskGlobalStatus() {
        databaseWriteExecutor.execute(()->{
            if (checkNetwork()) {
                globalStatus.postValue(askGlobalStatus());
            }
            else {
                Log.e("in repoaskGlobal", "no net");
                Looper.prepare();
                Toast.makeText(app,"You Do Not Have Network When Getting Global Pandemic Info", Toast.LENGTH_SHORT).show();
                Looper.loop();
            }
        });
    }
    public void repoAskDomesticStatus() {
        databaseWriteExecutor.execute(()->{
            if (checkNetwork()) {
                domesticStatus.postValue(askDomesticStatus());
            }
            else {
                Looper.prepare();
                Toast.makeText(app,"You Do Not Have Network When Getting Domestic Pandemic Info", Toast.LENGTH_SHORT).show();
                Looper.loop();
            }
        });
    }

    public void repoAskLivingScholar() {
        databaseWriteExecutor.execute(()->{
            if (checkNetwork()) {
                livingScholar.postValue(askLivingScholar());
            }
            else {
                Looper.prepare();
                Toast.makeText(app,"You Do Not Have Network When Getting Living Scholar Info", Toast.LENGTH_SHORT).show();
                Looper.loop();
            }
        });
    }
    public void repoAskPassedAwayScholar() {
        databaseWriteExecutor.execute(()->{
            if (checkNetwork()) {
                passedAwayScholar.postValue(askPassedAwayScholar());
            }
            else {
                Looper.prepare();
                Toast.makeText(app,"You Do Not Have Network When Getting Living Scholar Info", Toast.LENGTH_SHORT).show();
                Looper.loop();
            }
        });
    }
    private List<Scholar> askLivingScholar() {
        String url = "https://innovaapi.aminer.cn/predictor/api/v1/valhalla/highlight/get_ncov_expers_list?v=2";
        String result = getHTML(url);
        JSONObject jsonobject = JSON.parseObject(result);
        List<Scholar> livingScholar = new ArrayList<Scholar>();

        if ("success".equals(jsonobject.getString("message"))) {
            JSONArray arr = jsonobject.getJSONArray("data");
            int arrLength = arr.size();
            for (int i = 0; i < arrLength; i++) {
                JSONObject o = arr.getJSONObject(i);
                Boolean isPassedAway = o.getBoolean("is_passedaway");
                if (!isPassedAway)
                    livingScholar.add(new Scholar(o));
            }
        }
        return livingScholar;
    }

    private List<Scholar> askPassedAwayScholar() {
        String url = "https://innovaapi.aminer.cn/predictor/api/v1/valhalla/highlight/get_ncov_expers_list?v=2";
        String result = getHTML(url);
        JSONObject jsonobject = JSON.parseObject(result);
        List<Scholar> passedAwayScholar = new ArrayList<Scholar>();
        if ("success".equals(jsonobject.getString("message"))) {
            JSONArray arr = jsonobject.getJSONArray("data");
            int arrLength = arr.size();
            for (int i = 0; i < arrLength; i++) {
                JSONObject o = arr.getJSONObject(i);
                Boolean isPassedAway = o.getBoolean("is_passedaway");
                if (isPassedAway)
                    passedAwayScholar.add(new Scholar(o));
            }
        }
        return passedAwayScholar;
    }


    LiveData<List<Entry>> getResearchCluster() {return researchCluster;}
    LiveData<List<Entry>> getMedicineCluster() {return medicineCluster;}
    LiveData<List<Entry>> getPandemicCluster() {return pandemicCluster;}
    LiveData<List<Entry>> getTreatmentCluster() { return treatmentCluster; }

    public  static String getTimeMilli() {
        Calendar Cld = Calendar.getInstance();
        int YY = Cld.get(Calendar.YEAR) ;
        int MM = Cld.get(Calendar.MONTH)+1;
        int DD = Cld.get(Calendar.DATE);
        int HH = Cld.get(Calendar.HOUR_OF_DAY);
        int mm = Cld.get(Calendar.MINUTE);
        int SS = Cld.get(Calendar.SECOND);
        int MI = Cld.get(Calendar.MILLISECOND);
        return YY + "/" + MM + "/" + DD + "-" + HH + ":" + mm + ":" + SS + ":" + MI;
    }

    private static Entry[] getEntries(String naive_url, int page, int size, String type, boolean getClusterImmediate) {
        String url = naive_url + "?type=" + type + "&page=" + page + "&size=" + size;
        String result = getHTML(url);
        Log.e("after get HTML", "in getEntries");
        try {  // when no net, getJSONArray is on a null Object, which is error
            JSONArray jsonarr = JSON.parseObject(result).getJSONArray("data");
            Log.e("beginParsing", "begin"+getTimeMilli());
            List<Entry> list = EntryRepository.parseJSONArray(type, jsonarr, getClusterImmediate);
            Log.e("afterParsing", "after"+getTimeMilli());
            return list.toArray(new Entry[list.size()]);
        } catch (Exception e) {
            Log.e("in getEntries error", e.toString());
            List<Entry> list = new ArrayList<>();
            return list.toArray(new Entry[list.size()]);
        }
    }

    private static void insertEvents() {
        Log.e("begin InsertEvents", getTimeMilli());
        String url = "https://covid-dashboard.aminer.cn/api/events/list";
        Entry[] newPageEntry = getEntries(url, nowEventPage, eventPageSize, "event", true);
        if (newPageEntry.length > 0) { // == 0 when getEntries Failed
            mEntryDao.insert(newPageEntry);
            Log.e("insertEvents!", getTimeMilli());
            nowEventPage += 1;
        }
    }

    private static void getMoreNews() {
        String url = "https://covid-dashboard.aminer.cn/api/events/list";
        if (!newsFirstPageReady && nowNewsPage == 2) {  // for no net to with net, only scroll up
            nowNewsPage = 1;
            newsFirstPageReady = true;
        }

        Entry[] newPageEntry = getEntries(url, nowNewsPage, PAGE_SIZE, "news", false);
        if (newPageEntry.length > 0) { // == 0 when getEntries Failed
            mEntryDao.insert(newPageEntry);
            nowNewsPage += 1;
            Log.e("now news page", nowNewsPage+"");
        }
    }

    private static void getMorePaper() {
        String url = "https://covid-dashboard.aminer.cn/api/events/list";
        if (!paperFirstPageReady && nowPaperPage == 2) {
            nowPaperPage = 1;
            paperFirstPageReady = true;
        }

        Entry[] newPageEntry = getEntries(url, nowPaperPage, PAGE_SIZE, "paper", false);
        if (newPageEntry.length > 0) { // == 0 when getEntries Failed
            mEntryDao.insert(newPageEntry);
            nowPaperPage += 1;
        }
    }

    static private void refreshNews(boolean replace) {
        String url = "https://covid-dashboard.aminer.cn/api/events/list";
        Entry[] refreshed = getEntries(url, 1, PAGE_SIZE, "news", false);
        if (refreshed.length > 0) {
            newsFirstPageReady = true;
        }
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
        Entry[] refreshed = getEntries(url, 1, PAGE_SIZE, "paper", false);
        if (refreshed.length > 0) {
            paperFirstPageReady = true;
        }
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
    private static List<Entry> parseJSONArray(String type, JSONArray j, boolean getClusterImmediate) {
        List<Entry> res = new ArrayList<>();
        for (Object o: j) {
            JSONObject json = (JSONObject)(o);
            res.add(new Entry(type, json, getClusterImmediate));
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
//            if (update.size() != 0 ) {  // enter this branch when no data in db
                if (checkNetwork()) {
                    getMoreNews();
                    update = mEntryDao.selectData("news", lastNewsDate, newsLimitNumber);
                }
                else {
                    Looper.prepare();
                    Toast toast = Toast.makeText(app, "You Do not Have Network When Getting More News", Toast.LENGTH_SHORT);
                    toast.show();
                    Looper.loop();

                    Log.e("no net", "cannot getMore in news");
                }
//            }
        }

        Log.e("show size", update.size()+"");
        getCurrentNewsEntrys().postValue(update);
        Log.e("posted!", getTimeMilli());
    }

    static private void addMorePaper(int offset) {
        if (lastPaperDate == null) {
            lastPaperDate = getLatestDate();
        }
        paperLimitNumber += offset;
        List<Entry> update = mEntryDao.selectData("paper", lastPaperDate, paperLimitNumber);
        if (update.size() < paperLimitNumber) {
//            if (update.size() != 0) {
                if (checkNetwork()) {
                    getMorePaper();
                    update = mEntryDao.selectData("paper", lastPaperDate, paperLimitNumber);
                } else {
                    Looper.prepare();
                    Toast toast = Toast.makeText(app, "You Do not Have Network When Getting More Paper", Toast.LENGTH_SHORT);
                    toast.show();
                    Looper.loop();
                    Log.e("no net", "cannot getMore in paper");
                }
//            }
        }
        getCurrentPaperEntrys().postValue(update);
//        cluster();
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
        String result = getHTML(url);
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
        String result = getHTML(url);
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
        String result = getHTML(url);
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

    public void addMoreEvents() {
        databaseWriteExecutor.execute(()->{
            if (checkNetwork()) {
                insertEvents();

            }
            else {
                Looper.prepare();
                Toast.makeText(app, "You Do not Have Network When Adding More Events", Toast.LENGTH_SHORT).show();
                Looper.loop();
            }
        });
    }


}
