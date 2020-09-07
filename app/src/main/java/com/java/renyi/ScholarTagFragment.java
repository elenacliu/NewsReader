package com.java.renyi;

import android.content.Intent;
import android.util.Log;
import android.view.View;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.java.renyi.db.Entry;
import com.java.renyi.db.EntryViewModel;
import com.java.renyi.db.Scholar;

import java.util.List;

/**
 * 知疫学者界面
 * 逻辑和新闻列表分类相同
 */
public class ScholarTagFragment extends BaseFragment {

    String currentTag;
    RecyclerView recyclerView;
    ScholarListAdapter scholarListAdapter;
    EntryViewModel mEntryViewModel;

    public ScholarTagFragment() {
        this.currentTag = "其他";
    }

    public ScholarTagFragment(String currentTag) {
        this.currentTag = currentTag;
    }

    public static ScholarTagFragment newInstance(String currentTag) {
        return new ScholarTagFragment(currentTag);
    }
    @Override
    protected int inflateLayoutId() {
        return R.layout.fragment_scholar_tag;
    }

    @Override
    protected void initView(View rootView) {
        recyclerView = rootView.findViewById(R.id.recycler_view_scholar);
        scholarListAdapter = new ScholarListAdapter(getActivity());
        recyclerView.setAdapter(scholarListAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        mEntryViewModel = new ViewModelProvider(this).get(EntryViewModel.class);

        if (currentTag.equals("高关注学者")) {
            final Observer<List<Scholar>> nowScholarObserver = entries -> {
                Log.e("Scholar", entries.size()+"");
                scholarListAdapter.setScholarList(entries);
            };
            mEntryViewModel.getLivingScholar().observe(this, nowScholarObserver);
            mEntryViewModel.askLivingScholar();
        }
        else {
            final Observer<List<Scholar>> nowScholarObserver = entries -> {
                Log.e("Scholar", entries.size()+"");
                scholarListAdapter.setScholarList(entries);
            };
            mEntryViewModel.getPassedAwayScholar().observe(this, nowScholarObserver);
            mEntryViewModel.askPassedAwayScholar();
        }

        // 监听点击事件
        scholarListAdapter.setOnItemClickListener(new ScholarListAdapter.OnItemClickListener() {
            @Override
            public void OnItemClick(View view, Scholar scholar) {
                System.out.println("------------------------");
                Log.d("click", scholar.toString());
                System.out.println("------------------------");
                Intent intent = new Intent(getActivity(), ScholarDetailActivity.class);
                intent.putExtra("scholar", scholar);
                // 直接把值传进MainActivity ?
                startActivity(intent);
            }
        });
    }
}
