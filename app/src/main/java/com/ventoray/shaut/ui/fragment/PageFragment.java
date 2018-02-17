package com.ventoray.shaut.ui.fragment;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.IntDef;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.ventoray.shaut.R;
import com.ventoray.shaut.firebase.FirebaseContract;
import com.ventoray.shaut.firebase.Write;
import com.ventoray.shaut.model.ChatMetaData;
import com.ventoray.shaut.model.FriendRequest;
import com.ventoray.shaut.model.Shaut;
import com.ventoray.shaut.model.User;
import com.ventoray.shaut.ui.MessageActivity;
import com.ventoray.shaut.ui.adapter.ChatroomsAdapter;
import com.ventoray.shaut.ui.adapter.FriendFinderAdapter;
import com.ventoray.shaut.ui.adapter.FriendReqeustAdapter;
import com.ventoray.shaut.util.FileHelper;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.ventoray.shaut.client_data.DataHelper.refreshFriendRequests;
import static com.ventoray.shaut.client_data.FriendRequestsContract.FriendRequestEntry.COLUMN_REQUESTER_USER_KEY;
import static com.ventoray.shaut.client_data.FriendRequestsContract.FriendRequestEntry.CONTENT_URI;
import static com.ventoray.shaut.firebase.FirebaseContract.UsersCollection.User.ChatroomsCollection.ChatMetaData.FIELD_TIMESTAMP;
import static com.ventoray.shaut.ui.MessageActivity.PARCEL_KEY_CHAT_META_DATA;


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

    public static final int PAGINATION_LIMIT = 5;

    private static final String LOG_TAG = "PageFragment";

    //Chatrooms page
    private ListenerRegistration chatroomsReg;
    private List<ChatMetaData> chatMetaDataList;

    //friend request page
    private List<FriendRequest> friendRequests;
    private ListenerRegistration friendRequestReg;

    //friend finder page
    private List<User> potentialFriends;

    //shauts page
    private List<Shaut> shautsList;
    private FloatingActionButton shautFab;
    private EditText shautEditText;

    //all pages
    private TextView emptyTextView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView.Adapter adapter;
    private FirebaseFirestore db;
    private int pageType;
    private RecyclerView recyclerView;
    private User userObject;
    private boolean onLast;
    private DocumentSnapshot lastVisible;

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

        emptyTextView = (TextView) view.findViewById(R.id.textView_empty);
        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        initializeSwipeRefreshLayout(view);

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

    private void initializeSwipeRefreshLayout(View view) {
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
                        addShautsToView();

                        break;

                    case PAGE_TYPE_FRIEND_REQUESTS:
                        swipeRefreshLayout.setRefreshing(false);
                        break;

                    case PAGE_TYPE_MESSAGES:
                        swipeRefreshLayout.setRefreshing(false);
                        break;

                    default:
                        //do something
                }

            }
        });
    }

    private void initializePage(@PageType int pageType, View view) {

        Log.d(LOG_TAG, "Page type: " + pageType);
        switch (pageType) {
            case PAGE_TYPE_FRIENDFINDER:
                initializeFriendFinderPage();
                break;

            case PAGE_TYPE_SHAUTS:
                initializeShautsPage(view);
                break;

            case PAGE_TYPE_FRIEND_REQUESTS:
                initializeFriendRequestsPage();
                break;

            case PAGE_TYPE_MESSAGES:
                initializeChatroomsPage();
                break;

            default:
                //do something
        }
    }


    private void emptyTextVisiblity() {
        if (pageType == PAGE_TYPE_FRIEND_REQUESTS) {
            emptyTextView.setText(R.string.no_requests);
        }
        if (adapter.getItemCount() == 0) {
            emptyTextView.setVisibility(View.VISIBLE);
        } else {
            emptyTextView.setVisibility(View.GONE);
        }
    }

    /**
     * Removing ListenerRegistration objects in order to detach listeners
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
        if (friendRequestReg != null) {
            friendRequestReg.remove();
        }

        if (chatroomsReg != null) {
            chatroomsReg.remove();
        }
    }

    /***********************************************************************************************
     * Chatrooms page methods
     ***********************************************************************************************/

    private void initializeChatroomsPage() {
        Query chatroomsQuery = db
                .collection(FirebaseContract.UsersCollection.NAME)
                .document(userObject.getUserKey())
                .collection(FirebaseContract.UsersCollection.User.ChatroomsCollection.NAME)
                .orderBy(FIELD_TIMESTAMP, Query.Direction.DESCENDING);
//                .limit(PAGINATION_LIMIT);
        chatMetaDataList = new ArrayList<>();
        adapter = new ChatroomsAdapter(getContext(), chatMetaDataList,
                new ChatroomsAdapter.OnChatRoomClickedListener() {
                    @Override
                    public void onChatRoomClicked(ChatMetaData metaData) {
                        Intent intent = new Intent(getContext(), MessageActivity.class);
                        intent.putExtra(PARCEL_KEY_CHAT_META_DATA, metaData);
                        startActivity(intent);
                    }
                });
        recyclerView.setAdapter(adapter);
        chatroomsReg = chatroomsQuery.addSnapshotListener(chatroomListener);
    }

    EventListener<QuerySnapshot> chatroomListener = new EventListener<QuerySnapshot>() {
        @Override
        public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
            chatMetaDataList.clear();
            if (documentSnapshots != null && documentSnapshots.size() > 0) {
                for (DocumentSnapshot documentSnapshot : documentSnapshots) {
                    if (documentSnapshot == null) continue;
                    ChatMetaData chatMetaData = documentSnapshot.toObject(ChatMetaData.class);
                    chatMetaDataList.add(chatMetaData);
                }
                adapter.notifyDataSetChanged();
                emptyTextVisiblity();
            } else {
                Toast.makeText(getContext(), R.string.something_wrong, Toast.LENGTH_SHORT).show();
            }
        }
    };

    /***********************************************************************************************
     * Friend request page methods
     **********************************************************************************************/
    private void initializeFriendRequestsPage() {
         Query friendRequestQuery = db.collection(FirebaseContract.UsersCollection.NAME)
                .document(userObject.getUserKey())
                .collection(FirebaseContract.UsersCollection.User.StrangersRequestCollection.NAME);
        friendRequests = new ArrayList<>();
        adapter = new FriendReqeustAdapter(getContext(), friendRequests,
                new FriendReqeustAdapter.OnFriendRequestResponseListener() {
                    @Override
                    public void onResponse(FriendRequest friendRequest, boolean accepted) {
                        respondToRequest(friendRequest, accepted);
                    }
                });
        recyclerView.setAdapter(adapter);
        friendRequestReg =
                friendRequestQuery.addSnapshotListener(friendRequestListener);
    }


    EventListener<QuerySnapshot> friendRequestListener = new EventListener<QuerySnapshot>() {
        @Override
        public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
            friendRequests.clear();
            if (documentSnapshots != null && documentSnapshots.size() > 0) {
                for (DocumentSnapshot documentSnapshot : documentSnapshots) {
                    FriendRequest request = documentSnapshot.toObject(FriendRequest.class);
                    friendRequests.add(request);
                }
                adapter.notifyDataSetChanged();
                emptyTextVisiblity();

                if (friendRequests == null || friendRequests.size() < 0) return;
                refreshFriendRequests(getContext(), friendRequests);
            }
        }
    };

    /**
     * Handles the logic for responding to a stranger's friend request.
     * Deletes the request if declining and starts the chat room if accepting (also deletes request)
     * @param friendRequest
     * @param accepted
     */
    private void respondToRequest(FriendRequest friendRequest, boolean accepted) {
        if (accepted) {
            Write.initializeFriendship(friendRequest, db, userObject);
        } else { //declined friend request
            deleteFriendRequest(friendRequest);
        }

        String message = accepted ? getString(R.string.accepted) :
                getString(R.string.declined);

        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
    }

    /**
     * Removes the friend request from users strangers request collection
     * @param friendRequest
     */
    private void deleteFriendRequest(FriendRequest friendRequest) {
        db.collection(FirebaseContract.UsersCollection.NAME)
                .document(userObject.getUserKey())
                .collection(FirebaseContract.UsersCollection.User.StrangersRequestCollection.NAME)
                .document(friendRequest.getRequesterUserKey())
                .delete();

        String[] args = new String[]{friendRequest.getRequesterUserKey()};

        int deleted = getContext().getContentResolver()
                .delete(CONTENT_URI, COLUMN_REQUESTER_USER_KEY, args);

        Log.d(LOG_TAG, "Deleted " + deleted + " records");
    }


    /***********************************************************************************************
     * Friend finder page methods
     **********************************************************************************************/

    private void initializeFriendFinderPage() {
        Query query = db.collection(FirebaseContract.UsersCollection.NAME);
        potentialFriends = new ArrayList<>();
        adapter = new FriendFinderAdapter(this.getContext(), potentialFriends,
                new FriendFinderAdapter.OnFriendRequetedCallback() {
                    @Override
                    public void onFriendRequested(FriendRequest friendRequest) {
                        Toast.makeText(getContext(),
                                R.string.friend_request_sent,
                                Toast.LENGTH_SHORT).show();
                        Write.sendFriendRequest(friendRequest, userObject, db, null);
                    }
                });
        recyclerView.setAdapter(adapter);


        String cityKey = userObject.getCityKey();
        if (cityKey == null) {
            Toast.makeText(PageFragment.this.getContext(), "No City", Toast.LENGTH_SHORT).show();
            return;
        }
        getPotentialFriends(query, cityKey, true);
    }

    /**
     * userObject is reinitialized here in order to capture the new city key saved to file in the
     * main activity
     */
    private void refreshFriendFinder() {
        userObject = (User) FileHelper.readObjectFromFile(getContext(), FileHelper.USER_OBJECT_FILE);
        Query query = db.collection(FirebaseContract.UsersCollection.NAME);
        String cityKey = userObject.getCityKey();
        getPotentialFriends(query, cityKey, false);

    }

    private void getPotentialFriends(Query query, String cityKey, boolean startFromNewest) {
        query = query.whereEqualTo(FirebaseContract.UsersCollection.User.FIELD_CITY_KEY, cityKey)
                .orderBy(FirebaseContract.UsersCollection.User.FIELD_MOVED_TO_CITY_DATE,
                        Query.Direction.DESCENDING);

        if (!onLast && lastVisible != null && !startFromNewest) {
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

                        List<DocumentSnapshot> snapshots = documentSnapshots.getDocuments();
                        if (snapshots != null && snapshots.size() > 0) {
                            lastVisible = snapshots
                                    .get(documentSnapshots.size() - 1);
                        }

                        List<User> users = documentSnapshots.toObjects(User.class);
                        potentialFriends.clear();
                        for (User user : users) {
                            if (!user.getUserKey().equals(userObject.getUserKey())) {
                                potentialFriends.add(user);
                            }
                            Log.d(LOG_TAG, user.getUserEmailAddress());
                        }

                        adapter.notifyDataSetChanged();
                        swipeRefreshLayout.setRefreshing(false);
                        emptyTextVisiblity();
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e(LOG_TAG, e.getMessage());
            }
        });
    }


    /***********************************************************************************************
     * Shauts Page methods
     ***********************************************************************************************/

    private void initializeShautsPage(View view) {
        shautEditText = view.findViewById(R.id.editText_shaut);
        setUpShautsFab(view);
    }


    private void addShautsToView() {
        Query query = db.collection(FirebaseContract.UsersCollection.NAME);
        String cityKey = userObject.getCityKey();

        swipeRefreshLayout.setRefreshing(false);

    }

    private void setUpShautsFab(View view) {
        shautFab = view.findViewById(R.id.fab_newShaut);
        shautFab.setVisibility(View.VISIBLE);
        shautFab.setOnClickListener(createShautClickListener);
    }

    private void createShaut() {
        showSoftKeyboard();
        shautFab.setImageDrawable(getContext().getDrawable(R.drawable.ic_send_black_24px));
        shautFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                shautOutToTheWorld();
            }
        });

    }

    public void showSoftKeyboard() {
        shautEditText.setVisibility(View.VISIBLE);
        if (shautEditText.requestFocus()) {
            InputMethodManager imm = (InputMethodManager)
                    getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(shautEditText, InputMethodManager.SHOW_IMPLICIT);

        }
    }

    private void shautOutToTheWorld() {
        String shautString = hideEditText();
        if (shautString == null || shautString.isEmpty()) return;

        long time = new Date().getTime();

        Shaut shaut = new Shaut(
               userObject.getUserName(),
                userObject.getUserKey(),
                userObject.getCityKey(),
                userObject.getProfileImageUrl(),
                shautString,
                time,
                0,
                0

        );

        db.collection(FirebaseContract.ShautsCollection.NAME)
                .document()
                .set(shaut)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(getContext(),
                        R.string.app_name, Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getContext(), R.string.something_wrong,
                        Toast.LENGTH_SHORT).show();
                Log.d(LOG_TAG, "Error: " + e.getLocalizedMessage());
            }
        });

    }

    private void hideSoftKeyboard() {
        View view = getView();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    private String hideEditText() {
        String shautString = shautEditText.getText().toString();
        shautEditText.clearAnimation();
        shautEditText.clearComposingText();
        shautEditText.clearFocus();
        shautEditText.setVisibility(View.GONE);
        hideSoftKeyboard();
        shautFab.setImageDrawable(getContext().getDrawable(R.drawable.ic_create_white_24px));
        shautFab.setOnClickListener(createShautClickListener);
        return shautString;
    }

    View.OnClickListener createShautClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            createShaut();
        }
    };



}
