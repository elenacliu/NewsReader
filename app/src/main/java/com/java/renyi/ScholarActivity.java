package com.java.renyi;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;
import android.util.Log;

import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

/**
 * Scholar主activity
 */
public class ScholarActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private ArrayList<String> tags = new ArrayList<>();
    // 中间的滑动切换视图
    private ViewPager viewPager;
    private TabLayout tabLayout;
    private ArrayList<ScholarTagFragment> fragments = new ArrayList<>();
    private HomePagerAdapter2 homePagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scholar);

        initView();
        bindData();
    }

    private void addTags() {
        tags.add("高关注学者");
        tags.add("追忆学者");
    }

    private void initView() {
        toolbar = findViewById(R.id.toolbar_scholar);
        toolbar.setTitle("知疫学者");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);//左侧添加一个默认的返回图标
        getSupportActionBar().setHomeButtonEnabled(true);

        // 初始化视图
        viewPager = findViewById(R.id.viewpager_scholar_list);
        tabLayout = findViewById(R.id.tab_layout_scholar);
    }

    private void bindData() {
        addTags();
        fragments.clear();
        System.out.println("---------------");
        Log.e("bindData", tags.toString());
        System.out.println("---------------");
        for(String s: tags) {
            fragments.add(ScholarTagFragment.newInstance(s));
        }

        homePagerAdapter = new HomePagerAdapter2(getSupportFragmentManager(), tags, fragments);
        System.out.println("---------------");
        Log.e("homepageradapter", homePagerAdapter.toString());
        System.out.println("---------------");
        viewPager.setAdapter(homePagerAdapter);
        tabLayout.setupWithViewPager(viewPager);
    }
}