package com.ventoray.shaut.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ventoray.shaut.R;
import com.ventoray.shaut.model.ChatMessage;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by Nick on 2/12/2018.
 */

public class ChatMessageAdapter extends RecyclerView.Adapter<ChatMessageAdapter.ChatMessageViewHolder>{

    private final int VIEW_TYPE_USER = 1001;
    private final int VIEW_TYPE_FRIEND = 1002;

    private Context context;
    private List<ChatMessage> chatMessages;
    private String userKey;

    public ChatMessageAdapter(Context context, List<ChatMessage> chatMessages, String userKey) {
        this.context = context;
        this.chatMessages = chatMessages;
        this.userKey = userKey;
    }

    @Override
    public ChatMessageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        int layoutId;
        if (viewType == VIEW_TYPE_USER){
            layoutId = R.layout.list_item_message_user;
        } else {
            layoutId = R.layout.list_item_message_friend;
        }
        View view = LayoutInflater.from(context)
                .inflate(layoutId, parent, false);


        return new ChatMessageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ChatMessageViewHolder holder, int position) {
        ChatMessage chatMessage = chatMessages.get(position);
        String message = chatMessage.getMessageText();
        long timeStamp = chatMessage.getMessageTime();
        Date date = new Date(timeStamp);
        DateFormat dateFormat = new SimpleDateFormat();
        String timeString = dateFormat.format(date);

        holder.messageTextView.setText(message);
        holder.timestampTextView.setText(timeString);
    }

    @Override
    public int getItemViewType(int position) {
        String key = chatMessages.get(position).getUserKey();

        if (userKey.equals(key)) {
            return VIEW_TYPE_USER;
        } else {
            return VIEW_TYPE_FRIEND;
        }


    }

    @Override
    public int getItemCount() {
        if (chatMessages == null) return 0;
        return chatMessages.size();
    }

    class ChatMessageViewHolder extends RecyclerView.ViewHolder {

        TextView messageTextView;
        TextView timestampTextView;


        public ChatMessageViewHolder(View itemView) {
            super(itemView);

            messageTextView = itemView.findViewById(R.id.textView_message);
            timestampTextView = itemView.findViewById(R.id.textView_message_timestamp);
        }
    }


}
