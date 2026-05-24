package com.example.healthconsultapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class UserChatListAdapter extends RecyclerView.Adapter<UserChatListAdapter.UserViewHolder> {

    private final List<UserChatItem> userList;
    private final OnUserClickListener listener;

    public UserChatListAdapter(List<UserChatItem> userList, OnUserClickListener listener) {
        this.userList = userList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_user_chat, parent, false);
        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        UserChatItem user = userList.get(position);
        holder.tvUserName.setText(user.getFullName() + " (" + user.getUsername() + ")");
        holder.tvLastMessage.setText(user.getLastMessage());
        holder.tvLastTime.setText(user.getLastMessageTime());

        holder.itemView.setOnClickListener(v -> listener.onUserClick(user));
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    static class UserViewHolder extends RecyclerView.ViewHolder {
        TextView tvUserName, tvLastMessage, tvLastTime;

        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            tvUserName = itemView.findViewById(R.id.tvUserName);
            tvLastMessage = itemView.findViewById(R.id.tvLastMessage);
            tvLastTime = itemView.findViewById(R.id.tvLastTime);
        }
    }

    public interface OnUserClickListener {
        void onUserClick(UserChatItem user);
    }
}