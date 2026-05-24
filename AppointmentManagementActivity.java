package com.example.healthconsultapp;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.example.healthconsultapp.database.DatabaseHelper;
import com.example.healthconsultapp.databinding.ActivityAppointmentManagementBinding;
import java.util.ArrayList;
import java.util.List;

public class AppointmentManagementActivity extends AppCompatActivity
        implements AppointmentManagementAdapter.OnManagementClickListener {

    private ActivityAppointmentManagementBinding binding;
    private DatabaseHelper dbHelper;
    private AppointmentManagementAdapter adapter;
    private List<AppointmentItem> appointmentList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAppointmentManagementBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        dbHelper = new DatabaseHelper(this);

        setupRecyclerView();
        loadAllAppointments();

        binding.btnBack.setOnClickListener(v -> finish());
    }

    private void setupRecyclerView() {
        adapter = new AppointmentManagementAdapter(appointmentList, this);
        binding.recyclerAllAppointments.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerAllAppointments.setAdapter(adapter);
    }

    private void loadAllAppointments() {
        appointmentList.clear();
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        // SỬA LẠI: Dùng đúng tên cột trong database (date, time thay vì appointment_date, appointment_time)
        String query = "SELECT a.id, " +
                "d.full_name as doctor_name, " +
                "u.full_name as patient_name, " +
                "a.date, " +                    // ← Đổi từ appointment_date
                "a.time, " +                    // ← Đổi từ appointment_time
                "a.status " +
                "FROM " + DatabaseHelper.TABLE_APPOINTMENTS + " a " +
                "JOIN " + DatabaseHelper.TABLE_DOCTORS + " d ON a.doctor_id = d.id " +
                "JOIN " + DatabaseHelper.TABLE_USERS + " u ON a.user_id = u.id " +
                "ORDER BY a.date DESC, a.time DESC";

        Cursor cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow("id"));
                String doctorName = cursor.getString(cursor.getColumnIndexOrThrow("doctor_name"));
                String patientName = cursor.getString(cursor.getColumnIndexOrThrow("patient_name"));
                String date = cursor.getString(cursor.getColumnIndexOrThrow("date"));
                String time = cursor.getString(cursor.getColumnIndexOrThrow("time"));
                String status = cursor.getString(cursor.getColumnIndexOrThrow("status"));

                // Vì hiện tại chưa có cột room → để trống hoặc để "Phòng 1"
                String room = "Phòng khám";

                appointmentList.add(new AppointmentItem(id, doctorName, date, time, room, status));
            } while (cursor.moveToNext());
        }
        cursor.close();
        adapter.notifyDataSetChanged();
    }

    // Xử lý nút Xác nhận, Hủy, Xóa
    @Override
    public void onConfirm(AppointmentItem item) {
        updateAppointmentStatus(item.getId(), "CONFIRMED");
    }

    @Override
    public void onCancel(AppointmentItem item) {
        updateAppointmentStatus(item.getId(), "CANCELLED");
    }

    @Override
    public void onDelete(AppointmentItem item) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int rows = db.delete(DatabaseHelper.TABLE_APPOINTMENTS, "id = ?",
                new String[]{String.valueOf(item.getId())});
        if (rows > 0) {
            Toast.makeText(this, "Đã xóa lịch!", Toast.LENGTH_SHORT).show();
            loadAllAppointments();
        }
    }

    private void updateAppointmentStatus(int id, String newStatus) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        android.content.ContentValues values = new android.content.ContentValues();
        values.put("status", newStatus);

        int rows = db.update(DatabaseHelper.TABLE_APPOINTMENTS, values,
                "id = ?", new String[]{String.valueOf(id)});

        if (rows > 0) {
            Toast.makeText(this, "Cập nhật trạng thái thành công!", Toast.LENGTH_SHORT).show();

            // Gửi thông báo cho người dùng
            sendNotificationToUser(id, newStatus);

            loadAllAppointments();
        }
    }

    private void sendNotificationToUser(int appointmentId, String status) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        // Lấy user_id của lịch hẹn
        Cursor cursor = db.rawQuery("SELECT user_id FROM " + DatabaseHelper.TABLE_APPOINTMENTS +
                " WHERE id = ?", new String[]{String.valueOf(appointmentId)});

        if (cursor.moveToFirst()) {
            int userId = cursor.getInt(0);
            String title = "Cập nhật lịch khám";
            String message = "Lịch khám của bạn đã được " + (status.equals("CONFIRMED") ? "XÁC NHẬN" : "HỦY") + ".";

            android.content.ContentValues values = new android.content.ContentValues();
            values.put("user_id", userId);
            values.put("title", title);
            values.put("message", message);
            db.insert(DatabaseHelper.TABLE_NOTIFICATIONS, null, values);
        }
        cursor.close();
    }
}