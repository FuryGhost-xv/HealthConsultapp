package com.example.healthconsultapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class AppointmentManagementAdapter extends RecyclerView.Adapter<AppointmentManagementAdapter.ViewHolder> {

    private final List<AppointmentItem> list;
    private final OnManagementClickListener listener;

    public AppointmentManagementAdapter(List<AppointmentItem> list, OnManagementClickListener listener) {
        this.list = list;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_appointment_management, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        AppointmentItem item = list.get(position);
        holder.tvPatient.setText("Bệnh nhân: " + item.getDoctorName()); // tạm dùng doctorName để hiển thị bệnh nhân (sau có thể join user)
        holder.tvDoctorDateTime.setText("Bác sĩ: " + item.getDoctorName() + " | " + item.getDate() + " " + item.getTime());
        holder.tvRoom.setText("Phòng: " + item.getRoom());
        holder.tvStatus.setText("Trạng thái: " + item.getStatus());

        holder.btnConfirm.setOnClickListener(v -> listener.onConfirm(item));
        holder.btnCancel.setOnClickListener(v -> listener.onCancel(item));
        holder.btnDelete.setOnClickListener(v -> listener.onDelete(item));
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvPatient, tvDoctorDateTime, tvRoom, tvStatus;
        View btnConfirm, btnCancel, btnDelete;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvPatient = itemView.findViewById(R.id.tvPatient);
            tvDoctorDateTime = itemView.findViewById(R.id.tvDoctorDateTime);
            tvRoom = itemView.findViewById(R.id.tvRoom);
            tvStatus = itemView.findViewById(R.id.tvStatus);
            btnConfirm = itemView.findViewById(R.id.btnConfirm);
            btnCancel = itemView.findViewById(R.id.btnCancel);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }
    }

    public interface OnManagementClickListener {
        void onConfirm(AppointmentItem item);
        void onCancel(AppointmentItem item);
        void onDelete(AppointmentItem item);
    }
}