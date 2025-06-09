package com.example.tp;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.LinearLayout;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;


public class MeditationActivity extends AppCompatActivity {
    LinearLayout meditationLink5, meditationLink10, meditationLink15, meditationLink30;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meditation);
        ImageView backbtn = findViewById(R.id.back_button);
        backbtn.setOnClickListener(v -> finish());
        meditationLink5 = findViewById(R.id.meditation_link_5);
        meditationLink10 = findViewById(R.id.meditation_link_10);
        meditationLink15 = findViewById(R.id.meditation_link_15);
        meditationLink30 = findViewById(R.id.meditation_link_30);



        meditationLink5.setOnClickListener(v -> openUrl("https://www.youtube.com/results?search_query=5분+명상"));
        meditationLink10.setOnClickListener(v -> openUrl("https://www.youtube.com/results?search_query=10분+명상"));
        meditationLink15.setOnClickListener(v -> openUrl("https://www.youtube.com/results?search_query=15분+명상"));
        meditationLink30.setOnClickListener(v -> openUrl("https://www.youtube.com/results?search_query=30분+명상"));

    }
    
    private void openUrl(String url) {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        startActivity(intent);
    }
}
