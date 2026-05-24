package com.example.healthconsultapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ChatStaffAdapter extends RecyclerView.Adapter<ChatStaffAdapter.ChatViewHolder> {

    private List<ChatMessage> messageList;

    public ChatStaffAdapter(List<ChatMessage> messageList) {
        this.messageList = messageList;
    }

    @NonNull
    @Override
    public ChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_chat_message, parent, false);
        return new ChatViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatViewHolder holder, int position) {
        ChatMessage message = messageList.get(position);

        if (message.isFromUser()) {
            // Tin nhắn của Người dùng
            holder.tvBotMessage.setVisibility(View.GONE);
            holder.tvUserMessage.setVisibility(View.VISIBLE);
            holder.tvUserMessage.setText(message.getMessage());
        } else {
            // Tin nhắn của Staff / Dược sĩ
            holder.tvUserMessage.setVisibility(View.GONE);
            holder.tvBotMessage.setVisibility(View.VISIBLE);
            holder.tvBotMessage.setText(message.getMessage());
        }
    }

    @Override
    public int getItemCount() {
        return messageList.size();
    }

    public static class ChatViewHolder extends RecyclerView.ViewHolder {
        TextView tvBotMessage, tvUserMessage;

        public ChatViewHolder(@NonNull View itemView) {
            super(itemView);
            tvBotMessage = itemView.findViewById(R.id.tvBotMessage);
            tvUserMessage = itemView.findViewById(R.id.tvUserMessage);
        }
    }
}