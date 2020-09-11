package com.java.renyi;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.app.SearchManager;
import android.content.Intent;
import android.content.SearchRecentSuggestionsProvider;
import android.os.Bundle;
import android.provider.SearchRecentSuggestions;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.java.renyi.db.Entry;
import com.java.renyi.db.EntryViewModel;

import java.util.ArrayList;
import java.util.List;

// 参考：https://developer.android.google.cn/guide/topics/search/adding-recent-query-suggestions?hl=zh-cn#SavingQueries

public class SearchActivity extends AppCompatActivity {

    private Toolbar toolbar;    // 自定义顶部
    private RecyclerView recyclerView;
    private SearchNewsListAdapter newsListAdapter;
    private List<Entry> newsEntityList;
    private EntryViewModel mEntryViewModel;
    private String query;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        initView();
        // Get the intent, verify the action and get the query
        Intent intent = getIntent();
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            SearchRecentSuggestions suggestions = new SearchRecentSuggestions(this,
                    MySuggestionProvider.AUTHORITY, MySuggestionProvider.MODE);
            suggestions.saveRecentQuery(query, null);
//            this.query = query;
            doMySearch(query);
        }

    }

    private void initView() {
        toolbar = findViewById(R.id.toolbar_search);
        toolbar.setTitle("搜索");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);//左侧添加一个默认的返回图标
        getSupportActionBar().setHomeButtonEnabled(true);

        // 获取recyclerview
        recyclerView = findViewById(R.id.recycler_view_search);
        // 创建adapter
        newsListAdapter = new SearchNewsListAdapter(SearchActivity.this);
        newsListAdapter.setNewsEntityList(newsEntityList);
        // recyclerview与adapter绑定
        recyclerView.setAdapter(newsListAdapter);
        // 设置layoutManager
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        mEntryViewModel = new ViewModelProvider(this).get(EntryViewModel.class);

        final Observer<List<Entry>> nowSearchObserver = entries -> {
            newsListAdapter.setNewsEntityList(entries);
            Log.e("searchResultLength", entries.size()+"");
            if (entries.size() > 0)
                Log.e("searchResult[0]", entries.get(0).toString());
        };

        mEntryViewModel.getSearchResult().observe(this, nowSearchObserver);

        // 监听点击事件
        newsListAdapter.setOnItemClickListener(new SearchNewsListAdapter.OnItemClickListener() {
            @Override
            public void OnItemClick(View view, Entry news) {
                System.out.println("------------------------");
                Log.d("click", news.toString());
                System.out.println("------------------------");
                Intent intent = new Intent(SearchActivity.this, NewsDetailActivity.class);
                intent.putExtra("news", news);
                // 直接把值传进MainActivity ?
                startActivityForResult(intent, Constants.UPDATE_REQUEST);
            }
        });
    }

    private void doMySearch(String query) {
        if (query == null || query.equals("")) {
            Toast.makeText(this, "请输入搜索内容", Toast.LENGTH_SHORT).show();
        }
        else {
            Toast.makeText(this, "正在搜索\""+query+"\"", Toast.LENGTH_SHORT).show();
            mEntryViewModel.search(query);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
//        newsListAdapter.notifyDataSetChanged();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // 请求变灰
        if (requestCode == Constants.UPDATE_REQUEST) {
            if (resultCode == Activity.RESULT_OK) {
                assert data != null;
                Log.e("requesting grey", (data.getStringExtra("title")));
                // 更新后端
                mEntryViewModel.setViewed(data.getStringExtra("id"), data.getStringExtra("type"));
            }
        }
    }
}