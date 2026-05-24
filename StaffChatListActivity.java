package com.example.healthconsultapp;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.example.healthconsultapp.database.DatabaseHelper;
import com.example.healthconsultapp.databinding.ActivityStaffChatListBinding;
import java.util.ArrayList;
import java.util.List;

public class StaffChatListActivity extends AppCompatActivity implements UserChatListAdapter.OnUserClickListener {

    private ActivityStaffChatListBinding binding;
    private DatabaseHelper dbHelper;
    private UserChatListAdapter adapter;
    private final List<UserChatItem> userList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityStaffChatListBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        dbHelper = new DatabaseHelper(this);

        setupRecyclerView();
        loadUserChatList();
    }

    private void setupRecyclerView() {
        adapter = new UserChatListAdapter(userList, this);
        binding.recyclerUserList.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerUserList.setAdapter(adapter);
    }

    private void loadUserChatList() {
        userList.clear();
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        // Lấy danh sách user có tin nhắn + tin nhắn cuối cùng
        String query = "SELECT u.id, u.username, u.full_name, " +
                "       c.message, c.timestamp " +
                "FROM users u " +
                "INNER JOIN chat_messages c ON u.id = c.user_id " +
                "WHERE c.id IN (SELECT MAX(id) FROM chat_messages GROUP BY user_id) " +
                "ORDER BY c.timestamp DESC";

        Cursor cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            do {
                int userId = cursor.getInt(0);
                String username = cursor.getString(1);
                String fullName = cursor.getString(2);
                String lastMessage = cursor.getString(3);
                String lastTime = cursor.getString(4);

                userList.add(new UserChatItem(userId, username, fullName, lastMessage, lastTime));
            } while (cursor.moveToNext());
        }
        cursor.close();
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onUserClick(UserChatItem user) {
        // Khi STAFF chọn 1 người dùng → mở chat riêng
        Intent intent = new Intent(StaffChatListActivity.this, ChatWithStaffActivity.class);
        intent.putExtra("targetUserId", user.getUserId());
        startActivity(intent);
    }
}