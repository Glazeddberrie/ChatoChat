package com.chato.chatochat;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.chato.chatochat.models.Chat;

import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ChatViewHolder> {
    private List<Chat> chatList;

    public ChatAdapter(List<Chat> chatList) {
        this.chatList = chatList;
    }

    @Override
    public ChatViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat, parent, false);
        return new ChatViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ChatViewHolder holder, int position) {
        Chat chat = chatList.get(position);
        holder.chatNameTextView.setText(chat.getChatName());
        holder.lastMessageTextView.setText(chat.getLastMessage());
    }

    @Override
    public int getItemCount() {
        return chatList.size();
    }

    public static class ChatViewHolder extends RecyclerView.ViewHolder {
        public TextView chatNameTextView;
        public TextView lastMessageTextView;

        public ChatViewHolder(View itemView) {
            super(itemView);
            chatNameTextView = itemView.findViewById(R.id.chatNameTextView);
            lastMessageTextView = itemView.findViewById(R.id.lastMessageTextView);
        }
    }
}
