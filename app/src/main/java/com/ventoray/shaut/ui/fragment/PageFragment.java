package com.ventoray.shaut.ui.fragment;


import android.os.Bundle;
import android.support.annotation.IntDef;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.ventoray.shaut.R;
import com.ventoray.shaut.firebase.FirebaseContract;
import com.ventoray.shaut.model.User;
import com.ventoray.shaut.ui.adapter.TestRecyclerAdapter;
import com.ventoray.shaut.util.FileHelper;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.List;

import static com.ventoray.shaut.model.User.MOVED_TO_CITY_DATE;

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
    @interface PageType {
    }

    public static final int PAGE_TYPE_FRIENDFINDER = 0;
    public static final int PAGE_TYPE_SHAUTS = 1;
    public static final int PAGE_TYPE_FRIEND_REQUESTS = 2;
    public static final int PAGE_TYPE_MESSAGES = 3;
    public static final String ARGS_KEY_PAGE_TYPE =
            "com.ventoray.shaut.ui.fragment.PageFragment.ARGS_KEY_PAGE_TYPE";

    public static final int PAGINATION_LIMIT = 2;

    private static final String LOG_TAG = "PageFragment";

    private int pageType;
    private User userObject;
    private List<User> potentialFriends;
    private FirebaseFirestore db;
    private TestRecyclerAdapter testAdapter;
    private SwipeRefreshLayout swipeRefreshLayout;

    private DocumentSnapshot lastVisible;
    private boolean onLast;


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
        userObject = (User) FileHelper.readObjectFromFile(getContext(), FileHelper.USER_OBJECT_FILE);
        db = FirebaseFirestore.getInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_page, container, false);

        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayout.setRefreshing(true);
                Toast.makeText(getContext(), "Refresh", Toast.LENGTH_SHORT).show();

                switch (pageType) {
                    case PAGE_TYPE_FRIENDFINDER:
                        refreshFriendFinder();
                        break;

                    case PAGE_TYPE_SHAUTS:
                        break;

                    case PAGE_TYPE_FRIEND_REQUESTS:

                        break;

                    case PAGE_TYPE_MESSAGES:

                        break;

                    default:
                        //do something
                }

            }
        });

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

    private void setPageType() {
        Bundle args = getArguments();
        if (args != null && args.containsKey(ARGS_KEY_PAGE_TYPE)) {
            pageType = args.getInt(ARGS_KEY_PAGE_TYPE);
        }
    }

    private void initializePage(@PageType int pageType, View view) {
        switch (pageType) {
            case PAGE_TYPE_FRIENDFINDER:
                initializeFriendFinderPage();
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


    /***********************************************************************************************
     * Friend finder page methods
     **********************************************************************************************/

    private void initializeFriendFinderPage() {
        Query query = db.collection(FirebaseContract.UsersNode.NAME)
                /*.orderBy(MOVED_TO_CITY_DATE, Query.Direction.DESCENDING)*/;
        potentialFriends = new ArrayList<>();
        testAdapter = new TestRecyclerAdapter(this.getContext(), potentialFriends);
        recyclerView.setAdapter(testAdapter);
        //users/$userId/user_object/cityKey = true

        String cityKey = userObject.getCityKey();
        if (cityKey == null) {
            Toast.makeText(PageFragment.this.getContext(), "No City", Toast.LENGTH_SHORT).show();
            return;
        }
        getPotentialFriends(query, cityKey);
    }


    private void refreshFriendFinder() {
        Query query = db.collection(FirebaseContract.UsersNode.NAME);
        String cityKey = userObject.getCityKey();
        getPotentialFriends(query, cityKey);
    }

    private void getPotentialFriends(Query query, String cityKey) {
        query = query.whereEqualTo(User.CITY_KEY, cityKey)
                .orderBy(MOVED_TO_CITY_DATE, Query.Direction.DESCENDING);

        if (!onLast && lastVisible != null) {
            query = query.startAt(lastVisible);
        }
        query.limit(PAGINATION_LIMIT)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot documentSnapshots) {

                        if (documentSnapshots.getDocumentChanges().size() < 2) {
                            onLast = true;
                        } else {
                            onLast = false;
                        }
                        lastVisible = documentSnapshots.getDocuments()
                                .get(documentSnapshots.size() - 1);

                        List<User> users = documentSnapshots.toObjects(User.class);
                        potentialFriends.clear();
                        for (User user : users) {
                            potentialFriends.add(user);
                            Log.d(LOG_TAG, user.getUserEmailAddress());
                        }

                        testAdapter.notifyDataSetChanged();
                        swipeRefreshLayout.setRefreshing(false);
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e(LOG_TAG, e.getMessage());
            }
        });
    }


}
