package com.example.tp;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.widget.Toast;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;


import org.json.JSONObject;
public class SignUpActivity extends AppCompatActivity {
    private Button btn_continue;
    private EditText newEmail, newPassword, newUsername;
    private final String SIGNUP_URL = "http://10.0.2.2:3000/register";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        btn_continue = findViewById(R.id.btn_continue);
        newEmail = findViewById(R.id.input_email);
        newPassword = findViewById(R.id.input_password);
        newUsername = findViewById(R.id.input_username);
        btn_continue.setOnClickListener(v -> {
            String email = newEmail.getText().toString().trim();
            String password = newPassword.getText().toString().trim();
            String username = newUsername.getText().toString().trim();

            // JSON 객체 생성
            JSONObject jsonBody = new JSONObject();
            try {
                jsonBody.put("email", email);
                jsonBody.put("password", password);
                jsonBody.put("nickname", username);
            } catch (Exception e) {
                e.printStackTrace();
                return;
            }

            RequestQueue queue = Volley.newRequestQueue(SignUpActivity.this);

            StringRequest request = new StringRequest(
                    Request.Method.POST,
                    SIGNUP_URL,
                    response -> Toast.makeText(SignUpActivity.this, "회원가입 성공", Toast.LENGTH_SHORT).show(),
                    error -> Toast.makeText(SignUpActivity.this, "회원가입 실패: " + error.getMessage(), Toast.LENGTH_SHORT).show()
            ) {
                @Override
                public String getBodyContentType() {
                    return "application/json; charset=utf-8";
                }

                @Override
                public byte[] getBody() {
                    return jsonBody.toString().getBytes();
                }
            };

            queue.add(request);
        });
    }
}
