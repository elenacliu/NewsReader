package com.java.renyi.db;

import android.app.Application;
import android.util.Log;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.java.renyi.db.Entry;
import com.java.renyi.db.PandemicStatus;

import java.util.List;

public class EntryViewModel extends AndroidViewModel {
    private com.java.renyi.db.EntryRepository mRepository;
    private MutableLiveData<List<Entry>> mNewsEntries;
    private MutableLiveData<List<Entry>> mPaperEntries;
    private MutableLiveData<List<Entry>> mSearchResult;
    private MutableLiveData<List<SearchEntity>> mSearchEntityList;
    private MutableLiveData<List<PandemicStatus>> mGlobalStatus;
    private MutableLiveData<List<PandemicStatus>> mDomesticStatus;

//    private LiveData<List<Entry>> mAllEntrys;

    public EntryViewModel (Application application) {
        super(application);
        Log.e("before repo", "Before");

        mRepository = new com.java.renyi.db.EntryRepository(application);
        Log.e("after repo", "Before");
//        mAllEntrys = mRepository.getAllEntrys();
        mNewsEntries = mRepository.getCurrentNewsEntrys();
        mPaperEntries = mRepository.getCurrentPaperEntrys();
        mSearchResult = mRepository.getmSearchResult();
        mSearchEntityList = mRepository.getSearchEntityList();
        mGlobalStatus = mRepository.getGlobalStatus();
        mDomesticStatus = mRepository.getDomesticStatus();
    }

    public MutableLiveData<List<Entry>> getCurrentNewsEntrys() {
        return mNewsEntries;
    }
    public MutableLiveData<List<Entry>> getCurrentPaperEntrys() {
        return mPaperEntries;
    }
    public MutableLiveData<List<Entry>> getSearchResult() {
        return mSearchResult;
    }
    public MutableLiveData<List<SearchEntity>> getSearchEntityList() {
        return mSearchEntityList;
    }
    public MutableLiveData<List<PandemicStatus>> getGlobalStatus() { return mGlobalStatus; }
    public MutableLiveData<List<PandemicStatus>> getDomesticStatus() { return mDomesticStatus; }


    public void addMorePaper() {
        com.java.renyi.db.EntryRepository.repoAddMorePaper();
    }
    public void addMoreNews() {
        com.java.renyi.db.EntryRepository.repoAddMoreNews();
    }
    public void refresh(String type) {
        mRepository.repoRefresh(type);
    }
    public void setViewed(String _id, String type) { mRepository.setViewed(_id, type);}
    public void search(String target) {
        mRepository.search(target);
    }
    public void searchEntity(String target) { mRepository.repoSearchEntity(target);}
    public void askGlobalStatus() { mRepository.repoAskGlobalStatus(); }
    public void askDomesticStatus() { mRepository.repoAskDomesticStatus(); }

//    LiveData<List<Entry>> getAllEntrys() { return mAllEntrys; }
//    public void insert(Entry entry) { mRepository.insert(entry); }
}