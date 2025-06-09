package com.example.tp;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.LinearLayout;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;


public class StretchingActivity extends AppCompatActivity{

    LinearLayout stretchingLink5, stretchingLink10, stretchingLink15, stretchingLink30;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stretching);
        ImageView backbtn = findViewById(R.id.back_button);
        stretchingLink5 = findViewById(R.id.stretching_link_5);
        stretchingLink10 = findViewById(R.id.stretching_link_10);
        stretchingLink15 = findViewById(R.id.stretching_link_15);
        stretchingLink30 = findViewById(R.id.stretching_link_30);

        backbtn.setOnClickListener(v->finish());
        stretchingLink5.setOnClickListener(v -> openUrl("https://www.youtube.com/results?search_query=5분+스트레칭"));
        stretchingLink10.setOnClickListener(v -> openUrl("https://www.youtube.com/results?search_query=10분+스트레칭"));
        stretchingLink15.setOnClickListener(v -> openUrl("https://www.youtube.com/results?search_query=15분+스트레칭"));
        stretchingLink30.setOnClickListener(v -> openUrl("https://www.youtube.com/results?search_query=30분+스트레칭"));
    }
    private void openUrl(String url) {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        startActivity(intent);
    }
}
