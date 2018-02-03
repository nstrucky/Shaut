package com.ventoray.shaut.ui.util;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.ventoray.shaut.ui.fragment.PageFragment;

/**
 * Created by Nick on 1/29/2018.
 */

public class MainActivityPagerAdapter extends FragmentPagerAdapter {

    private final int NUM_PAGES = 4;
    private Context context;

    public MainActivityPagerAdapter(FragmentManager fm, Context context) {
        super(fm);
        this.context = context;
    }



    @Override
    public CharSequence getPageTitle(int position) {

        return null;
    }

    @Override
    public Fragment getItem(int position) {
        return PageFragment.newInstance(position);
    }

    @Override
    public int getCount() {
        return NUM_PAGES;
    }



}
