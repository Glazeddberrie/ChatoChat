package com.chato.chatochat;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.chato.chatochat.models.Chat;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ChatListAdapter extends RecyclerView.Adapter<ChatListAdapter.ChatViewHolder> {

    private List<Chat> chatList;
    private OnChatClickListener onChatClickListener;

    public interface OnChatClickListener {
        void onChatClick(Chat chat);
    }

    public ChatListAdapter(List<Chat> chatList, OnChatClickListener listener) {
        this.chatList = chatList;
        this.onChatClickListener = listener;
    }

    @NonNull
    @Override
    public ChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat, parent, false);
        return new ChatViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatViewHolder holder, int position) {
        Chat chat = chatList.get(position);
        holder.bind(chat, onChatClickListener);
    }

    @Override
    public int getItemCount() {
        return chatList.size();
    }

    public static class ChatViewHolder extends RecyclerView.ViewHolder {
        private TextView chatName;
        private TextView lastMessage;

        public ChatViewHolder(@NonNull View itemView) {
            super(itemView);
            chatName = itemView.findViewById(R.id.chatNameTextView);
            lastMessage = itemView.findViewById(R.id.lastMessageTextView);
        }

        public void bind(Chat chat, OnChatClickListener listener) {
            chatName.setText(chat.getChatName());
            lastMessage.setText(chat.getLastMessage());

            itemView.setOnClickListener(v -> listener.onChatClick(chat));
        }
    }
}