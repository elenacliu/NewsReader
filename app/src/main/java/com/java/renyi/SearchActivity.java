package com.java.renyi;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.SearchManager;
import android.content.Intent;
import android.content.SearchRecentSuggestionsProvider;
import android.os.Bundle;
import android.provider.SearchRecentSuggestions;
import android.widget.Toast;

// 参考：https://developer.android.google.cn/guide/topics/search/adding-recent-query-suggestions?hl=zh-cn#SavingQueries
public class SearchActivity extends AppCompatActivity {

    private Toolbar toolbar;    // 自定义顶部

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
            doMySearch(query);
        }
    }

    private void initView() {
        toolbar = findViewById(R.id.toolbar_search);
        setSupportActionBar(toolbar);
    }

    private void doMySearch(String query) {
        if (query == null) {
            Toast.makeText(this, "请输入搜索内容", Toast.LENGTH_SHORT).show();
        }
        else {
            Toast.makeText(this, query, Toast.LENGTH_SHORT).show();
        }
        // TODO: search things in sub thread
    }

    @Override
    protected void onResume() {
        super.onResume();
        // mAdapterNews.notifyDataSetChanged(); ?
    }
}