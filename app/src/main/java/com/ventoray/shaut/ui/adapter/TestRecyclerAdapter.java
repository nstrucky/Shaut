package com.ventoray.shaut.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.ventoray.shaut.R;
import com.ventoray.shaut.model.User;

import java.util.List;

/**
 * Created by Nick on 2/7/2018.
 */

public class TestRecyclerAdapter extends RecyclerView.Adapter<TestRecyclerAdapter.TestViewHolder> {

    private List<User> users;
    private Context context;

    public TestRecyclerAdapter(Context context, List<User> users) {
        this.context = context;
        this.users = users;
    }


    @Override
    public TestViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.list_item_friend_finder, parent, false);
        return new TestViewHolder(view);
    }

    @Override
    public void onBindViewHolder(TestViewHolder holder, int position) {
        User user = users.get(position);
        String userName = user.getUserName();
        if (userName == null) userName = user.getUserEmailAddress();
        if (userName == null) userName = context.getString(R.string.no_name);
        String userProfile = user.getProfileSummary();
        String imageUrl = user.getProfileImageUrl();

        if (imageUrl != null) {
            Glide
                    .with(context)
                    .load(imageUrl)
                    .into(holder.profileImageView);
        }

        holder.userNameTextView.setText(userName);
        holder.userProfileTextView.setText(userProfile);


    }

    @Override
    public int getItemCount() {
        if (users == null) return 0;
        return users.size();
    }

    class TestViewHolder extends RecyclerView.ViewHolder {

        ImageView profileImageView;
        TextView userNameTextView;
        TextView userProfileTextView;

        public TestViewHolder(View itemView) {
            super(itemView);

            profileImageView = itemView.findViewById(R.id.imageView_profilePicture);
            userNameTextView = itemView.findViewById(R.id.textView_userName);
            userProfileTextView = itemView.findViewById(R.id.textView_profileText);

        }
    }
}
