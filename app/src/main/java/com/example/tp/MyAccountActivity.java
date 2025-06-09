package com.example.tp;

import android.os.Bundle;
import android.content.SharedPreferences;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class MyAccountActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_account);

        SharedPreferences sharedPreferences = getSharedPreferences("auth", MODE_PRIVATE);
        String nickname = sharedPreferences.getString("nickname", "사용자");
        String email = sharedPreferences.getString("email", "email@example.com");

        TextView greetNickname = findViewById(R.id.user_nickname);
        TextView nicknameText = findViewById(R.id.nickname);
        TextView emailText = findViewById(R.id.user_email);
        ImageView backBtn = findViewById(R.id.back_button);

        greetNickname.setText(nickname);
        nicknameText.setText(nickname);
        emailText.setText(email);

        backBtn.setOnClickListener(v->finish());

    }
}
