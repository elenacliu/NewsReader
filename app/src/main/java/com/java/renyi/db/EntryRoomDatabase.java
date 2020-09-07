package com.java.renyi.db;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(entities = {Entry.class}, version = 1, exportSchema = false)
public abstract class EntryRoomDatabase extends RoomDatabase {
    public abstract EntryDao entryDao();
    private static volatile EntryRoomDatabase INSTANCE;

    private static final int NUMBER_OF_THREADS = 10;
    static final ExecutorService databaseWriteExecutor =
            Executors.newFixedThreadPool(NUMBER_OF_THREADS);

    static EntryRoomDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (EntryRoomDatabase.class) {
                Log.e("getting db", "before");
                if (INSTANCE == null) {
                    Log.e("getDB", "getDB");
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            EntryRoomDatabase.class, "entry_database")
                            .build();
                    // TODO: CALL BACK?
                }
            }
        }
        return INSTANCE;
    }

    private static RoomDatabase.Callback sRoomDatabaseCallback = new RoomDatabase.Callback() {
        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db) {
            super.onCreate(db);
        }
    };

}