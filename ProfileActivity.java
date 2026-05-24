package com.example.healthconsultapp;

import android.content.ContentValues;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.healthconsultapp.database.DatabaseHelper;
import com.example.healthconsultapp.databinding.ActivityProfileBinding;

import java.util.ArrayList;
import java.util.List;

public class ProfileActivity extends AppCompatActivity {

    private ActivityProfileBinding binding;
    private DatabaseHelper dbHelper;
    private int currentUserId;
    private String currentAvatarResource = "avatar_default"; // Avatar mặc định

    // Danh sách avatar có sẵn (bạn có thể thêm bớt)
    private final String[] avatarList = {
            "avatar_male_1", "avatar_male_2", "avatar_male_3",
            "avatar_female_1", "avatar_female_2", "avatar_female_3",
            "avatar_doctor_1", "avatar_nurse_1"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        dbHelper = new DatabaseHelper(this);

        SharedPreferences prefs = getSharedPreferences("user_session", MODE_PRIVATE);
        currentUserId = prefs.getInt("user_id", -1);

        if (currentUserId == -1) {
            Toast.makeText(this, "Không tìm thấy thông tin người dùng", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Nút chọn ảnh đại diện → Mở dialog chọn avatar có sẵn
        binding.btnChooseAvatar.setOnClickListener(v -> showAvatarChooserDialog());

        binding.btnUpdate.setOnClickListener(v -> updateProfile());
        binding.btnBack.setOnClickListener(v -> finish());

        loadUserProfile();
    }

    // ==================== HIỂN THỊ DIALOG CHỌN AVATAR ====================
    private void showAvatarChooserDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_choose_avatar, null);
        GridView gridView = view.findViewById(R.id.gridAvatar);

        List<Integer> avatarDrawables = new ArrayList<>();
        for (String name : avatarList) {
            int resId = getResources().getIdentifier(name, "drawable", getPackageName());
            if (resId != 0) {
                avatarDrawables.add(resId);
            }
        }

        AvatarAdapter adapter = new AvatarAdapter(this, avatarDrawables);
        gridView.setAdapter(adapter);

        AlertDialog dialog = builder.setView(view).create();

        gridView.setOnItemClickListener((parent, view1, position, id) -> {
            String selectedResourceName = avatarList[position];
            currentAvatarResource = selectedResourceName;

            // Hiển thị avatar ngay trên màn hình
            int resId = getResources().getIdentifier(selectedResourceName, "drawable", getPackageName());
            if (resId != 0) {
                binding.imgAvatar.setImageResource(resId);
            }

            // Lưu ngay vào database
            saveAvatarToDatabase(selectedResourceName);

            Toast.makeText(this, "Đã chọn ảnh đại diện", Toast.LENGTH_SHORT).show();
            dialog.dismiss();
        });

        dialog.show();
    }

    // ==================== LƯU AVATAR VÀO DATABASE ====================
    private void saveAvatarToDatabase(String avatarResourceName) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("avatar_path", avatarResourceName); // Lưu tên resource
        db.update(DatabaseHelper.TABLE_USERS, values, "id = ?",
                new String[]{String.valueOf(currentUserId)});
    }

    // ==================== LOAD THÔNG TIN NGƯỜI DÙNG ====================
    private void loadUserProfile() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + DatabaseHelper.TABLE_USERS + " WHERE id = ?",
                new String[]{String.valueOf(currentUserId)});

        if (cursor.moveToFirst()) {
            binding.etFullName.setText(getSafeString(cursor, "full_name"));
            binding.etEmail.setText(getSafeString(cursor, "email"));
            binding.etPhone.setText(getSafeString(cursor, "phone"));
            binding.etDateOfBirth.setText(getSafeString(cursor, "date_of_birth"));
            binding.etMedicalHistory.setText(getSafeString(cursor, "medical_history"));

            // ==================== LOAD AVATAR ====================
            int avatarIndex = cursor.getColumnIndex("avatar_path");
            if (avatarIndex != -1) {
                currentAvatarResource = cursor.getString(avatarIndex);

                if (currentAvatarResource != null && !currentAvatarResource.isEmpty()) {
                    int resId = getResources().getIdentifier(currentAvatarResource, "drawable", getPackageName());
                    if (resId != 0) {
                        binding.imgAvatar.setImageResource(resId);
                    } else {
                        // Không tìm thấy avatar → dùng avatar mặc định
                        binding.imgAvatar.setImageResource(R.drawable.avatar_default);
                    }
                } else {
                    // Chưa có avatar → dùng avatar mặc định
                    binding.imgAvatar.setImageResource(R.drawable.avatar_default);
                }
            } else {
                binding.imgAvatar.setImageResource(R.drawable.avatar_default);
            }
        }
        cursor.close();
    }

    // ==================== CẬP NHẬT THÔNG TIN ====================
    private void updateProfile() {
        String fullName = binding.etFullName.getText().toString().trim();
        if (fullName.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập họ và tên", Toast.LENGTH_SHORT).show();
            return;
        }

        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("full_name", fullName);
        values.put("email", binding.etEmail.getText().toString().trim());
        values.put("phone", binding.etPhone.getText().toString().trim());
        values.put("date_of_birth", binding.etDateOfBirth.getText().toString().trim());
        values.put("medical_history", binding.etMedicalHistory.getText().toString().trim());
        values.put("avatar_path", currentAvatarResource);

        int rows = db.update(DatabaseHelper.TABLE_USERS, values,
                "id = ?", new String[]{String.valueOf(currentUserId)});

        if (rows > 0) {
            Toast.makeText(this, "✅ Cập nhật thông tin thành công!", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "❌ Cập nhật thất bại!", Toast.LENGTH_SHORT).show();
        }
    }

    private String getSafeString(Cursor cursor, String columnName) {
        int index = cursor.getColumnIndex(columnName);
        return (index != -1) ? cursor.getString(index) : "";
    }
}