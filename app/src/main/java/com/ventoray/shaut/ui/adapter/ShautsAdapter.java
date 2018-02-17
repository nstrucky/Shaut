package com.ventoray.shaut.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.ventoray.shaut.R;
import com.ventoray.shaut.model.Shaut;

import java.util.List;

/**
 * Created by Nick on 2/17/2018.
 */

public class ShautsAdapter extends RecyclerView.Adapter<ShautsAdapter.ShautsViewHolder> {

    public interface OnVoteButtonsClickedListener {
        void onLikeButtonClicked(Shaut shaut);

        void onDislikeButtonClicked(Shaut shaut);
    }

    private Context context;
    private List<Shaut> shauts;
    private OnVoteButtonsClickedListener clickedListener;

    public ShautsAdapter(Context context,
                         List<Shaut> shauts,
                         OnVoteButtonsClickedListener clickedListener) {
        this.context = context;
        this.shauts = shauts;
        this.clickedListener = clickedListener;

    }

    @Override
    public ShautsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.list_item_shauts, parent, false);

        return new ShautsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ShautsViewHolder holder, int position) {
        Shaut shaut = shauts.get(position);
        if (shaut == null) return;

        String userName = shaut.getUserName();
        String shautMessage = shaut.getMessageText();
        String imageUrl = shaut.getProfileImageUrl();
        int likes = shaut.getUpVote();
        int dislikes = shaut.getDownVote();

        holder.nameTextView.setText(userName);
        holder.shautTextView.setText(shautMessage);
        holder.likeTextView.setText(String.valueOf(likes));
        holder.dislikeTextView.setText(String.valueOf(dislikes));

        if (imageUrl != null && !imageUrl.isEmpty()) {
            Picasso.with(context)
                    .load(imageUrl)
                    .into(holder.profileImageView);
        } else {
            holder.profileImageView.setImageResource(R.drawable.spanky);
        }
    }

    @Override
    public int getItemCount() {
        if (shauts == null) return 0;
        return shauts.size();
    }

    class ShautsViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView shautTextView;
        TextView nameTextView;
        TextView likeTextView;
        TextView dislikeTextView;
        ImageButton likeButton;
        ImageButton dislikeButton;
        ImageView profileImageView;


        public ShautsViewHolder(View itemView) {
            super(itemView);

            shautTextView = itemView.findViewById(R.id.textView_shaut_content);
            nameTextView = itemView.findViewById(R.id.textView_userName);
            likeTextView = itemView.findViewById(R.id.textView_likes);
            dislikeTextView = itemView.findViewById(R.id.textView_dislikes);
            likeButton = itemView.findViewById(R.id.button_thumbsUp);
            dislikeButton = itemView.findViewById(R.id.button_thumbsDown);
            profileImageView = itemView.findViewById(R.id.imageView_profilePicture);


        }

        @Override
        public void onClick(View view) {
            int id = view.getId();
            int position = getAdapterPosition();
            Shaut shaut = shauts.get(position);

            switch (id) {
                case R.id.button_thumbsUp:
                    clickedListener.onLikeButtonClicked(shaut);
                    break;

                case R.id.button_thumbsDown:
                    clickedListener.onDislikeButtonClicked(shaut);
                    break;
            }
        }
    }

}




