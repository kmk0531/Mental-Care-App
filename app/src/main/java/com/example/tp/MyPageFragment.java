package com.example.tp;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

public class MyPageFragment extends Fragment {

    private String nickname;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_mypage, container, false);

        // Retrieve nickname from SharedPreferences
        android.content.SharedPreferences prefs = requireContext().getSharedPreferences("UserPrefs", android.content.Context.MODE_PRIVATE);
        nickname = prefs.getString("nickname", "사용자");

        TextView tvGreeting = view.findViewById(R.id.tv_greeting);
        tvGreeting.setText("안녕하세요 " + nickname + "님");

        view.findViewById(R.id.item_edit_info).setOnClickListener(v ->
                Toast.makeText(getContext(), "내 정보 변경 클릭", Toast.LENGTH_SHORT).show());

        view.findViewById(R.id.item_emergency_call).setOnClickListener(v ->
                Toast.makeText(getContext(), "긴급전화 클릭", Toast.LENGTH_SHORT).show());

        view.findViewById(R.id.item_self_test).setOnClickListener(v ->openSurvey());
        view.findViewById(R.id.item_logout).setOnClickListener(v ->
                Toast.makeText(getContext(), "로그아웃 클릭", Toast.LENGTH_SHORT).show());

        return view;
    }


    private void openSurvey() {
        Intent intent = new Intent(requireContext(), SurveyActivity.class);
        startActivity(intent);
    }
}