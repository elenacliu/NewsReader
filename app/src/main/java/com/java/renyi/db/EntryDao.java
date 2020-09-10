package com.java.renyi.db;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface EntryDao {

    // allowing the insert of the same entry multiple times by passing a
    // conflict resolution strategy
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insert(Entry... entry);

    @Query("SELECT * from entry_table WHERE cluster == \"病毒研究\"")
    LiveData<List<Entry>> getResearchCluster();

    @Query("SELECT * from entry_table WHERE cluster == \"疫苗药物\"")
    LiveData<List<Entry>> getMedicineCluster();

    @Query("SELECT * from entry_table WHERE cluster == \"疫情形势\"")
    LiveData<List<Entry>> getPandemicCluster();

    @Query("SELECT * from entry_table WHERE cluster == \"患者治疗\" ")
    LiveData<List<Entry>> getTreatmentCluster();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert_replace(Entry... entry);

    @Query("DELETE FROM entry_table")
    void deleteAll();

    @Query("SELECT * from entry_table ORDER BY time DESC")
    LiveData<List<Entry>> getAlphabetizedEntrys();

    @Query("SELECT * from entry_table ORDER BY time DESC")
    List<Entry> getAll();

    @Query("SELECT * from entry_table WHERE type == :type ORDER BY time DESC")
    List<Entry> getTypeTimeData(String type);

    @Query("SELECT * FROM entry_table WHERE type == :type and time <= :date ORDER BY time DESC, _id DESC LIMIT :limit")
    List<Entry> selectData(String type, String date, int limit);

    @Query("UPDATE entry_table SET viewed = :viewed WHERE _id = :currentID;")
    void setViewed(boolean viewed, String currentID);

    @Query("SELECT * FROM entry_table WHERE title LIKE :target OR content LIKE :target ORDER BY time DESC")
    List<Entry> search(String target);

    @Query("SELECT * FROM entry_table WHERE cluster == \"\"")
    Entry[] getNotClustered();

    @Update
    void update(Entry... entries);
}
