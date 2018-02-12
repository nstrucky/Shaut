package com.ventoray.shaut.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ventoray.shaut.R;
import com.ventoray.shaut.model.ChatMetaData;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by Nick on 2/11/2018.
 */

public class ChatroomsAdapter extends RecyclerView.Adapter<ChatroomsAdapter.ChatroomsViewholder> {

    public interface OnChatRoomClickedListener {
        void onChatRoomClicked(ChatMetaData metaData);
    }

    private final int MESSAGE_PREV_MAX_CHARS = 26;

    private Context context;
    private List<ChatMetaData> chatMetaDataList;
    private OnChatRoomClickedListener chatRoomClickedListener;

    public ChatroomsAdapter(Context context,
                            List<ChatMetaData> chatMetaDataList,
                            OnChatRoomClickedListener chatRoomClickedListener) {
        this.context = context;
        this.chatMetaDataList = chatMetaDataList;
        this.chatRoomClickedListener = chatRoomClickedListener;
    }

    @Override
    public ChatroomsViewholder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater
                .from(context).inflate(R.layout.list_item_chatroom, parent, false);

        return new ChatroomsViewholder(view);
    }

    @Override
    public void onBindViewHolder(ChatroomsViewholder holder, int position) {
        ChatMetaData metaData = chatMetaDataList.get(position);

        String userName = metaData.getFriendName();
        String messagePreview = metaData.getLastMessage();
        char firstLetter;
        long timeStamp = metaData.getTimeStamp();
        String timeString;

        Date date = new Date(timeStamp);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat();
        timeString = simpleDateFormat.format(date);


        //Limit the number of characters displayed
        if (messagePreview != null && messagePreview.length() > MESSAGE_PREV_MAX_CHARS) {
            messagePreview = messagePreview.substring(0,
                    MESSAGE_PREV_MAX_CHARS) + "...";
        }

        //If username does not exist, use default
        if (userName == null || userName.isEmpty()) {
            userName = context.getString(R.string.no_name);
        }

        //get first letter from name or default
        firstLetter = userName.charAt(0);

        holder.userNameTextView.setText(userName);
        holder.firstLetterTextView.setText(String.valueOf(firstLetter));
        holder.messagePreviewTextView.setText(messagePreview);
        holder.timestampTextView.setText(timeString);

    }

    @Override
    public int getItemCount() {
        if (chatMetaDataList == null) return 0;
        return chatMetaDataList.size();
    }

    class ChatroomsViewholder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView userNameTextView;
        TextView messagePreviewTextView;
        TextView firstLetterTextView;
        TextView timestampTextView;

        public ChatroomsViewholder(View itemView) {
            super(itemView);
            userNameTextView = itemView.findViewById(R.id.textView_userName);
            messagePreviewTextView = itemView.findViewById(R.id.textView_message_preview);
            firstLetterTextView = itemView.findViewById(R.id.textView_firstLetter);
            timestampTextView = itemView.findViewById(R.id.textView_timestamp);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int position = getAdapterPosition();
            ChatMetaData metaData = chatMetaDataList.get(position);
            chatRoomClickedListener.onChatRoomClicked(metaData);
        }
    }
}
