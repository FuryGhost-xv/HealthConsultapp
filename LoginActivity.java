package com.example.healthconsultapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.healthconsultapp.database.DatabaseHelper;
import com.example.healthconsultapp.databinding.ActivityLoginBinding;

public class LoginActivity extends AppCompatActivity {

    private ActivityLoginBinding binding;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Dùng Singleton thay vì new trực tiếp
        dbHelper = new DatabaseHelper(this);

        binding.btnLogin.setOnClickListener(v -> loginUser());
        binding.tvRegister.setOnClickListener(v -> {
            startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
            finish();
        });
    }

    private void loginUser() {
        String input = binding.etUsername.getText().toString().trim();   // username hoặc email
        String password = binding.etPassword.getText().toString().trim();

        if (input.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin!", Toast.LENGTH_SHORT).show();
            return;
        }

        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String query = "SELECT * FROM " + DatabaseHelper.TABLE_USERS +
                " WHERE (username = ? OR email = ?)";

        Cursor cursor = db.rawQuery(query, new String[]{input, input});

        if (cursor.moveToFirst()) {
            String storedHash = cursor.getString(cursor.getColumnIndexOrThrow("password_hash"));
            String role = cursor.getString(cursor.getColumnIndexOrThrow("role"));
            int userId = cursor.getInt(cursor.getColumnIndexOrThrow("id"));
            String username = cursor.getString(cursor.getColumnIndexOrThrow("username"));

            if (DatabaseHelper.checkPassword(password, storedHash)) {
                // Lưu session người dùng
                saveLoginSession(userId, username, role);

                Toast.makeText(this, "Đăng nhập thành công! Vai trò: " + role, Toast.LENGTH_LONG).show();

                // Chuyển sang màn hình chính
                startActivity(new Intent(this, MainActivity.class));
                finish();
            } else {
                Toast.makeText(this, "Mật khẩu không đúng!", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Tài khoản không tồn tại!", Toast.LENGTH_SHORT).show();
        }
        cursor.close();
    }

    private void saveLoginSession(int userId, String username, String role) {
        SharedPreferences prefs = getSharedPreferences("user_session", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt("user_id", userId);
        editor.putString("username", username);
        editor.putString("role", role);
        editor.apply();
    }
}