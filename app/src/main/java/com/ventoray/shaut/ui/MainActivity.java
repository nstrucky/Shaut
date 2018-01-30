package com.ventoray.shaut.ui;

import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ventoray.shaut.MainActivityPagerAdapter;
import com.ventoray.shaut.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {


    @BindView(R.id.viewPager_main) ViewPager viewPager;
    @BindView(R.id.tablayout) TabLayout tabLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        setUpViewPager();
    }


    private void setUpViewPager() {
        tabLayout.setTabMode(TabLayout.MODE_FIXED);
//        mTabLayout.setTabTextColors(getColor(R.color.colorPrimary), getColor(R.color.yellow));
        tabLayout.setupWithViewPager(viewPager);
        viewPager.setAdapter(
                new MainActivityPagerAdapter(getSupportFragmentManager(), this));

            tabLayout.getTabAt(0).setIcon(R.drawable.ic_public_white_24px);
            tabLayout.getTabAt(2).setIcon(R.drawable.ic_person_add_white_24px);
            tabLayout.getTabAt(3).setIcon(R.drawable.ic_chat_white_24px);

    }


}
