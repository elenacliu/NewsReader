package com.example.newsreader;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.widget.SearchView;
import android.widget.Toast;

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
            doMySearch(query);
        }
        // TODO: 添加历史搜索记录，以及清空
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
        // TODO: search
    }


}