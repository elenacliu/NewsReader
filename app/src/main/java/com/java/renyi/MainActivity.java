package com.java.renyi;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import android.app.Activity;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.SearchView;
import android.widget.Toast;
//import androidx.appcompat.widget.SearchView;

import com.google.android.material.tabs.TabLayout;
import com.mikepenz.google_material_typeface_library.GoogleMaterial;
import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.interfaces.OnCheckedChangeListener;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.ProfileDrawerItem;
import com.mikepenz.materialdrawer.model.SwitchDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;

import java.util.ArrayList;

import static androidx.appcompat.app.AppCompatDelegate.setDefaultNightMode;


public class MainActivity extends AppCompatActivity {

    // 顶部导航栏和抽屉
    private Drawer drawer;      // 自定义的drawer布局
    private Toolbar toolbar;    // 自定义顶部
    // 中间的滑动切换视图
    private ViewPager viewPager;
    private TabLayout tabLayout;
    // 标签页数据和Fragment数据（暂定，按理要和TagSetting Activity做通信）
    // TODO: 该数据得存入数据库中
    private ArrayList<String> tags = new ArrayList<>();
    private ArrayList<String> delTags = new ArrayList<>();
    private ArrayList<Fragment> fragments = new ArrayList<>();
    // 适配器
    private HomePagerAdapter homePagerAdapter;
    private ImageButton imageButton;
    // 搜索栏
    private SearchView searchView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();
        bindData();
        setImageButtonClickListener();
    }

    // 监听ImageButton点击事件，跳转到TagEdit事件
    private void setImageButtonClickListener() {
        imageButton = findViewById(R.id.tag_setting_button);
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, TagEditActivity.class);
                // 将本activity中的标签传输给TagEditActivity，以便TagEditActivity做展示
                intent.putExtra("tags", tags);
                intent.putExtra("delTags", delTags);
                // 如果想获得其他activity关闭后返回的数据
                // 需要使用系统提供的startActivityForResult(Intent intent, int requestCode)方法打开新的activity
                // 新的Activity 关闭后会向前面的Activity传回数据
                // 为了得到传回的数据
                // 必须在前面的Activity中重写onActivityResult(int requestCode, int resultCode, Intent data)方法
                startActivityForResult(intent, Constants.EDIT_TAG_REQUEST);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Constants.EDIT_TAG_REQUEST) {
            // 成功修改Tag
            if (resultCode == Activity.RESULT_OK) {
                assert data != null;
                tags = (ArrayList<String>) data.getSerializableExtra("tags");
                delTags = (ArrayList<String>) data.getSerializableExtra("delTags");
                // 一定要清空吗？
                fragments.clear();
                for(String s: tags) {
                    fragments.add(TagFragment.newInstance(s));
                }
                homePagerAdapter = new HomePagerAdapter(getSupportFragmentManager(), tags, fragments);
                viewPager.setAdapter(homePagerAdapter);
                tabLayout.setupWithViewPager(viewPager);
            }
        }

    }

    private void initView() {
        toolbar = findViewById(R.id.toolbar_home);
        setSupportActionBar(toolbar);
        // 初始化视图
        viewPager = findViewById(R.id.viewpager_news_list);
        tabLayout = findViewById(R.id.tab_news_list);
        // 初始化drawer
        // TODO: 用户头像和用户名需要从服务器得到
        AccountHeader accountHeader = new AccountHeaderBuilder()
                .withActivity(this)
                .addProfiles(
                        new ProfileDrawerItem().withName("Tsinghua University")
                        .withIcon(R.drawable.tsinghua)
                        .withEmail("thu@tsinghua.mail.edu.cn"),
                        new ProfileDrawerItem().withName("Peking University")
                        .withIcon(R.drawable.peking)
                        .withEmail("pku@mail.pku.edu.cn")
                )
                .withHeaderBackground(R.drawable.header)
                .withTranslucentStatusBar(true)
                .build();
        SwitchDrawerItem itemNightMode = new SwitchDrawerItem().withName("夜间模式")
                .withIcon(GoogleMaterial.Icon.gmd_brightness_6)
                .withIdentifier(Constants.NIGHT_MODE_IDENTIFIER)
                .withSelectable(false)
                .withOnCheckedChangeListener(new OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(IDrawerItem drawerItem, CompoundButton buttonView, boolean isChecked) {
                        if (isChecked) {
                            setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                        }
                        else
                            setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                        Toast.makeText(MainActivity.this, "设置夜间模式需要重启软件", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(MainActivity.this, MainActivity.class);
                        // 小bug: 需要携带本页面的数据，比如tags & delTags
                        // 用户数据等等
//                        intent.putExtra("tags", tags);
//                        intent.putExtra("delTags", delTags);

                        startActivity(intent);
                        finish();
                    }
                });
        if(AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES)
            itemNightMode.withChecked(true);

        drawer = new DrawerBuilder().withActivity(this)
        .withToolbar(toolbar)
        .withAccountHeader(accountHeader)
        .withActionBarDrawerToggleAnimated(false)
        .addDrawerItems(
        new PrimaryDrawerItem().withName("首   页").withIcon(GoogleMaterial.Icon.gmd_home).withIdentifier(Constants.HOME_IDENTIFIER),
        new PrimaryDrawerItem().withName("我的收藏").withIcon(GoogleMaterial.Icon.gmd_favorite).withIdentifier(Constants.FAVORITE_IDENTIFIER),
        new PrimaryDrawerItem().withName("设置屏蔽").withIcon(GoogleMaterial.Icon.gmd_visibility_off).withIdentifier(Constants.SHIELD_IDENTIFIER),
        new PrimaryDrawerItem().withName("发布新闻").withIcon(GoogleMaterial.Icon.gmd_send).withIdentifier(Constants.RELEASE_IDENTIFIER),
        new PrimaryDrawerItem().withName("好友列表").withIcon(GoogleMaterial.Icon.gmd_people).withIdentifier(Constants.FRIREND_IDENTIFIER),  // 内含关注好友
        new PrimaryDrawerItem().withName("分享应用").withIcon(GoogleMaterial.Icon.gmd_share).withIdentifier(Constants.SHARE_IDENTIFIER),
        new PrimaryDrawerItem().withName("清除缓存").withIcon(GoogleMaterial.Icon.gmd_delete).withIdentifier(Constants.CLEAR_IDENTIFIER).withSelectable(false),
        itemNightMode,
        new DividerDrawerItem(),
        new PrimaryDrawerItem().withName("关    于").withIcon(GoogleMaterial.Icon.gmd_info).withIdentifier(Constants.ABOUT_IDENTIFIER)
//              还需要添加退出登录的功能
        )
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {

                    @Override
                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                        long identifier = drawerItem.getIdentifier();
                        if (identifier == Constants.CLEAR_IDENTIFIER) {
                            Toast.makeText(MainActivity.this, "清除缓存！", Toast.LENGTH_SHORT).show();
                            // TODO: 清除缓存
                        }
                        return false;
                    }
                })
        .build();
//        drawer.addStickyFooterItem(new PrimaryDrawerItem().withName("StickyFooterItem"));

    }


    private void bindData() {
        setTagList();
        fragments.clear();
        for(String s: tags) {
            fragments.add(TagFragment.newInstance(s));
        }
        // 这三行一行都不能省略
        homePagerAdapter = new HomePagerAdapter(getSupportFragmentManager(), tags, fragments);
        viewPager.setAdapter(homePagerAdapter);
        tabLayout.setupWithViewPager(viewPager);
    }

    private void setTagList() {
        // TODO: 暂时这样书写，实际上要获取相应Activity传来的值
        tags.add("推荐");
        tags.add("科技");
        tags.add("军事");
        tags.add("政治");
        tags.add("要闻");
        tags.add("体育");
        tags.add("教育");
        tags.add("民生");
        tags.add("娱乐");
        tags.add("其他");
    }

    // 初始化菜单栏，用Java代码创建SearchView (activity_main.xml中没有显式写入)
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // 显示menu中的search
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.options_menu, menu);

        // 将可搜索配置与SearchView关联，SearchView会在用户提交查询时使用ACTION_SEARCH intent启动SearchActivity
        // Associate searchable configuration with the SearchView
        SearchManager searchManager =
                (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchView =
                (SearchView) menu.findItem(R.id.action_search).getActionView();
        // 本句是打开新activity的关键
        searchView.setSearchableInfo(
                searchManager.getSearchableInfo(getComponentName()));

        return true;
    }
}