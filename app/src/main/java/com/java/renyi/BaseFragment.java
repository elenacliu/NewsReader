package com.java.renyi;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import butterknife.ButterKnife;
import butterknife.Unbinder;


/**
 * 所有Fragment的抽象基类
 * 为方便理解Fragment的生命周期，向控制台输出相关信息
 */
public abstract class BaseFragment extends Fragment {

    protected Context context;
    protected boolean isVisible;
    private View rootView;
    Unbinder unbinder;

//    Todo: Fragment懒加载，性能提升

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
        prepare();
        Log.e("onAttach", "onAttach");
    }

    protected abstract int inflateLayoutId();

    /**
     * 根据Fragment生命周期的执行过程，初始化ID和控件点击事件，一定要放在Fragment的onActivityCreated方法中
     * onCreateView中只是进行根节点rootView的初始化
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if(rootView == null)  {
            rootView = inflater.inflate(inflateLayoutId(), container, false);
            unbinder = ButterKnife.bind(this, rootView);
            initView(rootView);
        }
        Log.e("onCreateView", "onCreateView");
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.e("onResume", "onResume");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.e("onDestroy", "onDestroy");
        if(unbinder != null)
            unbinder.unbind();
    }

    protected void initView(View rootView) {

    }
    protected void prepare() {

    }
    public View getRootView() {
        return rootView;
    }
}
