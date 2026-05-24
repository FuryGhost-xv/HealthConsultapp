package com.example.healthconsultapp;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.example.healthconsultapp.database.DatabaseHelper;
import com.example.healthconsultapp.databinding.ActivityUserManagementBinding;
import java.util.ArrayList;
import java.util.List;

public class UserManagementActivity extends AppCompatActivity implements UserManagementAdapter.OnUserClickListener {

    private ActivityUserManagementBinding binding;
    private DatabaseHelper dbHelper;
    private UserManagementAdapter adapter;
    private List<UserItem> userList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityUserManagementBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        dbHelper = new DatabaseHelper(this);

        setupRecyclerView();
        loadAllUsers();

        binding.btnAddUser.setOnClickListener(v -> showAddUserDialog());
        binding.btnBack.setOnClickListener(v -> finish());
    }

    private void setupRecyclerView() {
        adapter = new UserManagementAdapter(userList, this);
        binding.recyclerUsers.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerUsers.setAdapter(adapter);
    }

    private void loadAllUsers() {
        userList.clear();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + DatabaseHelper.TABLE_USERS + " ORDER BY role DESC", null);

        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow("id"));
                String username = cursor.getString(cursor.getColumnIndexOrThrow("username"));
                String email = cursor.getString(cursor.getColumnIndexOrThrow("email"));
                String fullName = cursor.getString(cursor.getColumnIndexOrThrow("full_name"));
                String role = cursor.getString(cursor.getColumnIndexOrThrow("role"));

                userList.add(new UserItem(id, username, email, fullName, role));
            } while (cursor.moveToNext());
        }
        cursor.close();
        adapter.notifyDataSetChanged();
    }

    private void showAddUserDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_add_user, null);

        EditText etUsername = view.findViewById(R.id.etUsername);
        EditText etEmail = view.findViewById(R.id.etEmail);
        EditText etFullName = view.findViewById(R.id.etFullName);
        EditText etPassword = view.findViewById(R.id.etPassword);

        builder.setView(view)
                .setTitle("Thêm Người Dùng Mới (STAFF)")
                .setPositiveButton("Lưu", (dialog, which) -> {
                    String username = etUsername.getText().toString().trim();
                    String email = etEmail.getText().toString().trim();
                    String fullName = etFullName.getText().toString().trim();
                    String password = etPassword.getText().toString().trim();

                    if (username.isEmpty() || email.isEmpty() || password.isEmpty()) {
                        Toast.makeText(this, "Vui lòng điền đầy đủ!", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    String hashedPassword = DatabaseHelper.hashPassword(password);

                    ContentValues values = new ContentValues();
                    values.put("username", username);
                    values.put("email", email);
                    values.put("full_name", fullName);
                    values.put("password_hash", hashedPassword);
                    values.put("role", "STAFF");   // Mặc định là STAFF

                    long result = dbHelper.getWritableDatabase().insert(DatabaseHelper.TABLE_USERS, null, values);

                    if (result != -1) {
                        Toast.makeText(this, "Thêm STAFF thành công!", Toast.LENGTH_SHORT).show();
                        loadAllUsers();
                    } else {
                        Toast.makeText(this, "Username hoặc Email đã tồn tại!", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Hủy", null)
                .show();
    }

    @Override
    public void onChangeRole(UserItem user) {
        String newRole = user.getRole().equals("STAFF") ? "USER" : "STAFF";
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("role", newRole);

        db.update(DatabaseHelper.TABLE_USERS, values, "id = ?", new String[]{String.valueOf(user.getId())});
        Toast.makeText(this, "Đã đổi role thành " + newRole, Toast.LENGTH_SHORT).show();
        loadAllUsers();
    }

    @Override
    public void onDelete(UserItem user) {
        new AlertDialog.Builder(this)
                .setTitle("Xóa người dùng?")
                .setMessage("Bạn có chắc muốn xóa " + user.getUsername() + "?")
                .setPositiveButton("Xóa", (dialog, which) -> {
                    SQLiteDatabase db = dbHelper.getWritableDatabase();
                    db.delete(DatabaseHelper.TABLE_USERS, "id = ?", new String[]{String.valueOf(user.getId())});
                    Toast.makeText(this, "Đã xóa người dùng!", Toast.LENGTH_SHORT).show();
                    loadAllUsers();
                })
                .setNegativeButton("Hủy", null)
                .show();
    }
}