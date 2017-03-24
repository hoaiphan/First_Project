package com.example.gio.firstproject.viewpagers;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.gio.firstproject.R;
import com.example.gio.firstproject.adapter.ViewPagerInner3Adapter;

/**
 * Created by Gio on 3/23/2017.
 */

public class ViewPagerFragment3 extends Fragment {
    public ViewPagerFragment3() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.viewpager_fragment_3, container, false);

        ViewPager mViewPager = (ViewPager) view.findViewById(R.id.view_pager3);
        TabLayout mTabLayout = (TabLayout) view.findViewById(R.id.tab_layout3);

        ViewPagerInner3Adapter adapter = new ViewPagerInner3Adapter(getChildFragmentManager(), view.getContext());
        mViewPager.setAdapter(adapter);
        mTabLayout.setupWithViewPager(mViewPager);
        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(mTabLayout));
        mTabLayout.setTabsFromPagerAdapter(adapter);
        return view;
    }
}
