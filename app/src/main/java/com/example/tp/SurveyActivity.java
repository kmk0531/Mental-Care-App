package com.example.tp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class SurveyActivity extends AppCompatActivity{
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_survey);

        Button btnPhq9 = findViewById(R.id.button_phq9);
        Button btnGad7 = findViewById(R.id.button_gad7);
        Button btnStress = findViewById(R.id.button_pss);
        Button btnHistory = findViewById(R.id.button_history);

        btnPhq9.setOnClickListener(v -> startActivity(new Intent(this, PagedPHQ9Activity.class)));
        btnGad7.setOnClickListener(v -> startActivity(new Intent(this, PagedGAD7Activity.class)));
        btnStress.setOnClickListener(v -> startActivity(new Intent(this, PagedPSSActivity.class)));
        btnHistory.setOnClickListener(v -> startActivity(new Intent(this, HistoryActivity.class)));
    }
}
