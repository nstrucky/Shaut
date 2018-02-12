package com.ventoray.shaut.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.ventoray.shaut.R;
import com.ventoray.shaut.model.FriendRequest;

import java.util.List;

/**
 * Created by Nick on 2/11/2018.
 */

public class FriendReqeustAdapter extends RecyclerView.Adapter<FriendReqeustAdapter.FriendRequestViewHolder> {

    public interface OnFriendRequestResponseListener {
        void onResponse(FriendRequest friendRequest, boolean accepted);
    }

    private Context context;
    private List<FriendRequest> friendRequests;
    OnFriendRequestResponseListener requestResponseListener;

    public FriendReqeustAdapter(Context context, List<FriendRequest> friendRequests,
                                OnFriendRequestResponseListener requestResponseListener) {
        this.context = context;
        this.friendRequests = friendRequests;
        this.requestResponseListener = requestResponseListener;
    }


    @Override
    public FriendRequestViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater
                .from(context)
                .inflate(R.layout.list_item_friend_requests, parent, false);

        return new FriendRequestViewHolder(view);
    }

    @Override
    public void onBindViewHolder(FriendRequestViewHolder holder, int position) {
        FriendRequest friendRequest = friendRequests.get(position);
        String requesterName = friendRequest.getRequesterUserName();
        String photoUrl = friendRequest.getRequesterImageUrl();
        String profileContent = friendRequest.getRequesterProfileContent();
        if (requesterName == null || requesterName.isEmpty()) {
            requesterName = context.getString(R.string.no_name);
        }
        if (profileContent == null || requesterName.isEmpty()) {
            profileContent = context.getString(R.string.no_name);
        }

        if (photoUrl != null) {
            Glide.with(context)
                    .load(photoUrl)
                    .into(holder.profileImageView);
        }
        holder.userNameTextView.setText(requesterName);
        holder.shautTextView.setText(profileContent);
    }

    @Override
    public int getItemCount() {
        if (friendRequests == null) return 0;
        return friendRequests.size();
    }

    class FriendRequestViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        ImageView profileImageView;
        TextView shautTextView;
        TextView userNameTextView;
        ImageButton acceptButton;
        ImageButton declineButton;

        public FriendRequestViewHolder(View itemView) {
            super(itemView);

            profileImageView = itemView.findViewById(R.id.imageView_profilePicture);
            shautTextView = itemView.findViewById(R.id.textView_shaut_content);
            userNameTextView = itemView.findViewById(R.id.textView_userName);
            acceptButton = itemView.findViewById(R.id.button_accept);
            declineButton = itemView.findViewById(R.id.button_decline);
            acceptButton.setOnClickListener(this);
            declineButton.setOnClickListener(this);

        }

        @Override
        public void onClick(View view) {
            int viewId = view.getId();
            boolean accepted = viewId == R.id.button_accept;
            int position = getAdapterPosition();
            FriendRequest friendRequest = friendRequests.get(position);
            if (accepted) {
                acceptButton.setEnabled(false);
            }
            requestResponseListener.onResponse(friendRequest, accepted);
        }
    }
}
