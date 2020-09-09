package com.java.renyi.db;

import android.app.Application;
import android.util.Log;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.List;
import java.util.ListIterator;

public class EntryViewModel extends AndroidViewModel {
    static private EntryRepository mRepository;
    private MutableLiveData<List<Entry>> mNewsEntries;
    private MutableLiveData<List<Entry>> mPaperEntries;
    private MutableLiveData<List<Entry>> mSearchResult;
    private MutableLiveData<List<SearchEntity>> mSearchEntityList;
    private MutableLiveData<List<PandemicStatus>> mGlobalStatus;
    private MutableLiveData<List<PandemicStatus>> mDomesticStatus;
    private LiveData<List<Entry>> researchCluster;
    private LiveData<List<Entry>> medicineCluster;
    private LiveData<List<Entry>> pandemicCluster;
    private LiveData<List<Entry>> treatmentCluster;

    private MutableLiveData<List<Scholar>> livingScholar;
    private MutableLiveData<List<Scholar>> passedAwayScholar;

//    private LiveData<List<Entry>> mAllEntrys;

    public EntryViewModel (Application application) {
        super(application);
        Log.e("before repo", "Before");
        if (mRepository == null)
            mRepository = new EntryRepository(application);
        Log.e("after repo", "Before");
//        mAllEntrys = mRepository.getAllEntrys();
        mNewsEntries = mRepository.getCurrentNewsEntrys();
        mPaperEntries = mRepository.getCurrentPaperEntrys();
        mSearchResult = mRepository.getmSearchResult();
        mSearchEntityList = mRepository.getSearchEntityList();
        mGlobalStatus = mRepository.getGlobalStatus();
        mDomesticStatus = mRepository.getDomesticStatus();
        livingScholar = mRepository.getLivingScholar();
        passedAwayScholar = mRepository.getPassedAwayScholar();
        researchCluster = mRepository.getResearchCluster();
        medicineCluster = mRepository.getMedicineCluster();
        pandemicCluster = mRepository.getPandemicCluster();
        treatmentCluster = mRepository.getTreatmentCluster();
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
    public MutableLiveData<List<Scholar>> getLivingScholar() { return livingScholar; }
    public MutableLiveData<List<Scholar>> getPassedAwayScholar() { return passedAwayScholar; }


    public void addMorePaper() {
        EntryRepository.repoAddMorePaper();
    }
    public void addMoreNews() {
        EntryRepository.repoAddMoreNews();
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
    public void askLivingScholar() { mRepository.repoAskLivingScholar(); }
    public void askPassedAwayScholar() { mRepository.repoAskPassedAwayScholar(); }

    public LiveData<List<Entry>> getResearchCluster() {
        return researchCluster;
    }

    public LiveData<List<Entry>> getMedicineCluster() {
        return medicineCluster;
    }

    public LiveData<List<Entry>> getPandemicCluster() {
        return pandemicCluster;
    }

    public void addMoreEvents() {
        mRepository.addMoreEvents();
    }

    public LiveData<List<Entry>> getTreatmentCluster() {
        return treatmentCluster;
    }
    //    LiveData<List<Entry>> getAllEntrys() { return mAllEntrys; }
//    public void insert(Entry entry) { mRepository.insert(entry); }
}