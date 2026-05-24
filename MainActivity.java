package com.example.healthconsultapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.example.healthconsultapp.database.DatabaseHelper;
import com.example.healthconsultapp.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private DatabaseHelper dbHelper;
    private String currentRole;
    private int currentUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        dbHelper = new DatabaseHelper(this);

        // Lấy thông tin người dùng từ SharedPreferences
        SharedPreferences prefs = getSharedPreferences("user_session", MODE_PRIVATE);
        currentUserId = prefs.getInt("user_id", -1);
        String username = prefs.getString("username", "Người dùng");
        currentRole = prefs.getString("role", "USER");

        // Hiển thị lời chào
        binding.tvWelcome.setText("Xin chào, " + username + "!");
        binding.tvRole.setText("Vai trò: " + currentRole);

        // Load avatar ở header


        // Thiết lập giao diện theo vai trò
        setupRoleBasedUI();

        // ==================== XỬ LÝ SỰ KIỆN CLICK ====================

        // Chat với Chatbot
        binding.cardChatbot.setOnClickListener(v -> {
            if ("ADMIN".equals(currentRole) || "STAFF".equals(currentRole)) {
                startActivity(new Intent(this, AdminActivity.class));
            } else {
                startActivity(new Intent(this, ChatbotActivity.class));
            }
        });

        // Banner Chat với Dược sĩ (toàn bộ card)
        binding.cardChatWithUsers.setOnClickListener(v -> {
            if ("ADMIN".equals(currentRole) || "STAFF".equals(currentRole)) {
                startActivity(new Intent(this, StaffChatListActivity.class));
            } else {
                startActivity(new Intent(this, ChatWithStaffActivity.class));
            }
        });

        // Nút "Chat ngay" trên banner
        binding.btnChatNow.setOnClickListener(v -> {
            if ("ADMIN".equals(currentRole) || "STAFF".equals(currentRole)) {
                startActivity(new Intent(this, StaffChatListActivity.class));
            } else {
                startActivity(new Intent(this, ChatWithStaffActivity.class));
            }
        });

        // Đặt lịch khám
        binding.cardBookAppointment.setOnClickListener(v ->
                startActivity(new Intent(this, AppointmentActivity.class)));

        // Xem lịch của tôi
        binding.cardMyAppointments.setOnClickListener(v ->
                startActivity(new Intent(this, MyAppointmentsActivity.class)));

        // Hồ sơ cá nhân
        binding.cardProfile.setOnClickListener(v ->
                startActivity(new Intent(this, ProfileActivity.class)));

        // Quản lý lịch khám (chỉ hiện với ADMIN & STAFF)
        binding.cardManageAppointments.setOnClickListener(v ->
                startActivity(new Intent(this, AppointmentManagementActivity.class)));

        // Đăng xuất
        binding.btnLogout.setOnClickListener(v -> logout());
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Cập nhật avatar khi quay lại màn hình

    }

    /**
     * Thiết lập giao diện theo vai trò người dùng
     */
    private void setupRoleBasedUI() {
        if ("ADMIN".equals(currentRole) || "STAFF".equals(currentRole)) {
            // Hiện nút Quản lý lịch khám
            binding.cardManageAppointments.setVisibility(View.VISIBLE);

            // Đổi text banner Chat
            binding.tvChatStaffTitle.setText("Quản lý Chat với Người dùng");
            binding.tvChatStaffDesc.setText("Xem và trả lời tin nhắn từ người dùng");
        } else {
            // Ẩn nút Quản lý lịch khám
            binding.cardManageAppointments.setVisibility(View.GONE);

            // Text mặc định cho User
            binding.tvChatStaffTitle.setText("Chat với Dược sĩ");
            binding.tvChatStaffDesc.setText("Được tư vấn trực tiếp từ dược sĩ chuyên môn");
        }
    }

    /**
     * Load avatar người dùng ở header
     */


    private void logout() {
        SharedPreferences prefs = getSharedPreferences("user_session", MODE_PRIVATE);
        prefs.edit().clear().apply();
        startActivity(new Intent(this, LoginActivity.class));
        finish();
    }
}