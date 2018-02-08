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

import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ventoray.shaut.firebase.AuthHelper;
import com.ventoray.shaut.firebase.FirebaseContract;
import com.ventoray.shaut.firebase.Write;
import com.ventoray.shaut.model.User;
import com.ventoray.shaut.ui.util.MainActivityPagerAdapter;
import com.ventoray.shaut.R;
import com.ventoray.shaut.ui.util.FragmentPageTransformer;
import com.ventoray.shaut.util.AutoCompleteHelper;
import com.ventoray.shaut.util.FileHelper;


import butterknife.BindView;
import butterknife.ButterKnife;

import static com.ventoray.shaut.util.FileHelper.USER_OBJECT_FILE;
import static com.ventoray.shaut.util.PreferenceHelper.PREF_SELECTED_CITY_ID;
import static com.ventoray.shaut.util.PreferenceHelper.getPreferenceValue;
import static com.ventoray.shaut.util.PreferenceHelper.savePreference;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    public static final int RC_PLACE_AUTOCOMPLETE = 1001;

    public static final String LOG_TAG = "MainActivity";

    private User userObject;
    private DatabaseReference userObjectReference;

    @BindView(R.id.viewPager_main) ViewPager viewPager;
    @BindView(R.id.tablayout) TabLayout tabLayout;
    @BindView(R.id.imageView_city) ImageView cityImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        createDatabaseRefs();
        getUserObject();
        setUpNavDrawer();
        setUpViewPager();
        AutoCompleteHelper.initializePlaceAutoComplete(this, fragmentPlaceListener);

    }


    private void createDatabaseRefs() {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        userObjectReference = FirebaseDatabase.getInstance().getReference()
                .child(FirebaseContract.UsersNode.NAME)
                .child(userId)
                .child(FirebaseContract.UsersNode.User.USER_OBJECT);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == RC_PLACE_AUTOCOMPLETE) {
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
        if (userObject == null) return;
        String cityId = userObject.getCityKey();
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
                                RC_PLACE_AUTOCOMPLETE
                        );
                    }
                }).show();
    }


    PlaceSelectionListener fragmentPlaceListener = new PlaceSelectionListener() {
        @Override
        public void onPlaceSelected(Place place) {
            if (place == null) return;
            saveAndDisplayCity(place);

        }

        @Override
        public void onError(Status status) {
            Log.e(LOG_TAG, status.getStatusMessage());
        }
    };

    private void saveAndDisplayCity(Place place) {
        String oldCityId = userObject.getCityKey();
        String cityId = place.getId();
        String cityName = place.getName().toString();
        AutoCompleteHelper.getPlacePhoto(MainActivity.this,
                cityId, cityImageView);
        savePreference(MainActivity.this, PREF_SELECTED_CITY_ID, cityId);
        userObject.setCityKey(cityId);
        userObject.setCityName(cityName);

        FileHelper.writeObjectToFile(this, userObject, USER_OBJECT_FILE);

        Write.updateUserCity(userObject, oldCityId, null);

    }


    private void getUserObject() {
        Object object = FileHelper.readObjectFromFile(this, USER_OBJECT_FILE);
        if (object == null) {
            userObjectReference.addValueEventListener(userObjectValueEventListener);

        } else {
            userObject = (User) object;
            setUserData();
        }
    }


    private void setUserData() {
        checkCityPref();
        NavigationView navigationView = findViewById(R.id.nav_view);
        View headerLayout = navigationView.getHeaderView(0);
        TextView userNameTextView = headerLayout.findViewById(R.id.textView_userName);
        TextView userEmailAddressTextView = headerLayout.findViewById(R.id.textView_emailAddress);
        userNameTextView.setText(userObject.getUserName());
        userEmailAddressTextView.setText(userObject.getUserEmailAddress());
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
                AuthHelper.signOut(this);
                break;
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    ValueEventListener userObjectValueEventListener = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            userObject = dataSnapshot.getValue(User.class);
            if (userObject != null){
                FileHelper.writeObjectToFile(MainActivity.this,
                        userObject, FileHelper.USER_OBJECT_FILE);
                setUserData();
            }
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {

        }
    };//End listener for User Object


    @Override
    protected void onStop() {
        super.onStop();
        if (userObjectReference != null) {
            userObjectReference.removeEventListener(userObjectValueEventListener);
        }
    }
}
