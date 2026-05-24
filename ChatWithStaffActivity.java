package com.example.healthconsultapp;

import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.example.healthconsultapp.database.DatabaseHelper;
import com.example.healthconsultapp.databinding.ActivityChatWithStaffBinding;
import java.util.ArrayList;
import java.util.List;

public class ChatWithStaffActivity extends AppCompatActivity {

    private ActivityChatWithStaffBinding binding;
    private DatabaseHelper dbHelper;
    private ChatStaffAdapter adapter;
    private final List<ChatMessage> messageList = new ArrayList<>();
    private int currentUserId;
    private String currentRole;
    private int targetUserId;          // ID của bệnh nhân đang chat

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChatWithStaffBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        dbHelper = new DatabaseHelper(this);

        SharedPreferences prefs = getSharedPreferences("user_session", MODE_PRIVATE);
        currentUserId = prefs.getInt("user_id", -1);
        currentRole = prefs.getString("role", "USER");

        targetUserId = getIntent().getIntExtra("targetUserId", currentUserId);

        setupRecyclerView();
        loadChatHistory();

        // Nút xem thông tin bệnh nhân (chỉ STAFF/ADMIN thấy)
        if ("STAFF".equals(currentRole) || "ADMIN".equals(currentRole)) {
            binding.btnViewPatientInfo.setVisibility(View.VISIBLE);
            binding.btnViewPatientInfo.setOnClickListener(v -> showPatientInfo());
        }

        binding.btnSendStaff.setOnClickListener(v -> sendMessage());
    }

    private void setupRecyclerView() {
        adapter = new ChatStaffAdapter(messageList);
        binding.recyclerChatStaff.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerChatStaff.setAdapter(adapter);
    }

    private void loadChatHistory() {
        messageList.clear();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT message, is_from_user FROM chat_messages WHERE user_id = ? ORDER BY timestamp ASC",
                new String[]{String.valueOf(targetUserId)});

        if (cursor.moveToFirst()) {
            do {
                String msg = cursor.getString(0);
                boolean fromUser = cursor.getInt(1) == 1;
                messageList.add(new ChatMessage(msg, fromUser));
            } while (cursor.moveToNext());
        }
        cursor.close();
        adapter.notifyDataSetChanged();
    }

    private void sendMessage() {
        String text = binding.etMessageStaff.getText().toString().trim();
        if (text.isEmpty()) return;

        boolean isFromUser = !"STAFF".equals(currentRole) && !"ADMIN".equals(currentRole);

        SQLiteDatabase db = dbHelper.getWritableDatabase();
        android.content.ContentValues values = new android.content.ContentValues();
        values.put("user_id", targetUserId);
        values.put("message", text);
        values.put("is_from_user", isFromUser ? 1 : 0);
        db.insert("chat_messages", null, values);

        messageList.add(new ChatMessage(text, isFromUser));
        adapter.notifyItemInserted(messageList.size() - 1);
        binding.recyclerChatStaff.scrollToPosition(messageList.size() - 1);
        binding.etMessageStaff.setText("");
    }

    // ====================== XEM THÔNG TIN BỆNH NHÂN ======================
    private void showPatientInfo() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT full_name, email, phone, date_of_birth, medical_history " +
                        "FROM users WHERE id = ?",
                new String[]{String.valueOf(targetUserId)});

        if (cursor.moveToFirst()) {
            String fullName = cursor.getString(0);
            String email = cursor.getString(1);
            String phone = cursor.getString(2);
            String dob = cursor.getString(3);
            String medical = cursor.getString(4);

            String info = "Họ tên: " + fullName + "\n" +
                    "Email: " + email + "\n" +
                    "SĐT: " + (phone != null ? phone : "Chưa cập nhật") + "\n" +
                    "Ngày sinh: " + (dob != null ? dob : "Chưa cập nhật") + "\n\n" +
                    "Bệnh nền:\n" + (medical != null && !medical.isEmpty() ? medical : "Không có");

            new AlertDialog.Builder(this)
                    .setTitle("Thông tin bệnh nhân")
                    .setMessage(info)
                    .setPositiveButton("Đóng", null)
                    .show();
        } else {
            Toast.makeText(this, "Không tìm thấy thông tin bệnh nhân", Toast.LENGTH_SHORT).show();
        }
        cursor.close();
    }
}