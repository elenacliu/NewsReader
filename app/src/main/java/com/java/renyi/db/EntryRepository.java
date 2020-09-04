package com.java.renyi.db;

import android.app.Application;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import com.alibaba.fastjson.*;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

class EntryRepository {

    static private EntryDao mEntryDao;
//    private LiveData<List<Entry>> mAllEntrys;

    static private MutableLiveData<List<Entry>> mNewsEntries;
    static public MutableLiveData<List<Entry>> getCurrentNewsEntrys() {
        if (mNewsEntries == null) {
            mNewsEntries = new MutableLiveData<>();
            mNewsEntries.setValue(new ArrayList<>());
        }
        return mNewsEntries;
    }

    static private MutableLiveData<List<Entry>> mPaperEntries;
    static public MutableLiveData<List<Entry>> getCurrentPaperEntrys() {
        if (mPaperEntries == null) {
            mPaperEntries = new MutableLiveData<>();
            mPaperEntries.setValue(new ArrayList<>());
        }
        return mPaperEntries;
    }

    static private MutableLiveData<List<Entry>> mSearchResult;
    static public MutableLiveData<List<Entry>> getmSearchResult() {
        if (mSearchResult == null) {
            mSearchResult = new MutableLiveData<>();
//            mSearchResult.setValue(new ArrayList<>());
        }
        return mSearchResult;
    }

    EntryRepository(Application application) {
        EntryRoomDatabase db = EntryRoomDatabase.getDatabase(application);
        mEntryDao = db.entryDao();
//        mAllEntrys = mEntryDao.getAlphabetizedEntrys();
    }

//    // Room executes all queries on a separate thread.
//    // Observed LiveData will notify the observer when the data has changed.
//    LiveData<List<Entry>> getAllEntrys() {
//        return mAllEntrys;
//    }

    // You must call this on a non-UI thread or your app will throw an exception. Room ensures
    // that you're not doing any long running operations on the main thread, blocking the UI.
    void insert(Entry... entry) {
        EntryRoomDatabase.databaseWriteExecutor.execute(() -> {
            mEntryDao.insert(entry);
        });
    }
    public void refresh(String type) {
        EntryRoomDatabase.databaseWriteExecutor.execute(() -> {
            EntryRoomDatabase.refresh(type);
        });
    }

    // get json string from url
    public static String getHTML(String urlToRead)  {
        StringBuilder result = new StringBuilder();
        try {
            URL url = new URL(urlToRead);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line;
            while ((line = rd.readLine()) != null) {
                result.append(line);
            }
            rd.close();
        } catch (Exception e) {
            Log.e("getHTML error", e.toString());
        }
        return result.toString();
    }

    // return the list of entry based on the given JSONArray
    public static List<Entry> parseJSONArray(String type, JSONArray j) {
        List<Entry> res = new ArrayList<>();
        for (Object o: j) {
            JSONObject json = (JSONObject)(o);
            res.add(new Entry(type, json));
        }
        return res;
    }


    static void addMoreNews(int offset) {
        EntryRoomDatabase.databaseWriteExecutor.execute(() -> {
            if (EntryRoomDatabase.lastNewsDate == null) {
                EntryRoomDatabase.lastNewsDate = getLatestDate("news");
            }
            EntryRoomDatabase.newsLimitNumber += offset;
            List<Entry> update = mEntryDao.selectData("news", EntryRoomDatabase.lastNewsDate, EntryRoomDatabase.newsLimitNumber);
            List<Entry> prevNews = getCurrentNewsEntrys().getValue();
            Log.e("size:", update.size() + "" + prevNews.size());
            if (update.size() <= prevNews.size()) {
                EntryRoomDatabase.getMore("news");
                update = mEntryDao.selectData("news", EntryRoomDatabase.lastNewsDate, EntryRoomDatabase.newsLimitNumber);
            }

            getCurrentNewsEntrys().postValue(update);
        });
    }

    static void addMorePaper(int offset) {
        EntryRoomDatabase.databaseWriteExecutor.execute(() -> {
            if (EntryRoomDatabase.lastPaperDate == null) {
                EntryRoomDatabase.lastPaperDate = getLatestDate("paper");
            }
            EntryRoomDatabase.paperLimitNumber += offset;
            List<Entry> update = mEntryDao.selectData("paper", EntryRoomDatabase.lastPaperDate, EntryRoomDatabase.paperLimitNumber);
            List<Entry> prevPaper = getCurrentPaperEntrys().getValue();
            if (update.size() <= prevPaper.size()) {
                EntryRoomDatabase.getMore("paper");
                update = mEntryDao.selectData("paper", EntryRoomDatabase.lastPaperDate, EntryRoomDatabase.paperLimitNumber);
            }

            getCurrentPaperEntrys().postValue(update);
        });
    }

    static String getLatestDate(String type) {
        // TODO: do not need all items, only return text is ok
        Calendar calendar= Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd/HH:mm:ss");
        calendar.add(Calendar.HOUR_OF_DAY, 8);
        String nowTime = sdf.format(calendar.getTime());
        Log.e("nowTime:", nowTime);
        return nowTime;
//        List<Entry> list = mEntryDao.getTypeTimeData(type);
//        return list.get(0).time;
    }

    public void setViewed(String _id, String type) {
        EntryRoomDatabase.databaseWriteExecutor.execute(() -> {
            mEntryDao.setViewed(true, _id);
            if ("news".equals(type)) {
                addMoreNews(0);
            } else {
                addMorePaper(0);
            }
        });
    }
    public void search(String target) {
        EntryRoomDatabase.databaseWriteExecutor.execute(() -> {
            mSearchResult.postValue(mEntryDao.search("%"+target+"%"));
        });
    }
}
