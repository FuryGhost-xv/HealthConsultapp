package com.example.healthconsultapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class UserManagementAdapter extends RecyclerView.Adapter<UserManagementAdapter.UserViewHolder> {

    private final List<UserItem> userList;
    private final OnUserClickListener listener;

    public UserManagementAdapter(List<UserItem> userList, OnUserClickListener listener) {
        this.userList = userList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_user, parent, false);
        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        UserItem user = userList.get(position);
        holder.tvUsername.setText("Username: " + user.getUsername());
        holder.tvEmail.setText("Email: " + user.getEmail());
        holder.tvRole.setText("Role: " + user.getRole());

        holder.btnChangeRole.setOnClickListener(v -> listener.onChangeRole(user));
        holder.btnDelete.setOnClickListener(v -> listener.onDelete(user));
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    static class UserViewHolder extends RecyclerView.ViewHolder {
        TextView tvUsername, tvEmail, tvRole;
        View btnChangeRole, btnDelete;

        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            tvUsername = itemView.findViewById(R.id.tvUsername);
            tvEmail = itemView.findViewById(R.id.tvEmail);
            tvRole = itemView.findViewById(R.id.tvRole);
            btnChangeRole = itemView.findViewById(R.id.btnChangeRole);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }
    }

    public interface OnUserClickListener {
        void onChangeRole(UserItem user);
        void onDelete(UserItem user);
    }
}