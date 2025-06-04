package com.example.tp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

public class ResultActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        TextView textScore = findViewById(R.id.text_score);
        TextView textDescription = findViewById(R.id.text_description);
        Button btnBackHome = findViewById(R.id.btn_back_home);

        // 인텐트에서 결과 정보 받기
        int score = getIntent().getIntExtra("score", 0);
        String description = getIntent().getStringExtra("description");
        String testType = getIntent().getStringExtra("testType"); // "PHQ-9", "GAD-7" 등

        textScore.setText("총점: " + score + "점");
        textDescription.setText(description);

        //여기서 Room에 저장
        AppDatabase db = Room.databaseBuilder(getApplicationContext(),
                        AppDatabase.class, "result_db")
                .allowMainThreadQueries()
                .build();

        ResultRecord record = new ResultRecord();
        record.testType = testType;
        record.score = score;
        record.description = description;
        record.timestamp = System.currentTimeMillis();

        db.resultDao().insert(record); // 저장 수행

        // 돌아가기 버튼 동작
        btnBackHome.setOnClickListener(v -> {
            Intent intent = new Intent(ResultActivity.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        });
    }
}