package com.example.tp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class StartActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        Button startbtn = findViewById(R.id.button_get_started);
        TextView login = findViewById(R.id.text_sign_in);
        startbtn.setOnClickListener(v ->startActivity(new Intent(this, SignUpActivity.class)));
        login.setOnClickListener(v->startActivity(new Intent(this, LoginActivity.class)));
    }
}
