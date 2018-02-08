package com.ventoray.shaut.ui.fragment;


import android.os.Bundle;
import android.support.annotation.IntDef;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.ventoray.shaut.R;
import com.ventoray.shaut.firebase.FirebaseContract;
import com.ventoray.shaut.model.User;
import com.ventoray.shaut.ui.adapter.TestRecyclerAdapter;
import com.ventoray.shaut.util.FileHelper;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 */
public class PageFragment extends Fragment {

    @IntDef({
            PAGE_TYPE_FRIENDFINDER,
            PAGE_TYPE_SHAUTS,
            PAGE_TYPE_FRIEND_REQUESTS,
            PAGE_TYPE_MESSAGES
    })
    @Retention(RetentionPolicy.SOURCE)
    @interface PageType{}

    public static final int PAGE_TYPE_FRIENDFINDER = 0;
    public static final int PAGE_TYPE_SHAUTS = 1;
    public static final int PAGE_TYPE_FRIEND_REQUESTS = 2;
    public static final int PAGE_TYPE_MESSAGES = 3;
    public static final String ARGS_KEY_PAGE_TYPE =
            "com.ventoray.shaut.ui.fragment.PageFragment.ARGS_KEY_PAGE_TYPE";

    private static final String LOG_TAG = "PageFragment";

    private int pageType;
    private Query usersInCityQuery;
    private List<User> potentialFriends;
    TestRecyclerAdapter testAdapter;

//    @BindView(R.id.recyclerView) RecyclerView recyclerView;
    private RecyclerView recyclerView;

    public PageFragment() {
        // Required empty public constructor
    }

    public static PageFragment newInstance(int position) {
        Bundle args = new Bundle();
        args.putInt(ARGS_KEY_PAGE_TYPE, position);
        PageFragment fragment = new PageFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setPageType();


        potentialFriends = new ArrayList<>();
        testAdapter = new TestRecyclerAdapter(this.getContext(), potentialFriends);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_page, container, false);

//        ButterKnife.bind(view);
        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(testAdapter);



        try {
            initializePage(pageType, view);
        } catch (IllegalArgumentException e) {
            Log.e(LOG_TAG, e.getLocalizedMessage());
        }

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

    }

    private void initializePage(@PageType int pageType, View view) {
        switch (pageType) {
            case PAGE_TYPE_FRIENDFINDER:
                doFriendFinderPageStuff();
                break;

            case PAGE_TYPE_SHAUTS:
                view.findViewById(R.id.fab_newShaut).setVisibility(View.VISIBLE);
                break;

            case PAGE_TYPE_FRIEND_REQUESTS:

                break;

            case PAGE_TYPE_MESSAGES:

                break;

                default:
                    //do something
        }
    }


    private void setPageType() {
        Bundle args = getArguments();
        if (args != null && args.containsKey(ARGS_KEY_PAGE_TYPE)) {
            pageType = args.getInt(ARGS_KEY_PAGE_TYPE);
        }
    }


    private void doFriendFinderPageStuff() {
        //users/$userId/user_object/cityKey = true
        User user = (User) FileHelper.readObjectFromFile(getContext(), FileHelper.USER_OBJECT_FILE);
        usersInCityQuery = FirebaseDatabase.getInstance().getReference("users")
                .orderByChild(FirebaseContract.UsersNode.User.USER_OBJECT + "/" +
                        User.CITY_KEY)
                .equalTo(user.getCityKey());

        usersInCityQuery.addListenerForSingleValueEvent(friendFinderChildEventListener);
    }


    ValueEventListener friendFinderChildEventListener = new ValueEventListener() {

        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            potentialFriends.clear();

            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                    User user = snapshot1.getValue(User.class);
                    potentialFriends.add(user);
                }
            }

            testAdapter.notifyDataSetChanged();
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {

        }
    };


    @Override
    public void onStop() {
        super.onStop();
        if (usersInCityQuery != null) {
            usersInCityQuery.removeEventListener(friendFinderChildEventListener);
        }
    }
}
