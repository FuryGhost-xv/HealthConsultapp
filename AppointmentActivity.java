package com.example.healthconsultapp;

import android.app.DatePickerDialog;
import android.content.ContentValues;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.healthconsultapp.database.DatabaseHelper;
import com.example.healthconsultapp.databinding.ActivityAppointmentBinding;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class AppointmentActivity extends AppCompatActivity {

    private ActivityAppointmentBinding binding;
    private DatabaseHelper dbHelper;
    private int currentUserId;

    private List<String> departmentList = new ArrayList<>();
    private List<String> doctorList = new ArrayList<>();
    private List<Integer> doctorIds = new ArrayList<>();
    private String selectedDepartment = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAppointmentBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        dbHelper = new DatabaseHelper(this);

        SharedPreferences prefs = getSharedPreferences("user_session", MODE_PRIVATE);
        currentUserId = prefs.getInt("user_id", -1);

        loadDepartments();
        setupTimeSpinner();

        binding.spinnerDepartment.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(android.widget.AdapterView<?> parent, android.view.View view, int position, long id) {
                selectedDepartment = departmentList.get(position);
                loadDoctorsByDepartment(selectedDepartment);
            }
            @Override
            public void onNothingSelected(android.widget.AdapterView<?> parent) {}
        });

        binding.etDate.setOnClickListener(v -> showDatePicker());

        binding.btnBookAppointment.setOnClickListener(v -> bookAppointment());
        binding.btnBack.setOnClickListener(v -> finish());
    }

    private void loadDepartments() {
        departmentList.clear();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT DISTINCT specialty FROM " + DatabaseHelper.TABLE_DOCTORS, null);

        if (cursor.moveToFirst()) {
            do {
                departmentList.add(cursor.getString(0));
            } while (cursor.moveToNext());
        }
        cursor.close();

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, departmentList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.spinnerDepartment.setAdapter(adapter);
    }

    private void loadDoctorsByDepartment(String department) {
        doctorList.clear();
        doctorIds.clear();

        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT id, full_name FROM " + DatabaseHelper.TABLE_DOCTORS +
                " WHERE specialty = ?", new String[]{department});

        if (cursor.moveToFirst()) {
            do {
                doctorIds.add(cursor.getInt(0));
                doctorList.add(cursor.getString(1));
            } while (cursor.moveToNext());
        }
        cursor.close();

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, doctorList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.spinnerDoctor.setAdapter(adapter);
    }

    private void setupTimeSpinner() {
        List<String> times = new ArrayList<>();
        for (int i = 8; i <= 17; i++) {
            times.add(String.format("%02d:00", i));
            times.add(String.format("%02d:30", i));
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, times);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.spinnerTime.setAdapter(adapter);
    }

    private void showDatePicker() {
        Calendar calendar = Calendar.getInstance();
        DatePickerDialog datePicker = new DatePickerDialog(this,
                (view, year, month, dayOfMonth) -> {
                    String selectedDate = year + "-" + String.format("%02d", month + 1) + "-" + String.format("%02d", dayOfMonth);
                    binding.etDate.setText(selectedDate);
                },
                calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        datePicker.show();
    }

    private void bookAppointment() {
        if (currentUserId == -1) {
            Toast.makeText(this, "Vui lòng đăng nhập lại!", Toast.LENGTH_SHORT).show();
            return;
        }

        int doctorPosition = binding.spinnerDoctor.getSelectedItemPosition();
        if (doctorPosition < 0 || doctorList.isEmpty()) {
            Toast.makeText(this, "Vui lòng chọn bác sĩ!", Toast.LENGTH_SHORT).show();
            return;
        }

        int doctorId = doctorIds.get(doctorPosition);
        String date = binding.etDate.getText().toString().trim();
        String time = binding.spinnerTime.getSelectedItem().toString();
        String room = selectedDepartment;   // Dùng khoa làm room

        if (date.isEmpty()) {
            Toast.makeText(this, "Vui lòng chọn ngày hẹn!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (isAppointmentConflict(doctorId, date, time)) {
            Toast.makeText(this, "Lịch này đã có người đặt! Vui lòng chọn thời gian khác.", Toast.LENGTH_LONG).show();
            return;
        }

        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("user_id", currentUserId);
        values.put("doctor_id", doctorId);
        values.put("appointment_date", date);
        values.put("appointment_time", time);
        values.put("room", room);
        values.put("status", "PENDING");
        values.put("payment_status", "UNPAID");

        long result = db.insert(DatabaseHelper.TABLE_APPOINTMENTS, null, values);

        if (result != -1) {
            Toast.makeText(this, "✅ Đặt lịch thành công!", Toast.LENGTH_LONG).show();
            finish();
        } else {
            Toast.makeText(this, "Đặt lịch thất bại!", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean isAppointmentConflict(int doctorId, String date, String time) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String query = "SELECT * FROM " + DatabaseHelper.TABLE_APPOINTMENTS +
                " WHERE doctor_id = ? AND appointment_date = ? AND appointment_time = ? AND status != 'CANCELLED'";
        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(doctorId), date, time});
        boolean exists = cursor.moveToFirst();
        cursor.close();
        return exists;
    }
}