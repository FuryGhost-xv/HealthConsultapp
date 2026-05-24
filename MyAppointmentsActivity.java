package com.example.healthconsultapp;

import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.example.healthconsultapp.database.DatabaseHelper;
import com.example.healthconsultapp.databinding.ActivityMyAppointmentsBinding;
import java.util.ArrayList;
import java.util.List;

public class MyAppointmentsActivity extends AppCompatActivity {

    private ActivityMyAppointmentsBinding binding;
    private DatabaseHelper dbHelper;
    private AppointmentAdapter adapter;
    private List<AppointmentItem> appointmentList = new ArrayList<>();
    private int currentUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMyAppointmentsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        dbHelper = new DatabaseHelper(this);

        SharedPreferences prefs = getSharedPreferences("user_session", MODE_PRIVATE);
        currentUserId = prefs.getInt("user_id", -1);

        setupRecyclerView();
        loadAppointments();

        binding.btnBack.setOnClickListener(v -> finish());
    }

    private void setupRecyclerView() {
        adapter = new AppointmentAdapter(appointmentList);
        binding.recyclerAppointments.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerAppointments.setAdapter(adapter);
    }

    private void loadAppointments() {
        appointmentList.clear();
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String query = "SELECT a.id, d.full_name, a.appointment_date, a.appointment_time, " +
                "a.room, a.status FROM " + DatabaseHelper.TABLE_APPOINTMENTS + " a " +
                "JOIN " + DatabaseHelper.TABLE_DOCTORS + " d ON a.doctor_id = d.id " +
                "WHERE a.user_id = ? ORDER BY a.appointment_date DESC";

        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(currentUserId)});

        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(0);
                String doctor = cursor.getString(1);
                String date = cursor.getString(2);
                String time = cursor.getString(3);
                String room = cursor.getString(4);
                String status = cursor.getString(5);

                appointmentList.add(new AppointmentItem(id, doctor, date, time, room, status));
            } while (cursor.moveToNext());
        }
        cursor.close();
        adapter.notifyDataSetChanged();

        if (appointmentList.isEmpty()) {
            Toast.makeText(this, "Bạn chưa có lịch khám nào!", Toast.LENGTH_LONG).show();
        }
    }
}