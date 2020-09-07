package com.java.renyi;

import androidx.recyclerview.widget.RecyclerView;

import com.java.renyi.db.Entry;
import com.java.renyi.db.EntryViewModel;

import java.util.List;

import butterknife.BindView;

/**
 * 知疫学者界面
 * 逻辑和新闻列表分类相同
 */
public class ScholarTagFragment extends BaseFragment {

    String currentTag;

    RecyclerView recyclerView;
    NewsListAdapter newsListAdapter;
    List<Entry> newsEntityList;
    EntryViewModel mEntryViewModel;


    @Override
    protected int inflateLayoutId() {
        return R.layout.fragment_scholar_tag;
    }
}
