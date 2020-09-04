package com.java.renyi;

import android.nfc.Tag;
import android.util.Log;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.fragment.app.FragmentTransaction;

import java.util.ArrayList;

/**
 * FragmentPagerAdapter
 * 解决ViewPager与Fragment之间切换引起的问题
 * 每页都是一个Fragment，并且所有的Fragment实例一直保存在FragmentManager中
 */
public class HomePagerAdapter extends FragmentStatePagerAdapter {

    private ArrayList<String> tagList;      // 标题
    private ArrayList<TagFragment> fragmentList;       // fragment

    public HomePagerAdapter(FragmentManager fragmentManager, ArrayList<String> tagList,
                            ArrayList<TagFragment> fragmentList) {
        super(fragmentManager, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        Log.e("HomePagerAdapter", "-----Construct-----");
        this.tagList = tagList;
        this.fragmentList = fragmentList;
    }

    public void setFragmentList(ArrayList<TagFragment> fragmentList) {
        this.fragmentList = fragmentList;
    }

    public void setTagList(ArrayList<String> tagList) {
        this.tagList = tagList;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        return fragmentList.get(position);
    }

    @Override
    public int getCount() {
        return fragmentList.size();
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return tagList.get(position);
    }

}
