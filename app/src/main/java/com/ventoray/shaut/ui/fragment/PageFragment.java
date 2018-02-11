package com.ventoray.shaut.ui.fragment;


import android.os.Bundle;
import android.os.Message;
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
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.WriteBatch;
import com.ventoray.shaut.R;
import com.ventoray.shaut.firebase.FirebaseContract;
import com.ventoray.shaut.firebase.Write;
import com.ventoray.shaut.model.ChatMessage;
import com.ventoray.shaut.model.ChatMetaData;
import com.ventoray.shaut.model.FriendRequest;
import com.ventoray.shaut.model.User;
import com.ventoray.shaut.ui.adapter.FriendFinderAdapter;
import com.ventoray.shaut.ui.adapter.FriendReqeustAdapter;
import com.ventoray.shaut.util.FileHelper;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


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

    public static final int PAGINATION_LIMIT = 3;

    private static final String LOG_TAG = "PageFragment";


    //friend request page
    private List<FriendRequest> friendRequests;
    private ListenerRegistration friendRequestReg;

    //friend finder page
    private List<User> potentialFriends;

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
                        swipeRefreshLayout.setRefreshing(false);
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
        switch (pageType) {
            case PAGE_TYPE_FRIENDFINDER:
                initializeFriendFinderPage();
                break;

            case PAGE_TYPE_SHAUTS:
                view.findViewById(R.id.fab_newShaut).setVisibility(View.VISIBLE);
                break;

            case PAGE_TYPE_FRIEND_REQUESTS:
                initializeFriendRequestsPage();
                break;

            case PAGE_TYPE_MESSAGES:

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


    @Override
    public void onDestroy() {
        super.onDestroy();
        if (friendRequestReg != null) {
            friendRequestReg.remove();
        }
    }


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
                        String message = accepted ? getString(R.string.accepted) :
                                getString(R.string.declined);

                        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
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
            initializeFriendship(friendRequest);
        } else { //declined friend request
            deleteFriendRequest(friendRequest);
        }
    }

    private void deleteFriendRequest(FriendRequest friendRequest) {
        db.collection(FirebaseContract.UsersCollection.NAME)
                .document(userObject.getUserKey())
                .collection(FirebaseContract.UsersCollection.User.StrangersRequestCollection.NAME)
                .document(friendRequest.getRequesterUserKey())
                .delete();
    }

    /**
     *
     * @param friendRequest
     */
    private void initializeFriendship(final FriendRequest friendRequest) {
        WriteBatch batch = db.batch();
        String chatroomId;
        String userKey = userObject.getUserKey();
        String userName = userObject.getUserName();
        String friendKey = friendRequest.getRequesterUserKey();
        String friendName = friendRequest.getRequesterUserName();
        long time = new Date().getTime();
        ChatMessage message = new ChatMessage("", "",
                "Welcome to Friendship!", time);

        //Create messages col/chatroomID doc/messages col/messages doc/message
        DocumentReference messagesRef = db
                .collection(FirebaseContract.MessagesCollection.NAME)
                .document();
        chatroomId = messagesRef.getId(); //create chatroom ID here and use it later

        messagesRef
                .collection(FirebaseContract.MessagesCollection.ChatMessagesCollection.NAME)
                .document();

        ChatMetaData userChatMetaData = new ChatMetaData(time, userKey, userName, friendKey, friendName, message.getMessageText());
        //Create users col/user doc/chatrooms col/chatmetadata doc/chatmetadata
        DocumentReference userChatMetaDataRef = db
                .collection(FirebaseContract.UsersCollection.NAME)
                .document(userKey)
                .collection(FirebaseContract.UsersCollection.ChatroomsCollection.NAME)
                .document(chatroomId);

        ChatMetaData friendChatMetaData = new ChatMetaData(time, friendKey, friendName, userKey, userName, message.getMessageText());
        //Create users col/user doc/chatrooms col/chatmetadata doc/chatmetadata
        DocumentReference friendChatMetaDataRef = db
                .collection(FirebaseContract.UsersCollection.NAME)
                .document(friendKey)
                .collection(FirebaseContract.UsersCollection.ChatroomsCollection.NAME)
                .document(chatroomId);

        //Set and commit batch update
        batch.set(messagesRef, message);
        batch.set(userChatMetaDataRef, userChatMetaData);
        batch.set(friendChatMetaDataRef, friendChatMetaData);
        batch.commit().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (!task.isSuccessful()) {
                    Log.d(LOG_TAG, task.getException().getMessage());
                } else {
                    deleteFriendRequest(friendRequest);
                }
            }
        });
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
     * userObject is reinitialized here in order to captur the new city key saved to file in the
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




}
