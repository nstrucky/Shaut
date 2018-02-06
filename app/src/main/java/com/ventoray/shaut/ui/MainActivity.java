package com.ventoray.shaut.ui;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.ventoray.shaut.ui.util.MainActivityPagerAdapter;
import com.ventoray.shaut.R;
import com.ventoray.shaut.ui.util.FragmentPageTransformer;
import com.ventoray.shaut.util.AutoCompleteHelper;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.ventoray.shaut.util.PreferenceHelper.PREF_SELECTED_CITY_ID;
import static com.ventoray.shaut.util.PreferenceHelper.getPreferenceValue;
import static com.ventoray.shaut.util.PreferenceHelper.savePreference;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    public static final int PLACE_AUTOCOMPLETE_REQUEST_CODE = 1001;

    public static final String LOG_TAG = "MainActivity";

    @BindView(R.id.viewPager_main) ViewPager viewPager;
    @BindView(R.id.tablayout) TabLayout tabLayout;
    @BindView(R.id.imageView_city) ImageView cityImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        setUpNavDrawer();
        setUpViewPager();
        AutoCompleteHelper.initializePlaceAutoComplete(this, fragmentPlaceListener);
        setUserData();


    }

    @Override
    protected void onStart() {
        super.onStart();
        checkCityPref();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PLACE_AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Place place = PlaceAutocomplete.getPlace(this, data);
                saveAndDisplayCity(place);
                Log.i(LOG_TAG, "Place: " + place.getName());
            } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                Status status = PlaceAutocomplete.getStatus(this, data);
                makeMoveSnackbar();
                Log.i(LOG_TAG, status.getStatusMessage());

            } else if (resultCode == RESULT_CANCELED) {
                // The user canceled the operation.
            }
        }
    }

    private void checkCityPref() {
        String cityId = (String) getPreferenceValue(this, PREF_SELECTED_CITY_ID);
        if (cityId == null) {
            makeMoveSnackbar();
            return;
        }
        AutoCompleteHelper.getPlacePhoto(this, cityId, cityImageView);
    }


    private void makeMoveSnackbar() {
        Snackbar.make(findViewById(R.id.container_app_bar),
                R.string.no_city_selected, Snackbar.LENGTH_INDEFINITE)
                .setAction(getString(R.string.move), new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        AutoCompleteHelper.startAutoCompleteActivity(
                                MainActivity.this,
                                PLACE_AUTOCOMPLETE_REQUEST_CODE
                        );
                    }
                }).show();
    }



    private void saveAndDisplayCity(Place place) {
        String cityId = place.getId();
        AutoCompleteHelper.getPlacePhoto(MainActivity.this,
                cityId, cityImageView);
        savePreference(MainActivity.this, PREF_SELECTED_CITY_ID, cityId);
    }

    PlaceSelectionListener fragmentPlaceListener = new PlaceSelectionListener() {
        @Override
        public void onPlaceSelected(Place place) {
            if (place == null) return;
            saveAndDisplayCity(place);

        }

        @Override
        public void onError(Status status) {
            Log.e("MainActivity", status.getStatusMessage());
        }
    };

    private void setUserData() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        NavigationView navigationView = findViewById(R.id.nav_view);
        View headerLayout = navigationView.getHeaderView(0);

        TextView userNameTextView = headerLayout.findViewById(R.id.textView_userName);

        TextView userEmailAddressTextView = headerLayout.findViewById(R.id.textView_emailAddress);
        userNameTextView.setText(user.getDisplayName());
        userEmailAddressTextView.setText(user.getEmail());
    }

    private void setUpNavDrawer() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);

        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    private void setUpViewPager() {
        tabLayout.setTabMode(TabLayout.MODE_FIXED);
        tabLayout.setupWithViewPager(viewPager);
        viewPager.setAdapter(
                new MainActivityPagerAdapter(getSupportFragmentManager(), this));

            tabLayout.getTabAt(0).setIcon(R.drawable.ic_search_white_24px);
            tabLayout.getTabAt(1).setIcon(R.drawable.ic_public_white_24px);
            tabLayout.getTabAt(2).setIcon(R.drawable.ic_person_add_white_24px);
            tabLayout.getTabAt(3).setIcon(R.drawable.ic_chat_white_24px);

            viewPager.setPageTransformer(true, new FragmentPageTransformer());

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        Intent intent;
        switch (id) {
            case R.id.nav_edit_profile:
                intent = new Intent(this, ProfileEditorActivity.class);
                startActivity(intent);
                break;

            case R.id.nav_logout:
                signOut();
                break;
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    /**
     * Signs the firebase authenticated user out of the application and TODO removes cached
     * data on phone.
     */
    private void signOut() {
        AuthUI.getInstance()
                .signOut(this)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Intent intent = new Intent(MainActivity.this, PreSignInActivity.class);
                        intent.setFlags(intent.getFlags() | Intent.FLAG_ACTIVITY_NO_HISTORY);
                        startActivity(intent);
                        finish();
                    }
                });
    }



}
