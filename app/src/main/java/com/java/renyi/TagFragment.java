package com.java.renyi;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.java.renyi.db.Entry;
import com.java.renyi.db.EntryViewModel;
import com.scwang.smart.refresh.footer.ClassicsFooter;
import com.scwang.smart.refresh.header.ClassicsHeader;
import com.scwang.smart.refresh.layout.api.RefreshLayout;
import com.scwang.smart.refresh.layout.listener.OnLoadMoreListener;
import com.scwang.smart.refresh.layout.listener.OnRefreshListener;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import butterknife.BindView;

/**
 * 每个单独标签的新闻列表Fragment
 */

public class TagFragment extends BaseFragment {
    // 下拉刷新，上拉加载
    @BindView(R.id.refresh_layout)
    RefreshLayout refreshLayout;

    String currentTag;

    RecyclerView recyclerView;
    NewsListAdapter newsListAdapter;
    List<Entry> newsEntityList;
    EntryViewModel mEntryViewModel;

    public TagFragment() {
        this.currentTag = "其他";
    }

    public TagFragment(String currentTag) {
        this.currentTag = currentTag;
    }

    public static TagFragment newInstance(String currentTag) {
        return new TagFragment(currentTag);
    }

    @Override
    protected int inflateLayoutId() {
        return R.layout.fragment_tag;
    }

    protected void initView(View view) {
        // 获取recylerView
        recyclerView = view.findViewById(R.id.recycler_view);
        // 创建adapter
        newsListAdapter = new NewsListAdapter(getActivity());
        // 先创建完adapter再设置（更改）newsEntityList
//        newsListAdapter.setNewsEntityList(newsEntityList);
        // recyclerView设置adapter
        recyclerView.setAdapter(newsListAdapter);
        // 设置layoutManager
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        mEntryViewModel = new ViewModelProvider(this).get(EntryViewModel.class);

        if (currentTag.equals("paper")) {
            final Observer<List<Entry>> nowPaperEntryObserver = entries -> {
                Log.e("PaperToFront", entries.size()+"");
                // TODO: Print entries, when content is null
                Iterator<Entry> iterator = entries.iterator();
                while (iterator.hasNext()) {
                    Entry entry = iterator.next();
                    if (entry.content == null || entry.content.equals("")) {
                        iterator.remove();
                    }
                }
                newsListAdapter.setNewsEntityList(entries);
                Log.e("PaperToFrontFinal", entries.size()+"");
            };
            mEntryViewModel.getCurrentPaperEntrys().observe(this, nowPaperEntryObserver);
        }
        else if (currentTag.equals("news")) {
            final Observer<List<Entry>> nowNewsEntryObserver = entries -> {
                Log.e("NewsToFront: ", entries.size()+"");
                if (entries.size() > 0) {
                    Entry now = entries.get(0);
                    for (int i = 0;i < now.urls.size(); i++) {
                        Log.e("url"+i, now.urls.get(i));
                    }
                }

                Iterator<Entry> iterator = entries.iterator();
                while (iterator.hasNext()) {
                    Entry entry = iterator.next();
                    if (entry.content == null || entry.content.equals("")) {
                        iterator.remove();
                    }
                }
                newsListAdapter.setNewsEntityList(entries);
                Log.e("NewsToFrontFinal", entries.size()+"");
            };
            mEntryViewModel.getCurrentNewsEntrys().observe(this, nowNewsEntryObserver);
        }

        else if (currentTag.equals("病毒研究")) {
            mEntryViewModel.getResearchCluster().observe(this, entries -> {
                Iterator<Entry> iterator = entries.iterator();
                while (iterator.hasNext()) {
                    Entry entry = iterator.next();
                    if (entry.content == null) {
                        iterator.remove();
                    }
                }

                newsListAdapter.setNewsEntityList(entries);
                Log.e("researchCluster ", entries.size()+"");
            });
        }

        else if (currentTag.equals("疫苗药物")) {
            mEntryViewModel.getMedicineCluster().observe(this, entries -> {

                Iterator<Entry> iterator = entries.iterator();
                while (iterator.hasNext()) {
                    Entry entry = iterator.next();
                    if (entry.content == null || entry.content.equals("")) {
                        iterator.remove();
                    }
                }
                newsListAdapter.setNewsEntityList(entries);
                Log.e("medicineCluster ", entries.size()+"");
            });
        }

        else if (currentTag.equals("疫情形势")) {
            mEntryViewModel.getPandemicCluster().observe(this, entries -> {
                Iterator<Entry> iterator = entries.iterator();
                while (iterator.hasNext()) {
                    Entry entry = iterator.next();
                    if (entry.content == null) {
                        iterator.remove();
                    }
                }
                newsListAdapter.setNewsEntityList(entries);
                Log.e("pandemicCluster ", entries.size()+"");
            });
        }
        else if (currentTag.equals("患者治疗")) {
            mEntryViewModel.getTreatmentCluster().observe(this, entries -> {
                Iterator<Entry> iterator = entries.iterator();
                while (iterator.hasNext()) {
                    Entry entry = iterator.next();
                    if (entry.content == null) {
                        iterator.remove();
                    }
                }
                newsListAdapter.setNewsEntityList(entries);
                Log.e("treatmentCluster", entries.size()+"");
            });
        }

        // 监听点击事件
        newsListAdapter.setOnItemClickListener(new NewsListAdapter.OnItemClickListener() {
            @Override
            public void OnItemClick(View view, Entry news) {
                System.out.println("------------------------");
                Log.d("click", news.toString());
                System.out.println("------------------------");
                Intent intent = new Intent(getActivity(), NewsDetailActivity.class);
                intent.putExtra("news", news);
                // 直接把值传进MainActivity ?
                startActivityForResult(intent, Constants.UPDATE_REQUEST);
            }
        });


        refreshLayout.setRefreshHeader(new ClassicsHeader(getActivity()));
        refreshLayout.setRefreshFooter(new ClassicsFooter(getActivity()));
        // 注意每次实施onfresh判断一下网络连接状态
        refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                if (!NetworkUtil.isNetworkAvailable(getActivity())) {
                    Toast.makeText(getActivity(), "offline",Toast.LENGTH_SHORT).show();
                }
                else {
                    Toast.makeText(getActivity(), "online", Toast.LENGTH_SHORT).show();
                    if (currentTag.equals("paper")) {
                        mEntryViewModel.refresh("paper");
                    }
                    else if (currentTag.equals("news")) {
                        mEntryViewModel.refresh("news");
                    }
                }
                refreshlayout.finishRefresh(2000/*,false*/);//传入false表示刷新失败
            }
        });

        // TODO: get op3，上拉加载，获取更多新闻
        refreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(RefreshLayout refreshlayout) {
                if (currentTag.equals("paper")) {
                    mEntryViewModel.addMorePaper();
                }
                else if (currentTag.equals("news")) {
                    mEntryViewModel.addMoreNews();
                } else if (currentTag.equals("病毒研究") || currentTag.equals("疫苗药物") || currentTag.equals("疫情形势") || currentTag.equals("患者治疗")) {
                    mEntryViewModel.addMoreEvents();
                }
                refreshlayout.finishLoadMore(2000/*,false*/);//传入false表示加载失败
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // 请求变灰
        if (requestCode == Constants.UPDATE_REQUEST) {
            if (resultCode == Activity.RESULT_OK) {
//                assert data != null;
                mEntryViewModel.setViewed(data.getStringExtra("id"), currentTag);
            }
        }
    }

    /**
     * TODO: load more?
     */
    public void onResume() {
        super.onResume();
        Log.e("onResume", currentTag +" onResume");
    }

    @Override
    public String toString() {
        return "TagFragment{" +
                "refreshLayout=" + refreshLayout +
                ", currentTag='" + currentTag + '\'' +
                ", recyclerView=" + recyclerView +
                ", newsListAdapter=" + newsListAdapter +
                ", newsEntityList=" + newsEntityList +
                ", mEntryViewModel=" + mEntryViewModel +
                '}';
    }
}
