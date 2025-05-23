package com.example.tp;


import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.ValueFormatter;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CalendarFragment extends Fragment {

    private GridView calendarGrid;
    private TextView textMonth;
    private Button btnPrev, btnNext;
    private BarChart barChart;

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
        barChart = view.findViewById(R.id.chart);

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
        updateEmotionBarChart(year, month);
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

    private void updateEmotionBarChart(int year, int month) {
        SharedPreferences prefs = context.getSharedPreferences("EmotionData", Context.MODE_PRIVATE);

        Map<String, Integer> emotionCount = new HashMap<>();
        emotionCount.put("#6B9973", 0);  // 평온
        emotionCount.put("#D4B83D", 0);  // 기쁨
        emotionCount.put("#828ABF", 0);  // 슬픔
        emotionCount.put("#D68056", 0);  // 분노
        emotionCount.put("#B26BB2", 0);  // 불안

        Map<String, ?> allEntries = prefs.getAll();
        String monthPrefix = year + "-" + month + "-";

        int totalCount = 0;

        for (String key : allEntries.keySet()) {
            if (key.endsWith("_color") && key.startsWith(monthPrefix)) {
                String color = (String) allEntries.get(key);
                if (emotionCount.containsKey(color)) {
                    emotionCount.put(color, emotionCount.get(color) + 1);
                    totalCount++;
                }
            }
        }

        if (totalCount == 0) {
            barChart.clear();
            barChart.invalidate();
            return;
        }

        List<Map.Entry<String, Integer>> sortedList = new ArrayList<>(emotionCount.entrySet());
        sortedList.sort((e1, e2) -> e2.getValue().compareTo(e1.getValue()));

        List<BarEntry> entries = new ArrayList<>();
        List<String> labels = new ArrayList<>();
        int maxItems = Math.min(3, sortedList.size());

        for (int i = 0; i < maxItems; i++) {
            Map.Entry<String, Integer> entry = sortedList.get(i);
            if (entry.getValue() == 0) break;

            float percentage = (entry.getValue() * 100f) / totalCount;

            entries.add(new BarEntry(i, percentage));
            labels.add(getEmotionNameByColor(entry.getKey()));
        }

        BarDataSet dataSet = new BarDataSet(entries, "감정 Top 3 (%)");
        dataSet.setColors(Color.parseColor("#6B9973"), Color.parseColor("#D4B83D"),
                Color.parseColor("#828ABF"), Color.parseColor("#D68056"),
                Color.parseColor("#B26BB2"));
        dataSet.setValueTextSize(14f);

        BarData data = new BarData(dataSet);
        data.setBarWidth(0.5f);

        barChart.setDrawGridBackground(false);
        barChart.setBackgroundColor(Color.TRANSPARENT);

        barChart.setData(data);

        XAxis xAxis = barChart.getXAxis();
        xAxis.setGranularity(1f);
        xAxis.setGranularityEnabled(true);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);

        xAxis.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                int index = (int) value;
                return (index >= 0 && index < labels.size()) ? labels.get(index) : "";
            }
        });

        YAxis leftAxis = barChart.getAxisLeft();
        leftAxis.setDrawGridLines(false);
        leftAxis.setAxisMinimum(0f);
        leftAxis.setAxisMaximum(100f);
        barChart.getAxisRight().setEnabled(false);

        Description desc = new Description();
        desc.setText("");
        barChart.setDescription(desc);

        barChart.invalidate();
    }

    private String getEmotionNameByColor(String color) {
        switch (color) {
            case "#6B9973": return "평온";
            case "#D4B83D": return "기쁨";
            case "#828ABF": return "슬픔";
            case "#D68056": return "분노";
            case "#B26BB2": return "불안";
            default: return "기타";
        }
    }
}
