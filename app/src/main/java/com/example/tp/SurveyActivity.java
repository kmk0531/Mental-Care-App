package com.example.tp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class SurveyActivity extends AppCompatActivity{
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_survey);

        LinearLayout layoutPhq9 = findViewById(R.id.button_phq9);
        LinearLayout layoutGad7 = findViewById(R.id.button_gad7);
        LinearLayout layoutStress = findViewById(R.id.button_pss);
        LinearLayout layoutHistory = findViewById(R.id.button_history);
        ImageView backBtn = findViewById(R.id.back_button);

        backBtn.setOnClickListener(v->finish());
        layoutPhq9.setOnClickListener(v -> startActivity(new Intent(this, PagedPHQ9Activity.class)));
        layoutGad7.setOnClickListener(v -> startActivity(new Intent(this, PagedGAD7Activity.class)));
        layoutStress.setOnClickListener(v -> startActivity(new Intent(this, PagedPSSActivity.class)));
        layoutHistory.setOnClickListener(v -> startActivity(new Intent(this, HistoryActivity.class)));
    }
}
