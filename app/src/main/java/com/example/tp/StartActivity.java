package com.example.tp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class StartActivity extends AppCompatActivity {
    private Button startbtn;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        startbtn = findViewById(R.id.button_get_started);
        startbtn.setOnClickListener(v ->startActivity(new Intent(this, SignUpActivity.class)));
    }
}
