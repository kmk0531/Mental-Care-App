package com.example.tp;

import android.os.Bundle;
import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import java.util.List;

public class HistoryActivity extends AppCompatActivity{
    private RecyclerView recyclerView;
    private ResultAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        ImageView backButton = findViewById(R.id.back_button);
        backButton.setOnClickListener(v -> finish());

        recyclerView = findViewById(R.id.recycler_results);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        AppDatabase db = Room.databaseBuilder(getApplicationContext(),
                        AppDatabase.class, "result_db")
                .allowMainThreadQueries()
                .build();

        List<ResultRecord> recordList = db.resultDao().getAll();
        adapter = new ResultAdapter(recordList);
        recyclerView.setAdapter(adapter);
    }
}
