package com.example.tp;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

public class TherapyFragment extends Fragment {
    TextView btnMeditation;
    View layoutMeditationLinks;
    TextView youtubeLink5, youtubeLink10, youtubeLink15, youtubeLink30;

    TextView btnStretching;
    View layoutStretchingLinks;
    TextView stretchingLink5, stretchingLink10, stretchingLink15, stretchingLink30;

    TextView btnAutoDiagnosis;
    View layoutAutoDiagnosisLinks;
    TextView testLink1, testLink2, testLink3;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_therapy, container, false);
        btnMeditation = view.findViewById(R.id.btn_meditation);
        layoutMeditationLinks = view.findViewById(R.id.layout_meditation_links);
        youtubeLink5 = view.findViewById(R.id.youtube_link_5);
        youtubeLink10 = view.findViewById(R.id.youtube_link_10);
        youtubeLink15 = view.findViewById(R.id.youtube_link_15);
        youtubeLink30 = view.findViewById(R.id.youtube_link_30);

        btnMeditation.setOnClickListener(v -> {
            layoutMeditationLinks.setVisibility(
                    layoutMeditationLinks.getVisibility() == View.GONE ? View.VISIBLE : View.GONE);
        });

        youtubeLink5.setOnClickListener(v -> openUrl("https://www.youtube.com/results?search_query=5분+명상"));
        youtubeLink10.setOnClickListener(v -> openUrl("https://www.youtube.com/results?search_query=10분+명상"));
        youtubeLink15.setOnClickListener(v -> openUrl("https://www.youtube.com/results?search_query=15분+명상"));
        youtubeLink30.setOnClickListener(v -> openUrl("https://www.youtube.com/results?search_query=30분+명상"));

        // 스트레칭
        btnStretching = view.findViewById(R.id.btn_stretching);
        layoutStretchingLinks = view.findViewById(R.id.layout_stretching_links);
        stretchingLink5 = view.findViewById(R.id.stretching_link_5);
        stretchingLink10 = view.findViewById(R.id.stretching_link_10);
        stretchingLink15 = view.findViewById(R.id.stretching_link_15);
        stretchingLink30 = view.findViewById(R.id.stretching_link_30);

        btnStretching.setOnClickListener(v -> {
            layoutStretchingLinks.setVisibility(
                    layoutStretchingLinks.getVisibility() == View.GONE ? View.VISIBLE : View.GONE);
        });

        stretchingLink5.setOnClickListener(v -> openUrl("https://www.youtube.com/results?search_query=5분+스트레칭"));
        stretchingLink10.setOnClickListener(v -> openUrl("https://www.youtube.com/results?search_query=10분+스트레칭"));
        stretchingLink15.setOnClickListener(v -> openUrl("https://www.youtube.com/results?search_query=15분+스트레칭"));
        stretchingLink30.setOnClickListener(v -> openUrl("https://www.youtube.com/results?search_query=30분+스트레칭"));

        // 자가진단
        btnAutoDiagnosis = view.findViewById(R.id.btn_autodiagnosis);
        layoutAutoDiagnosisLinks = view.findViewById(R.id.layout_autodiagnosis_links);
        testLink1 = view.findViewById(R.id.test_link_1);
        testLink2 = view.findViewById(R.id.test_link_2);
        testLink3 = view.findViewById(R.id.test_link_3);

        btnAutoDiagnosis.setOnClickListener(v -> openSurvey());


        return view;
    }
    private void openUrl(String url) {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        startActivity(intent);
    }
    private void openSurvey() {
        Intent intent = new Intent(requireContext(), SurveyActivity.class);
        startActivity(intent);
    }
}

