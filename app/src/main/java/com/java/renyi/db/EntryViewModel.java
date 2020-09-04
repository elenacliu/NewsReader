package com.java.renyi.db;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import java.util.List;

public class EntryViewModel extends AndroidViewModel {
    private final static int ADD_SIZE = 10;
    private EntryRepository mRepository;
    private MutableLiveData<List<Entry>> mNewsEntries;
    private MutableLiveData<List<Entry>> mPaperEntries;
    private MutableLiveData<List<Entry>> mSearchResult;

//    private LiveData<List<Entry>> mAllEntrys;

    public EntryViewModel (Application application) {
        super(application);
        mRepository = new EntryRepository(application);
//        mAllEntrys = mRepository.getAllEntrys();
        mNewsEntries = mRepository.getCurrentNewsEntrys();
        mPaperEntries = mRepository.getCurrentPaperEntrys();
        mSearchResult = mRepository.getmSearchResult();
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


    public void addMorePaper() {
        EntryRepository.addMorePaper(ADD_SIZE);
    }
    public void addMoreNews() {
        EntryRepository.addMoreNews(ADD_SIZE);
    }
    public void refresh(String type) {
        mRepository.refresh(type);
    }
    public void setViewed(String _id, String type) { mRepository.setViewed(_id, type);}
    public void search(String target) {
        mRepository.search(target);
    }
//    LiveData<List<Entry>> getAllEntrys() { return mAllEntrys; }
//    public void insert(Entry entry) { mRepository.insert(entry); }
}