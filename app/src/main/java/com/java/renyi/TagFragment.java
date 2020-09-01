package com.java.renyi;

import androidx.fragment.app.Fragment;

import com.scwang.smart.refresh.footer.ClassicsFooter;
import com.scwang.smart.refresh.header.ClassicsHeader;
import com.scwang.smart.refresh.layout.api.RefreshLayout;
import com.scwang.smart.refresh.layout.listener.OnLoadMoreListener;
import com.scwang.smart.refresh.layout.listener.OnRefreshListener;

import butterknife.BindView;

/**
 * 每个单独标签的新闻列表Fragment
 */
public class TagFragment extends BaseFragment {

    //    下拉刷新，上拉加载
    @BindView(R.id.refresh_layout)
    RefreshLayout refreshLayout;

    String currentTag;

    public TagFragment(String currentTag) {
        this.currentTag = currentTag;
    }

    //    Todo: 每个Tag的新闻列表内部格式有待完善
    public static Fragment newInstance(String currentTag) {
        return new TagFragment(currentTag);
    }

    @Override
    protected int inflateLayoutId() {
        return R.layout.fragment_tag;
    }

    protected void initView() {
        refreshLayout.setRefreshHeader(new ClassicsHeader(getActivity()));
        refreshLayout.setRefreshFooter(new ClassicsFooter(getActivity()));
        refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                refreshlayout.finishRefresh(2000/*,false*/);//传入false表示刷新失败
            }
        });
        refreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(RefreshLayout refreshlayout) {
                refreshlayout.finishLoadMore(2000/*,false*/);//传入false表示加载失败
            }
        });
    }
}
