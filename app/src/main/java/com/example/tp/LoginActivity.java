package com.example.tp;


import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

public class LoginActivity extends AppCompatActivity{

    private EditText editEmail, editPassword;
    private Button btnLogin;
    private final String LOGIN_URL = "http://10.0.2.2:3000/login";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        editEmail = findViewById(R.id.edit_email);
        editPassword = findViewById(R.id.edit_password);
        btnLogin = findViewById(R.id.submit);

        btnLogin.setOnClickListener(v -> {
            String email = editEmail.getText().toString().trim();
            String password = editPassword.getText().toString().trim();

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "모든 입력을 채워주세요", Toast.LENGTH_SHORT).show();
                return;
            }

            loginUser(email, password);
        });


    }
    private void loginUser(String email, String password) {
        JSONObject body = new JSONObject();
        try {
            body.put("email", email);
            body.put("password", password);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, LOGIN_URL, body,
                response -> {
                    try {
                        String token = response.getString("token");
                        int userId = response.getInt("user_id");
                        String nickname = response.getString("nickname");
                        String userEmail = response.getString("email");
                        // JWT 저장
                        SharedPreferences prefs = getSharedPreferences("auth", MODE_PRIVATE);
                        SharedPreferences.Editor editor = prefs.edit();
                        editor.putString("jwt_token", token);
                        editor.putInt("user_id", userId);
                        editor.putString("nickname", nickname);
                        editor.putString("email", userEmail);
                        editor.apply();

                        Toast.makeText(this, "로그인 성공!", Toast.LENGTH_SHORT).show();

                        // MainActivity로 이동
                        Intent intent = new Intent(this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(this, "응답 처리 중 오류", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    Log.e("LOGIN", "에러: " + error.toString());
                    Toast.makeText(this, "로그인 실패", Toast.LENGTH_SHORT).show();
                });

        Volley.newRequestQueue(this).add(request);
    }
}
