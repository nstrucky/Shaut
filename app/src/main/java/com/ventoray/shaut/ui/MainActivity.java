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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;
import com.ventoray.shaut.firebase.AuthHelper;
import com.ventoray.shaut.firebase.FirebaseContract;
import com.ventoray.shaut.model.User;
import com.ventoray.shaut.ui.util.MainActivityPagerAdapter;
import com.ventoray.shaut.R;
import com.ventoray.shaut.ui.util.FragmentPageTransformer;
import com.ventoray.shaut.util.AutoCompleteHelper;
import com.ventoray.shaut.util.FileHelper;


import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.ventoray.shaut.util.FileHelper.USER_OBJECT_FILE;
import static com.ventoray.shaut.util.PreferenceHelper.PREF_SELECTED_CITY_ID;
import static com.ventoray.shaut.util.PreferenceHelper.savePreference;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    public static final int RC_PLACE_AUTOCOMPLETE = 1001;

    public static final String LOG_TAG = "MainActivity";

    private User userObject;
    private FirebaseFirestore db;

    @BindView(R.id.viewPager_main) ViewPager viewPager;
    @BindView(R.id.tablayout) TabLayout tabLayout;
    @BindView(R.id.imageView_city) ImageView cityImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        getUserObject();
        setUpNavDrawer();
        setUpViewPager();
        AutoCompleteHelper.initializePlaceAutoComplete(this, fragmentPlaceListener);

    }

    private void getUserObject() {
        db = FirebaseFirestore.getInstance();
        Object object = FileHelper.readObjectFromFile(this, USER_OBJECT_FILE);
        if (object == null) {
            getUserObjectFromFirestore();

        } else {
            userObject = (User) object;
            setUserData();
        }
    }

    private void getUserObjectFromFirestore() {
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        db.collection(FirebaseContract.UsersCollection.NAME)
                .document(uid)
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot documentSnapshot = task.getResult();
                    if (documentSnapshot != null && documentSnapshot.exists()) {
                        userObject = documentSnapshot.toObject(User.class);
                        if (userObject != null){
                            FileHelper.writeObjectToFile(MainActivity.this,
                                    userObject, FileHelper.USER_OBJECT_FILE);
                            setUserData();
                        }
                    } else {//document does not exist

                    }
                } else {
                    Log.e(LOG_TAG, task.getException().getMessage().toString());
                }
            }
        });

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
        long movedToCityDate = new Date().getTime();
        AutoCompleteHelper.getPlacePhoto(MainActivity.this,
                cityId, cityImageView);
        savePreference(MainActivity.this, PREF_SELECTED_CITY_ID, cityId);
        userObject.setCityKey(cityId);
        userObject.setCityName(cityName);
        userObject.setMovedToCityDate(movedToCityDate);

        FileHelper.writeObjectToFile(this, userObject, USER_OBJECT_FILE);

//        Write.updateUserCity(userObject, oldCityId, null);
        db.collection(FirebaseContract.UsersCollection.NAME)
                .document(userObject.getUserKey())
                .set(userObject.toMap());

    }

    private void setUserData() {
        checkCityPref();
        NavigationView navigationView = findViewById(R.id.nav_view);
        View headerLayout = navigationView.getHeaderView(0);
        ImageView imageView = headerLayout.findViewById(R.id.imageView_profilePicture);
        TextView userNameTextView = headerLayout.findViewById(R.id.textView_userName);
        TextView userEmailAddressTextView = headerLayout.findViewById(R.id.textView_emailAddress);
        userNameTextView.setText(userObject.getUserName());
        userEmailAddressTextView.setText(userObject.getUserEmailAddress());
        String picUrl = userObject.getProfileImageUrl();

        if (picUrl != null) {
            Picasso
                    .with(this)
                    .load(picUrl)
                    .into(imageView);
        }
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
}
