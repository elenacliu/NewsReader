package com.java.renyi.db;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(entities = {Entry.class}, version = 1, exportSchema = false)
public abstract class EntryRoomDatabase extends RoomDatabase {
    public abstract EntryDao entryDao();
    private static volatile EntryRoomDatabase INSTANCE;
    private static final int INIT_SHOW_SIZE = 10;

    public static String lastNewsDate;
    public static String lastPaperDate;
//    public static String nowTime;


    public static int newsLimitNumber;
    public static int paperLimitNumber;
    private static int newsEndPage;
    private  static int paperEndPage;

    private static final int PAGE_SIZE = 50;
    private static final int NUMBER_OF_THREADS = 4;
    static final ExecutorService databaseWriteExecutor =
            Executors.newFixedThreadPool(NUMBER_OF_THREADS);

    static EntryRoomDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (EntryRoomDatabase.class) {
                if (INSTANCE == null) {
                    Log.e("getDB", "getDB");
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            EntryRoomDatabase.class, "entry_database")
                            .build();

                    // .addCallback(sRoomDatabaseCallback)
                    databaseWriteExecutor.execute(() -> {
                        Log.e("refreshing", "before news Refresh");
                        refresh("news");
                    });
                    databaseWriteExecutor.execute(() -> {
                        Log.e("refreshing", "before news Refresh");
                        refresh("paper");
                    });

                }
            }
        }
        return INSTANCE;
    }

    static void refresh(String type) {
        EntryDao dao = INSTANCE.entryDao();
        String url = "https://covid-dashboard.aminer.cn/api/events/list";
        int total = getTotal("https://covid-dashboard.aminer.cn/api/events/list?type="+ type +"&page=1&size=5");
        int endPage = (int)Math.ceil((double)total / PAGE_SIZE);
        if ("news".equals(type)) {
            newsEndPage = endPage - 2;
        } else {
            paperEndPage = endPage - 2;
        }
        dao.insert(getEntries(url, endPage, PAGE_SIZE, type));
        dao.insert(getEntries(url, endPage - 1, PAGE_SIZE, type));
        if ("news".equals(type)) {
            lastNewsDate = null;
            newsLimitNumber = 0;
            Log.e("refreshing", "in news Refresh");
            EntryRepository.addMoreNews(INIT_SHOW_SIZE);
        } else {
            lastPaperDate = null;
            paperLimitNumber = 0;
            EntryRepository.addMorePaper(INIT_SHOW_SIZE);
        }
    }


    private static RoomDatabase.Callback sRoomDatabaseCallback = new RoomDatabase.Callback() {
        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db) {
            super.onCreate(db);
//            Log.e("in calling back", "in callback");
//            databaseWriteExecutor.execute(() -> {
//                Log.e("refreshing", "before news Refresh");
//                refresh("news");
//            });
//            databaseWriteExecutor.execute(() -> {
////                refresh("paper");
//            });
        }
    };

    private static Entry[] getEntries(String naive_url, int page, int size, String type) {
        String url = naive_url + "?type=" + type + "&page=" + page + "&size=" + size;
        String result = EntryRepository.getHTML(url);
        JSONArray jsonarr = JSON.parseObject(result).getJSONArray("data");
        List<Entry> list = EntryRepository.parseJSONArray(type, jsonarr);
        return list.toArray(new Entry[list.size()]);
    }

    private static int getTotal(String url) {
        String result = EntryRepository.getHTML(url);
        JSONObject json = JSON.parseObject(result).getJSONObject("pagination");
        return json.getInteger("total");
    }
    public static void getMore(String type) {
        Log.e("getMore", "entering getMore");
        EntryDao dao = INSTANCE.entryDao();
        String url = "https://covid-dashboard.aminer.cn/api/events/list";
        if ("news".equals(type)) {
            dao.insert(getEntries(url, newsEndPage, PAGE_SIZE, type));
            newsEndPage -= 1;
        } else {
            dao.insert(getEntries(url, paperEndPage, PAGE_SIZE, type));
            paperEndPage -= 1;
        }
    }

}