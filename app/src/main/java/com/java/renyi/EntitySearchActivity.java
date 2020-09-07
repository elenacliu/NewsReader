package com.java.renyi;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

/**
 * 搜索展示出的页面就在本页进行展示
 * 展示时显示recyclerview
 * 单层recyclerview实现点击展开效果
 */
public class EntitySearchActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private RecyclerView recyclerView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_entity_search);

        initView();
    }

    private void initView() {
        toolbar = findViewById(R.id.toolbar_entity);
        toolbar.setTitle("疫情图谱实体搜索");
        setSupportActionBar(toolbar);

        recyclerView = findViewById(R.id.recycler_view_entity);
    }
}