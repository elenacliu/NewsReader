package com.java.renyi;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.TextView;

import java.util.ArrayList;

public class TagEditActivity extends AppCompatActivity {

    private ArrayList<String> tags;
    private ArrayList<String> delTags;
    private DragGridLayout addTagLayout, deleteTagLayout;
    private int count;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tag_edit);
        initView();

        Intent intent = getIntent();    // intent用于数据传递
        // 获取MainActivity中传递过来的初始数据
        tags = (ArrayList<String>) intent.getSerializableExtra("tags");
        delTags = (ArrayList<String>) intent.getSerializableExtra("delTags");

        System.out.println(tags);
        System.out.println(delTags);

        bindData();
        initEvent();

    }

    private void bindData() {
        addTagLayout.setItems(tags);
        deleteTagLayout.setItems(delTags);
    }

    private void initView() {
        addTagLayout = findViewById(R.id.add_tag);
        deleteTagLayout = findViewById(R.id.delete_tag);
        addTagLayout.setAllowDrag(true);
        deleteTagLayout.setAllowDrag(true);
    }

    private void initEvent() {
        deleteTagLayout.setOnDragItemClickListener(new DragGridLayout.OnDragItemClickListener() {
            @Override
            public void onDragItemClick(TextView tv) {
                deleteTagLayout.removeView(tv);     // 移除需要时间，不能直接添加
                addTagLayout.addItem(tv.getText().toString(), 0);
                // 可以在这里对tagList进行操作吗？
            }
        });

        addTagLayout.setOnDragItemClickListener(new DragGridLayout.OnDragItemClickListener() {
            @Override
            public void onDragItemClick(TextView tv) {
                addTagLayout.removeView(tv);
                deleteTagLayout.addItem(tv.getText().toString());
            }
        });
    }

    public void addItem(View view) {
        // 获取页面数据，修改
        getSortedTags();

        Log.e("back","clicked");
//        Toast.makeText(this, "Back", Toast.LENGTH_SHORT).show();
        Intent intent = getIntent();
        intent.putExtra("tags", tags);
        intent.putExtra("delTags", delTags);
        setResult(Activity.RESULT_OK, intent);
        onBackPressed();
    }
    // 遍历得到排好序的元素
    // 遍历ViewGroup
    private void getSortedTags() {
        tags = new ArrayList<>();
        delTags = new ArrayList<>();
        ViewGroup viewGroup = getWindow().getDecorView().findViewById(android.R.id.content);
        ViewGroup rootView = (ViewGroup) viewGroup.getChildAt(0);

        count = 0;
        traversalView(rootView);
        System.out.println(tags);
        System.out.println(delTags);
    }

    private void traversalView(ViewGroup rootView) {
        Log.d("Traversal", "start rootView: " + rootView);
        for(int i = 0; i < rootView.getChildCount(); i++) {
            View childVg = rootView.getChildAt(i);
            if(childVg instanceof GridLayout) {
                ++count;
            }
            if(childVg instanceof ViewGroup) {
                traversalView((ViewGroup) childVg);
            }
            else if(childVg instanceof TextView && rootView instanceof GridLayout) {
                String text = ((TextView) childVg).getText().toString();
                if(count == 1) {
                    tags.add(text);
                }
                else if(count == 2) {
                    delTags.add(text);
                }
            }
        }
    }

}