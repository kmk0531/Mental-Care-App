package com.example.tp;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ResultAdapter extends RecyclerView.Adapter<ResultAdapter.ResultViewHolder>{
    private List<ResultRecord> recordList;
    private Context context;

    public ResultAdapter(List<ResultRecord> recordList) {
        this.recordList = recordList;
    }

    @NonNull
    @Override
    public ResultViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.item_result_card, parent, false);
        return new ResultViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ResultViewHolder holder, int position) {
        ResultRecord record = recordList.get(position);

        holder.testTypeText.setText(record.testType);
        holder.scoreText.setText("점수: " + record.score);
        holder.descriptionText.setText(record.description);

        String formattedDate = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
                .format(new Date(record.timestamp));
        holder.dateText.setText(formattedDate);

        holder.btnDelete.setOnClickListener(v -> {
            new AlertDialog.Builder(context)
                    .setTitle("삭제 확인")
                    .setMessage("이 결과를 삭제하시겠습니까?")
                    .setPositiveButton("삭제", (dialog, which) -> {
                        AppDatabase db = Room.databaseBuilder(context, AppDatabase.class, "result_db")
                                .allowMainThreadQueries()
                                .build();
                        db.resultDao().delete(record);
                        recordList.remove(position);
                        notifyItemRemoved(position);
                    })
                    .setNegativeButton("취소", null)
                    .show();
        });
    }

    @Override
    public int getItemCount() {
        return recordList.size();
    }

    static class ResultViewHolder extends RecyclerView.ViewHolder {
        TextView testTypeText, scoreText, descriptionText, dateText;
        ImageButton btnDelete;

        public ResultViewHolder(@NonNull View itemView) {
            super(itemView);
            testTypeText = itemView.findViewById(R.id.text_type);
            scoreText = itemView.findViewById(R.id.text_score);
            descriptionText = itemView.findViewById(R.id.text_description);
            dateText = itemView.findViewById(R.id.text_date);
            btnDelete = itemView.findViewById(R.id.button_delete);
        }
    }
}
