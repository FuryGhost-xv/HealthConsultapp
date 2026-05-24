package com.example.healthconsultapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class AppointmentAdapter extends RecyclerView.Adapter<AppointmentAdapter.AppointmentViewHolder> {

    private final List<AppointmentItem> appointmentList;

    public AppointmentAdapter(List<AppointmentItem> appointmentList) {
        this.appointmentList = appointmentList;
    }

    @NonNull
    @Override
    public AppointmentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_appointment, parent, false);
        return new AppointmentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AppointmentViewHolder holder, int position) {
        AppointmentItem item = appointmentList.get(position);
        holder.tvDoctor.setText("Bác sĩ: " + item.getDoctorName());
        holder.tvDateTime.setText("Ngày: " + item.getDate() + " | Giờ: " + item.getTime());
        holder.tvRoom.setText("Phòng: " + item.getRoom());
        holder.tvStatus.setText("Trạng thái: " + item.getStatus());
    }

    @Override
    public int getItemCount() {
        return appointmentList.size();
    }

    static class AppointmentViewHolder extends RecyclerView.ViewHolder {
        TextView tvDoctor, tvDateTime, tvRoom, tvStatus;

        public AppointmentViewHolder(@NonNull View itemView) {
            super(itemView);
            tvDoctor = itemView.findViewById(R.id.tvDoctor);
            tvDateTime = itemView.findViewById(R.id.tvDateTime);
            tvRoom = itemView.findViewById(R.id.tvRoom);
            tvStatus = itemView.findViewById(R.id.tvStatus);
        }
    }
}