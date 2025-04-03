package com.example.tp;


import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class CalendarFragment extends Fragment {

    private GridView calendarGrid;
    private TextView textMonth;
    private Button btnPrev, btnNext;

    private Calendar calendar;
    private final List<String> dayList = new ArrayList<>();
    private CalendarAdapter adapter;

    private Context context;  // context를 따로 저장해서 사용

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_calendar, container, false);
        context = getContext();  // SharedPreferences나 Toast 등에 사용

        calendarGrid = view.findViewById(R.id.calendar_grid);
        textMonth = view.findViewById(R.id.text_month);
        btnPrev = view.findViewById(R.id.btn_prev);
        btnNext = view.findViewById(R.id.btn_next);

        calendar = Calendar.getInstance();
        updateCalendar();

        btnPrev.setOnClickListener(v -> {
            calendar.add(Calendar.MONTH, -1);
            updateCalendar();
        });

        btnNext.setOnClickListener(v -> {
            calendar.add(Calendar.MONTH, 1);
            updateCalendar();
        });

        calendarGrid.setOnItemClickListener((parent, v, position, id) -> {
            String clickedDay = dayList.get(position);
            if (clickedDay.isEmpty()) return;
            showEmotionDialog(clickedDay);
        });

        return view;
    }

    private void updateCalendar() {
        dayList.clear();
        calendar.set(Calendar.DAY_OF_MONTH, 1);

        int startDay = calendar.get(Calendar.DAY_OF_WEEK) - 1;
        int maxDay = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);

        for (int i = 0; i < startDay; i++) dayList.add("");
        for (int i = 1; i <= maxDay; i++) dayList.add(String.valueOf(i));

        adapter = new CalendarAdapter(context, dayList, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH));
        calendarGrid.setAdapter(adapter);

        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1;
        textMonth.setText(year + "년 " + month + "월");
    }

    private void showEmotionDialog(String day) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_emotion_entry, null);
        builder.setView(dialogView);
        builder.setTitle("감정 기록 - " + textMonth.getText() + " " + day + "일");

        RadioGroup emotionGroup = dialogView.findViewById(R.id.emotion_group);
        EditText diaryEdit = dialogView.findViewById(R.id.edit_diary);

        String fullDate = calendar.get(Calendar.YEAR) + "-" + (calendar.get(Calendar.MONTH) + 1) + "-" + day;

        SharedPreferences prefs = context.getSharedPreferences("EmotionData", Context.MODE_PRIVATE);
        String savedColor = prefs.getString(fullDate + "_color", null);
        String savedDiary = prefs.getString(fullDate + "_diary", "");

        diaryEdit.setText(savedDiary);

        if (savedColor != null) {
            if (savedColor.equals("#6B9973")) emotionGroup.check(R.id.rb_calm);
            else if (savedColor.equals("#D4B83D")) emotionGroup.check(R.id.rb_happy);
            else if (savedColor.equals("#828ABF")) emotionGroup.check(R.id.rb_sad);
            else if (savedColor.equals("#D68056")) emotionGroup.check(R.id.rb_angry);
            else if (savedColor.equals("#B26BB2")) emotionGroup.check(R.id.rb_anxious);
        }

        builder.setPositiveButton("저장", (dialog, which) -> {
            int selectedId = emotionGroup.getCheckedRadioButtonId();
            String selectedEmotion = "";

            if (selectedId == R.id.rb_calm) selectedEmotion = "#6B9973";
            else if (selectedId == R.id.rb_happy) selectedEmotion = "#D4B83D";
            else if (selectedId == R.id.rb_sad) selectedEmotion = "#828ABF";
            else if (selectedId == R.id.rb_angry) selectedEmotion = "#D68056";
            else if (selectedId == R.id.rb_anxious) selectedEmotion = "#B26BB2";

            String diaryText = diaryEdit.getText().toString();
            saveEmotionData(fullDate, selectedEmotion, diaryText);
            updateCalendar();
            Toast.makeText(context, "감정이 저장되었습니다!", Toast.LENGTH_SHORT).show();
        });

        builder.setNeutralButton("초기화", (dialog, which) -> {
            SharedPreferences.Editor editor = prefs.edit();
            editor.remove(fullDate + "_color");
            editor.remove(fullDate + "_diary");
            editor.apply();
            updateCalendar();
            Toast.makeText(context, "감정이 초기화되었습니다.", Toast.LENGTH_SHORT).show();
        });

        builder.setNegativeButton("취소", null);
        builder.show();
    }

    private void saveEmotionData(String date, String color, String diary) {
        SharedPreferences prefs = context.getSharedPreferences("EmotionData", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(date + "_color", color);
        editor.putString(date + "_diary", diary);
        editor.apply();
    }
}
