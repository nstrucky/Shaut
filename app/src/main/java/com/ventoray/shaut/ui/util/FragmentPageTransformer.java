package com.ventoray.shaut.ui.util;

import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.ViewPager;
import android.view.View;

import com.ventoray.shaut.R;

/**
 * Created by Nick on 2/3/2018.
 */

public class FragmentPageTransformer implements ViewPager.PageTransformer{


    @Override
    public void transformPage(@NonNull View page, float position) {


        if (position < -1) { // [-Infinity, 1]

        } else if (position <= 1) { //[-1, 1]
            FloatingActionButton fab = page.findViewById(R.id.fab_newShaut);
            fab.setRotation(position * 360f);

        } else { //[1, +Infinity]
        }

    }
}
