package com.example.tp;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class TherapyFragment extends Fragment {
    TextView btnMeditation;
    TextView btnStretching;
    ImageView btnAutoDiagnosis;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_therapy, container, false);
        btnMeditation = view.findViewById(R.id.btn_meditation);

        btnMeditation.setOnClickListener(v -> {
            Intent intent = new Intent(requireContext(), MeditationActivity.class);
            startActivity(intent);
        });



        // 스트레칭
        btnStretching = view.findViewById(R.id.btn_stretching);

        btnStretching.setOnClickListener(v -> {
            Intent intent = new Intent(requireContext(), StretchingActivity.class);
            startActivity(intent);
        });


        // 자가진단
        btnAutoDiagnosis = view.findViewById(R.id.btn_autodiagnosis);
        btnAutoDiagnosis.setOnClickListener(v -> openSurvey());
        return view;
    }

    private void openSurvey() {
        Intent intent = new Intent(requireContext(), SurveyActivity.class);
        startActivity(intent);
    }
}
