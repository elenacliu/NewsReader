package com.java.renyi;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.DefaultItemAnimator;

import android.widget.Toast;

import com.java.renyi.db.Entry;
import com.java.renyi.db.EntryViewModel;
import com.java.renyi.db.SearchEntity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import me.texy.treeview.TreeNode;
import me.texy.treeview.TreeView;
import me.texy.treeview.TreeViewAdapter;

/**
 * 搜索展示出的页面就在本页进行展示
 * 展示时显示recyclerview
 * 单层recyclerview实现点击展开效果
 */
public class EntitySearchActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private ViewGroup viewGroup;
    private TreeView treeView;
    private SearchView searchView;
    EntryViewModel mEntryViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_entity_search);

        initView();
        initTreeList();
    }

    private void initView() {
        toolbar = findViewById(R.id.toolbar_entity);
        toolbar.setTitle("疫情图谱实体搜索");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);//左侧添加一个默认的返回图标
        getSupportActionBar().setHomeButtonEnabled(true);

        searchView = findViewById(R.id.search_view_entity);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if (query == null) {
                    Toast.makeText(EntitySearchActivity.this, "请输入搜索内容", Toast.LENGTH_SHORT).show();
                }
                else {
                    Toast.makeText(EntitySearchActivity.this, query, Toast.LENGTH_SHORT).show();
                    mEntryViewModel.searchEntity(query);
                }
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        viewGroup = (RelativeLayout) findViewById(R.id.container);
        mEntryViewModel = new ViewModelProvider(this).get(EntryViewModel.class);
    }

    private void initTreeList() {
        MyNodeViewFactory myNodeViewFactory = new MyNodeViewFactory();
        final Observer<List<SearchEntity>> nowSearchObserver = entries -> {
            treeView = new TreeView(buildTree(entries), this, myNodeViewFactory);
            treeView.setItemAnimator(null);
            treeView.refreshTreeView();
            View view = treeView.getView();
            // 将自定义视图装入其中
            view.setLayoutParams(new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            viewGroup.removeAllViews();
            viewGroup.addView(view);
            Log.e("searchEntityLength", entries.size()+"");
            if (entries.size() > 0)
                Log.e("searchEntity[0]", entries.get(0).toString());
        };
        mEntryViewModel.getSearchEntityList().observe(this, nowSearchObserver);
//        可以直接调用treeview的
        /**
         * public void refreshTreeView() {
         *         if (rootView != null) {
         *             ((TreeViewAdapter) rootView.getAdapter()).refreshView();
         *         }
         *     }
         */

    }

    /**
     * 构建出生成多层所需要的树结构
     * @param entries 返回的词条
     * @return
     */
    private TreeNode buildTree(List<SearchEntity> entries) {
        TreeNode root = TreeNode.root();
        for (SearchEntity entry: entries) {
            // 一级父节点
            List<Object> firstLevel = new ArrayList<>();
            firstLevel.add(entry.getLabel());
            firstLevel.add(entry.getHot());
            System.out.println("----------");
            System.out.println("Label: "+entry.getLabel()+ ", Hot: "+entry.getHot());
            System.out.println("----------");
            TreeNode parent = new TreeNode(firstLevel);
            parent.setLevel(0);
            root.addChild(parent);

            // 子节点: 描述
            if (entry.getIntro() != null && !entry.getIntro().equals("")
                    && entry.getImg() != null && !entry.getImg().equals("")) {
                TreeNode child = new TreeNode("描述");
                child.setLevel(1);
                System.out.println("----------");
                System.out.println("描述");
                System.out.println("----------");
                parent.addChild(child);
                // 孙子节点描述信息
                List<Object> thirdLevel = new ArrayList<>();
                thirdLevel.add(entry.getIntro());
                thirdLevel.add(entry.getImg());
                System.out.println("----------");
                System.out.println("Intro: "+entry.getIntro()+ ", Img: "+entry.getImg());
                System.out.println("----------");
                TreeNode grandchild = new TreeNode(thirdLevel);
                grandchild.setLevel(2);
                child.addChild(grandchild);
            }

            // 子节点: 关系
            List<HashMap<String, String>> hashMapList = entry.getRelation();
            TreeNode child = new TreeNode("关系");
            child.setLevel(1);
            parent.addChild(child);
            // 判断孙子节点是否都有内容
            boolean hasRelation = false;
            int cnt = 0;        // 统计非空的relation项数
            for (HashMap<String, String> hashMap: hashMapList) {
                // 实际上，取hashmap的第一项即可, 因为一定是一致的
                if (hashMap.get("forward") != null && cnt <= 20) {
                    hasRelation = true;
                    cnt++;
                    TreeNode grandchild = new TreeNode(hashMap);
                    grandchild.setLevel(2);
                    System.out.println("----------");
                    System.out.println(SearchEntity.showMap(hashMap));
                    System.out.println("----------");
                    child.addChild(grandchild);
                }
            }
            if (!hasRelation)
                parent.removeChild(child);

            // 子节点：属性
            HashMap<String, String> hashMap = entry.getProperty();
            System.out.println("----------");
            System.out.println(SearchEntity.showMap(hashMap));
            System.out.println("----------");
            // 当且仅当map中有内容
            if (hashMap.size() > 0) {
                child = new TreeNode("属性");
                child.setLevel(1);
                parent.addChild(child);
                for (Map.Entry<String, String> e: hashMap.entrySet()) {
                    List<Object> thirdLevel = new ArrayList<>();
                    thirdLevel.add(e.getKey());
                    thirdLevel.add(e.getValue());
                    thirdLevel.add(1);
                    TreeNode grandchild = new TreeNode(thirdLevel);
                    grandchild.setLevel(2);
                    child.addChild(grandchild);
                }
            }
        }
        return root;
    }
}