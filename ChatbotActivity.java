package com.example.healthconsultapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.example.healthconsultapp.database.DatabaseHelper;
import com.example.healthconsultapp.databinding.ActivityChatbotBinding;
import java.util.ArrayList;
import java.util.List;

public class ChatbotActivity extends AppCompatActivity {

    private ActivityChatbotBinding binding;
    private DatabaseHelper dbHelper;
    private ChatAdapter chatAdapter;
    private List<ChatMessage> messageList = new ArrayList<>();
    private int currentUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChatbotBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        dbHelper = new DatabaseHelper(this);

        SharedPreferences prefs = getSharedPreferences("user_session", MODE_PRIVATE);
        currentUserId = prefs.getInt("user_id", -1);

        setupRecyclerView();
        setupRatingButtons();
    }

    private void setupRecyclerView() {
        chatAdapter = new ChatAdapter(messageList);
        binding.recyclerChat.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerChat.setAdapter(chatAdapter);

        addBotMessage("Chào bạn! Tôi là chatbot tư vấn sức khỏe. Bạn đang gặp vấn đề gì?");
    }

    private void setupRatingButtons() {
        binding.btnSend.setOnClickListener(v -> sendMessage());

        binding.btnSatisfied.setOnClickListener(v -> {
            Toast.makeText(this, "Cảm ơn bạn đã đánh giá 👍", Toast.LENGTH_SHORT).show();
            hideRating();
        });

        binding.btnNotSatisfied.setOnClickListener(v -> {
            Toast.makeText(this, "Chúng tôi sẽ cải thiện!", Toast.LENGTH_SHORT).show();
            binding.btnContactPharmacist.setVisibility(View.VISIBLE);
            hideRating();
        });

        // NÚT LIÊN HỆ DƯỢC SĨ
        binding.btnContactPharmacist.setOnClickListener(v -> {
            try {
                Intent intent = new Intent(ChatbotActivity.this, ChatWithStaffActivity.class);
                startActivity(intent);
            } catch (Exception e) {
                Toast.makeText(this, "Lỗi mở chat: " + e.getMessage(), Toast.LENGTH_LONG).show();
                e.printStackTrace();
            }
        });
    }

    private void sendMessage() {
        String userMessage = binding.etMessage.getText().toString().trim();
        if (userMessage.isEmpty()) return;

        messageList.add(new ChatMessage(userMessage, true));
        chatAdapter.notifyItemInserted(messageList.size() - 1);
        binding.recyclerChat.scrollToPosition(messageList.size() - 1);
        binding.etMessage.setText("");

        String botReply = getBotReply(userMessage);
        addBotMessage(botReply);
        showRating();
    }

    private String getBotReply(String userMessage) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String searchMessage = userMessage.toLowerCase().trim();

        // 1. Tìm theo cột question trước
        String query = "SELECT answer, keywords FROM " + DatabaseHelper.TABLE_QA_PAIRS;
        Cursor cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            do {
                String answer = cursor.getString(0);
                String keywords = cursor.getString(1);

                // Kiểm tra câu hỏi mẫu
                if (cursor.getString(0).toLowerCase().contains(searchMessage)) {
                    cursor.close();
                    return answer;
                }

                // Kiểm tra từ khóa (hỗ trợ nhiều từ khóa cách nhau dấu phẩy)
                if (keywords != null && !keywords.isEmpty()) {
                    String[] keywordArray = keywords.toLowerCase().split(",");
                    for (String keyword : keywordArray) {
                        keyword = keyword.trim();
                        if (!keyword.isEmpty() && searchMessage.contains(keyword)) {
                            cursor.close();
                            return answer;
                        }
                    }
                }
            } while (cursor.moveToNext());
        }
        cursor.close();

        // Không tìm thấy
        return "Tôi chưa hiểu rõ câu hỏi của bạn. Bạn có thể mô tả chi tiết hơn được không?";
    }

    private void addBotMessage(String message) {
        messageList.add(new ChatMessage(message, false));
        chatAdapter.notifyItemInserted(messageList.size() - 1);
        binding.recyclerChat.scrollToPosition(messageList.size() - 1);
    }

    private void showRating() {
        binding.layoutRating.setVisibility(View.VISIBLE);
        binding.btnContactPharmacist.setVisibility(View.GONE);
    }

    private void hideRating() {
        binding.layoutRating.setVisibility(View.GONE);
    }
}