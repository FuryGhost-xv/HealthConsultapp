package com.example.healthconsultapp;

import android.content.ContentValues;
import android.content.Intent;
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
import com.example.healthconsultapp.databinding.ActivityAdminBinding;
import java.util.ArrayList;
import java.util.List;

public class AdminActivity extends AppCompatActivity implements QAAdapter.OnQAClickListener {

    private ActivityAdminBinding binding;
    private DatabaseHelper dbHelper;
    private QAAdapter qaAdapter;
    private List<QAItem> qaList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAdminBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        dbHelper = new DatabaseHelper(this);

        setupRecyclerView();
        loadQAList();

        // Nút Thêm Q&A mới
        binding.fabAddQA.setOnClickListener(v -> showAddQADialog());

        // Nút Quản lý Người dùng
        binding.btnManageUsers.setOnClickListener(v -> {
            startActivity(new Intent(AdminActivity.this, UserManagementActivity.class));
        });

        binding.btnBack.setOnClickListener(v -> finish());
    }

    private void setupRecyclerView() {
        qaAdapter = new QAAdapter(qaList, this);
        binding.recyclerQA.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerQA.setAdapter(qaAdapter);
    }

    private void loadQAList() {
        // Nút Thêm Q&A mới
        binding.fabAddQA.setOnClickListener(v -> showAddQADialog());
        qaList.clear();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + DatabaseHelper.TABLE_QA_PAIRS + " ORDER BY id DESC", null);

        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow("id"));
                String question = cursor.getString(cursor.getColumnIndexOrThrow("question"));
                String answer = cursor.getString(cursor.getColumnIndexOrThrow("answer"));
                qaList.add(new QAItem(id, question, answer));
            } while (cursor.moveToNext());
        }
        cursor.close();
        qaAdapter.notifyDataSetChanged();
    }



    // ==================== THÊM Q&A ====================
    private void showAddQADialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_add_qa, null);

        EditText etQuestion = view.findViewById(R.id.etQuestion);
        EditText etAnswer = view.findViewById(R.id.etAnswer);
        EditText etKeywords = view.findViewById(R.id.etKeywords);   // Thêm dòng này

        builder.setView(view)
                .setTitle("Thêm câu hỏi - trả lời mới")
                .setPositiveButton("Lưu", (dialog, which) -> {
                    String question = etQuestion.getText().toString().trim();
                    String answer = etAnswer.getText().toString().trim();
                    String keywords = etKeywords.getText().toString().trim();   // Thêm dòng này

                    if (!question.isEmpty() && !answer.isEmpty()) {
                        addNewQA(question, answer, keywords);
                    }
                })
                .setNegativeButton("Hủy", null)
                .show();
    }

    private void addNewQA(String question, String answer, String keywords) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("question", question);
        values.put("answer", answer);
        values.put("keywords", keywords);           // Thêm dòng này

        long result = db.insert(DatabaseHelper.TABLE_QA_PAIRS, null, values);
        if (result != -1) {
            Toast.makeText(this, "Thêm thành công!", Toast.LENGTH_SHORT).show();
            loadQAList();
        }
    }

    // ==================== SỬA Q&A ====================
    @Override
    public void onEdit(QAItem item) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_add_qa, null);

        EditText etQuestion = view.findViewById(R.id.etQuestion);
        EditText etAnswer = view.findViewById(R.id.etAnswer);
        EditText etKeywords = view.findViewById(R.id.etKeywords);   // Thêm dòng này

        etQuestion.setText(item.getQuestion());
        etAnswer.setText(item.getAnswer());

        // Lấy keywords hiện tại từ database
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT keywords FROM " + DatabaseHelper.TABLE_QA_PAIRS + " WHERE id = ?",
                new String[]{String.valueOf(item.getId())});
        if (cursor.moveToFirst()) {
            etKeywords.setText(cursor.getString(0));
        }
        cursor.close();

        builder.setView(view)
                .setTitle("Sửa câu hỏi - trả lời")
                .setPositiveButton("Cập nhật", (dialog, which) -> {
                    String newQ = etQuestion.getText().toString().trim();
                    String newA = etAnswer.getText().toString().trim();
                    String newKeywords = etKeywords.getText().toString().trim();

                    if (!newQ.isEmpty() && !newA.isEmpty()) {
                        updateQA(item.getId(), newQ, newA, newKeywords);
                    }
                })
                .setNegativeButton("Hủy", null)
                .show();
    }

    private void updateQA(int id, String question, String answer, String keywords) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("question", question);
        values.put("answer", answer);
        values.put("keywords", keywords);           // Thêm dòng này

        db.update(DatabaseHelper.TABLE_QA_PAIRS, values, "id = ?", new String[]{String.valueOf(id)});
        loadQAList();
    }

    // ==================== XÓA Q&A ====================
    @Override
    public void onDelete(QAItem item) {
        new AlertDialog.Builder(this)
                .setTitle("Xóa câu hỏi?")
                .setMessage("Bạn có chắc muốn xóa câu hỏi này không?")
                .setPositiveButton("Xóa", (dialog, which) -> {
                    SQLiteDatabase db = dbHelper.getWritableDatabase();
                    db.delete(DatabaseHelper.TABLE_QA_PAIRS, "id = ?", new String[]{String.valueOf(item.getId())});
                    loadQAList();
                })
                .setNegativeButton("Hủy", null)
                .show();
    }
}