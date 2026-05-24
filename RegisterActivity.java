package com.example.healthconsultapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.healthconsultapp.database.DatabaseHelper;
import com.example.healthconsultapp.databinding.ActivityRegisterBinding;

public class RegisterActivity extends AppCompatActivity {

    private ActivityRegisterBinding binding;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRegisterBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        dbHelper = new DatabaseHelper(this);

        binding.btnRegister.setOnClickListener(v -> registerUser());
        binding.tvLogin.setOnClickListener(v -> {
            startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
            finish();
        });
    }

    private void registerUser() {
        String fullName = binding.etFullName.getText().toString().trim();
        String email = binding.etEmail.getText().toString().trim();
        String username = binding.etUsername.getText().toString().trim();
        String password = binding.etPassword.getText().toString().trim();
        String phone = binding.etPhone.getText().toString().trim();

        if (fullName.isEmpty() || email.isEmpty() || username.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Vui lòng điền đầy đủ thông tin!", Toast.LENGTH_SHORT).show();
            return;
        }

        String hashedPassword = DatabaseHelper.hashPassword(password);

        long result = dbHelper.getWritableDatabase().insert(
                DatabaseHelper.TABLE_USERS, null,
                createUserValues(username, email, hashedPassword, fullName, phone, "USER")
        );

        if (result != -1) {
            Toast.makeText(this, "Đăng ký thành công! Hãy đăng nhập.", Toast.LENGTH_LONG).show();
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        } else {
            Toast.makeText(this, "Tên đăng nhập hoặc email đã tồn tại!", Toast.LENGTH_SHORT).show();
        }
    }

    private android.content.ContentValues createUserValues(String username, String email,
                                                           String passwordHash, String fullName, String phone, String role) {
        android.content.ContentValues values = new android.content.ContentValues();
        values.put("username", username);
        values.put("email", email);
        values.put("password_hash", passwordHash);
        values.put("full_name", fullName);
        values.put("phone", phone);
        values.put("role", role);
        return values;
    }
}