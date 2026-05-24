package com.example.healthconsultapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class QAAdapter extends RecyclerView.Adapter<QAAdapter.QAViewHolder> {

    private final List<QAItem> qaList;
    private final OnQAClickListener listener;

    public QAAdapter(List<QAItem> qaList, OnQAClickListener listener) {
        this.qaList = qaList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public QAViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_qa, parent, false);
        return new QAViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull QAViewHolder holder, int position) {
        QAItem item = qaList.get(position);
        holder.tvQuestion.setText("Câu hỏi: " + item.getQuestion());
        holder.tvAnswer.setText("Trả lời: " + item.getAnswer());

        holder.btnEdit.setOnClickListener(v -> listener.onEdit(item));
        holder.btnDelete.setOnClickListener(v -> listener.onDelete(item));
    }

    @Override
    public int getItemCount() {
        return qaList.size();
    }

    static class QAViewHolder extends RecyclerView.ViewHolder {
        TextView tvQuestion, tvAnswer;
        View btnEdit, btnDelete;

        public QAViewHolder(@NonNull View itemView) {
            super(itemView);
            tvQuestion = itemView.findViewById(R.id.tvQuestion);
            tvAnswer = itemView.findViewById(R.id.tvAnswer);
            btnEdit = itemView.findViewById(R.id.btnEdit);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }
    }

    public interface OnQAClickListener {
        void onEdit(QAItem item);
        void onDelete(QAItem item);
    }
}