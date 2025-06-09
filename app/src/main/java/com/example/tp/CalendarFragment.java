package com.example.tp;
import java.net.URLEncoder;
import java.io.UnsupportedEncodingException;


import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
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

        // Load emotion color map from server asynchronously
        SharedPreferences authPrefs = context.getSharedPreferences("auth", Context.MODE_PRIVATE);
        int userId = authPrefs.getInt("user_id", -1);
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        if (userId == -1) {
            Toast.makeText(context, "로그인 정보가 없습니다", Toast.LENGTH_SHORT).show();
            // Still update month text/bar chart
            textMonth.setText(year + "년 " + (month + 1) + "월");
            updateEmotionBarChart(year, month + 1);
            return;
        }

        String url = "http://10.0.2.2:3000/monthly-emotion-colors?user_id=" + userId + "&year=" + year + "&month=" + (month + 1);
        com.android.volley.RequestQueue queue = com.android.volley.toolbox.Volley.newRequestQueue(context);
        com.android.volley.toolbox.JsonArrayRequest request = new com.android.volley.toolbox.JsonArrayRequest(
                com.android.volley.Request.Method.GET, url, null,
                response -> {
                    Map<String, String> emotionColorMap = new HashMap<>();
                    for (int i = 0; i < response.length(); i++) {
                        try {
                            org.json.JSONObject obj = response.getJSONObject(i);
                            String diaryDate = obj.optString("diary_date", "");
                            String emotion = obj.optString("emotion", "");
                            if (!diaryDate.isEmpty() && !emotion.isEmpty()) {
                                emotionColorMap.put(diaryDate, emotion);
                            }
                        } catch (org.json.JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    adapter = new CalendarAdapter(context, dayList, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), emotionColorMap);
                    calendarGrid.setAdapter(adapter);
                },
                error -> {
                    error.printStackTrace();
                    // fallback: show empty color map
                    adapter = new CalendarAdapter(context, dayList, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), new HashMap<>());
                    calendarGrid.setAdapter(adapter);
                }
        );
        queue.add(request);

        textMonth.setText(year + "년 " + (month + 1) + "월");
        updateEmotionBarChart(year, month + 1);
        fetchTopWordsForEmotions(year, month + 1);
    }

    private void showEmotionDialog(String day) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_emotion_entry, null);
        builder.setView(dialogView);
        builder.setTitle("감정 기록 - " + textMonth.getText() + " " + day + "일");

        EditText diaryEdit = dialogView.findViewById(R.id.edit_diary);

        // Set up CheckBoxes and EditTexts for percentage-based multi-emotion selection
        CheckBox[] checkBoxes = {
            dialogView.findViewById(R.id.cb_joy),      // 기쁨
            dialogView.findViewById(R.id.cb_happy),    // 행복
            dialogView.findViewById(R.id.cb_proud),    // 뿌듯
            dialogView.findViewById(R.id.cb_tired),    // 피곤
            dialogView.findViewById(R.id.cb_depressed),// 우울
            dialogView.findViewById(R.id.cb_sad),      // 슬픔
            dialogView.findViewById(R.id.cb_angry),    // 분노
            dialogView.findViewById(R.id.cb_anxious),  // 불안
            dialogView.findViewById(R.id.cb_calm)      // 평온
        };

        // Set tag for each emotion checkbox to the corresponding hex color
        checkBoxes[0].setTag("#F4CE6F"); // 기쁨
        checkBoxes[1].setTag("#F3AAA8"); // 행복
        checkBoxes[2].setTag("#D68056"); // 뿌듯
        checkBoxes[3].setTag("#828BBF"); // 피곤
        checkBoxes[4].setTag("#C2C3C5"); // 우울
        checkBoxes[5].setTag("#8DB6EA"); // 슬픔
        checkBoxes[6].setTag("#D5595B"); // 분노
        checkBoxes[7].setTag("#976BB2"); // 불안
        checkBoxes[8].setTag("#6B9973"); // 평온

        EditText[] percentFields = {
            dialogView.findViewById(R.id.et_joy_percentage),       // 기쁨
            dialogView.findViewById(R.id.et_happy_percentage),     // 행복
            dialogView.findViewById(R.id.et_proud_percentage),     // 뿌듯
            dialogView.findViewById(R.id.et_tired_percentage),     // 피곤
            dialogView.findViewById(R.id.et_depressed_percentage), // 우울
            dialogView.findViewById(R.id.et_sad_percentage),       // 슬픔
            dialogView.findViewById(R.id.et_angry_percentage),     // 분노
            dialogView.findViewById(R.id.et_anxious_percentage),   // 불안
            dialogView.findViewById(R.id.et_calm_percentage)       // 평온
        };
        // Enable/disable percent fields based on checkbox state
        for (int i = 0; i < checkBoxes.length; i++) {
            final int index = i;
            percentFields[i].setEnabled(checkBoxes[i].isChecked());
            checkBoxes[i].setOnCheckedChangeListener((buttonView, isChecked) -> {
                percentFields[index].setEnabled(isChecked);
                if (!isChecked) percentFields[index].setText("");
            });
        }

        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        String fullDate = String.format("%04d-%02d-%02d", year, month + 1, Integer.parseInt(day));

        SharedPreferences prefs = context.getSharedPreferences("auth", Context.MODE_PRIVATE);
        int userId = prefs.getInt("user_id", -1);
        if (userId == -1) {
            Toast.makeText(context, "로그인 정보가 없습니다", Toast.LENGTH_SHORT).show();
            return;
        }

        String url = "http://10.0.2.2:3000/get-emotion-entries?user_id=" + userId + "&diary_date=" + fullDate;

        com.android.volley.RequestQueue queue = com.android.volley.toolbox.Volley.newRequestQueue(context);
        com.android.volley.toolbox.JsonObjectRequest request = new com.android.volley.toolbox.JsonObjectRequest(
                com.android.volley.Request.Method.GET, url, null,
                response -> {
                    try {
                        org.json.JSONArray entries = response.optJSONArray("entries");
                        if (entries != null && entries.length() > 0) {
                            LinearLayout layoutWrite = dialogView.findViewById(R.id.layout_write_entry);
                            LinearLayout layoutView = dialogView.findViewById(R.id.layout_view_entries);
                            ListView listPrevious = dialogView.findViewById(R.id.list_previous_entries);

                            List<String> diaryList = new ArrayList<>();
                            for (int i = 0; i < entries.length(); i++) {
                                org.json.JSONObject obj = entries.getJSONObject(i);
                                String emotion = getEmotionNameByColor(obj.optString("dominant_emotion", ""));
                                String diary = obj.optString("diary", "");
                                String createdAt = obj.optString("created_at", "");
                                // Add emotion percentages
                                org.json.JSONArray emotionsArray = obj.optJSONArray("emotions");
                                StringBuilder emotionDetailBuilder = new StringBuilder();
                                if (emotionsArray != null) {
                                    for (int j = 0; j < emotionsArray.length(); j++) {
                                        org.json.JSONObject emo = emotionsArray.getJSONObject(j);
                                        String color = emo.optString("emotion", "");
                                        int percent = emo.optInt("percent", 0);
                                        emotionDetailBuilder.append(getEmotionNameByColor(color)).append(": ").append(percent).append("%");
                                        if (j < emotionsArray.length() - 1) {
                                            emotionDetailBuilder.append(", ");
                                        }
                                    }
                                }
                                diaryList.add("[" + emotion + "] " + createdAt + "\n" + diary + "\n" + emotionDetailBuilder.toString());
                            }

                            ArrayAdapter<String> adapter = new ArrayAdapter<>(context, android.R.layout.simple_list_item_1, diaryList);
                            listPrevious.setAdapter(adapter);

                            layoutView.setVisibility(View.VISIBLE);
                            layoutWrite.setVisibility(View.GONE);

                            // Add button logic to toggle to writing view
                            Button btnAddEntry = dialogView.findViewById(R.id.btn_add_entry);
                            btnAddEntry.setOnClickListener(v -> {
                                layoutWrite.setVisibility(View.VISIBLE);
                                layoutView.setVisibility(View.GONE);
                            });
                        }
                    } catch (org.json.JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(context, "불러오기 실패", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    error.printStackTrace();
                    Toast.makeText(context, "불러오기 실패", Toast.LENGTH_SHORT).show();
                });

        queue.add(request);

        builder.setPositiveButton("저장", (dialog, which) -> {
            // Multi-emotion with percent logic
            org.json.JSONArray emotionsArray = new org.json.JSONArray();
            int maxPercent = -1;
            String dominantEmotion = "";
            for (int i = 0; i < checkBoxes.length; i++) {
                if (checkBoxes[i].isChecked()) {
                    String percentText = percentFields[i].getText().toString().trim();
                    if (percentText.isEmpty()) continue;
                    int percent = Integer.parseInt(percentText);
                    if (percent > maxPercent) {
                        maxPercent = percent;
                        dominantEmotion = checkBoxes[i].getTag().toString(); // assumes tag is hex color
                    }
                    try {
                        org.json.JSONObject obj = new org.json.JSONObject();
                        obj.put("emotion", checkBoxes[i].getTag().toString());
                        obj.put("percent", percent);
                        emotionsArray.put(obj);
                    } catch (org.json.JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
            if (emotionsArray.length() == 0) {
                Toast.makeText(context, "감정을 하나 이상 선택하고 퍼센티지를 입력하세요.", Toast.LENGTH_SHORT).show();
                return;
            }
            String diaryText = diaryEdit.getText().toString();
            saveEmotionDataWithPercents(fullDate, dominantEmotion, diaryText, emotionsArray);
            Toast.makeText(context, "감정이 저장되었습니다!", Toast.LENGTH_SHORT).show();
        });

        builder.setNeutralButton("초기화", (dialog, which) -> {
            SharedPreferences emotionPrefs = context.getSharedPreferences("EmotionData", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = emotionPrefs.edit();
            editor.remove(fullDate + "_color");
            editor.remove(fullDate + "_diary");
            editor.apply();
            updateCalendar();
            Toast.makeText(context, "감정이 초기화되었습니다.", Toast.LENGTH_SHORT).show();
        });

        builder.setNegativeButton("취소", null);
        builder.show();
    }

    // New method for saving multi-emotion (percent) data
    private void saveEmotionDataWithPercents(String date, String dominantColor, String diary, org.json.JSONArray emotionsArray) {
        SharedPreferences userPrefs = context.getSharedPreferences("auth", Context.MODE_PRIVATE);
        int userId = userPrefs.getInt("user_id", -1);
        if (userId == -1) {
            Toast.makeText(context, "로그인 정보가 없습니다", Toast.LENGTH_SHORT).show();
            return;
        }
        String url = "http://10.0.2.2:3000/save-emotion-entry";
        com.android.volley.RequestQueue queue = com.android.volley.toolbox.Volley.newRequestQueue(context);
        org.json.JSONObject jsonBody = new org.json.JSONObject();
        try {
            jsonBody.put("user_id", userId);
            jsonBody.put("dominant_emotion", dominantColor);
            jsonBody.put("diary", diary);
            jsonBody.put("diary_date", date);
            jsonBody.put("emotions", emotionsArray);
        } catch (org.json.JSONException e) {
            e.printStackTrace();
            Toast.makeText(context, "데이터 생성 오류", Toast.LENGTH_SHORT).show();
            return;
        }
        com.android.volley.toolbox.JsonObjectRequest request = new com.android.volley.toolbox.JsonObjectRequest(
                com.android.volley.Request.Method.POST, url, jsonBody,
                response -> {
                    SharedPreferences prefs = context.getSharedPreferences("EmotionData", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = prefs.edit();
                    editor.putString(date + "_color", dominantColor);
                    editor.putString(date + "_diary", diary);
                    editor.apply();
                    updateCalendar();
                    Toast.makeText(context, "감정이 저장되었습니다!", Toast.LENGTH_SHORT).show();
                },
                error -> {
                    error.printStackTrace();
                    Toast.makeText(context, "저장 실패", Toast.LENGTH_SHORT).show();
                }
        );
        queue.add(request);
    }

    private void saveEmotionData(String date, String color, String diary) {
        // Load user_id from SharedPreferences named "UserInfo"
        SharedPreferences userPrefs = context.getSharedPreferences("auth", Context.MODE_PRIVATE);
        int userId = userPrefs.getInt("user_id", -1);
        if (userId == -1) {
            Toast.makeText(context, "로그인 정보가 없습니다", Toast.LENGTH_SHORT).show();
            return;
        }

        String url = "http://10.0.2.2:3000/save-emotion-entry";
        com.android.volley.RequestQueue queue = com.android.volley.toolbox.Volley.newRequestQueue(context);

        java.util.Map<String, String> params = new java.util.HashMap<>();
        params.put("user_id", String.valueOf(userId));
        params.put("emotion", color);
        params.put("diary", diary);
        params.put("diary_date", date);

        org.json.JSONObject jsonBody = new org.json.JSONObject(params);

        com.android.volley.toolbox.JsonObjectRequest request = new com.android.volley.toolbox.JsonObjectRequest(
                com.android.volley.Request.Method.POST, url, jsonBody,
                response -> {
                    SharedPreferences prefs = context.getSharedPreferences("EmotionData", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = prefs.edit();
                    editor.putString(date + "_color", color);
                    editor.putString(date + "_diary", diary);
                    editor.apply();
                    updateCalendar();
                    Toast.makeText(context, "감정이 저장되었습니다!", Toast.LENGTH_SHORT).show();
                },
                error -> {
                    error.printStackTrace();
                    Toast.makeText(context, "저장 실패", Toast.LENGTH_SHORT).show();
                }
        );

        queue.add(request);
    }

    private void updateEmotionBarChart(int year, int month) {
        SharedPreferences userPrefs = context.getSharedPreferences("auth", Context.MODE_PRIVATE);
        int userId = userPrefs.getInt("user_id", -1);
        if (userId == -1) {
            Toast.makeText(context, "로그인 정보가 없습니다", Toast.LENGTH_SHORT).show();
            return;
        }

        String url = "http://10.0.2.2:3000/monthly-top-emotions?user_id=" + userId + "&year=" + year + "&month=" + month;

        com.android.volley.RequestQueue queue = com.android.volley.toolbox.Volley.newRequestQueue(context);
        com.android.volley.toolbox.JsonArrayRequest request = new com.android.volley.toolbox.JsonArrayRequest(
            com.android.volley.Request.Method.GET, url, null,
            response -> {
                List<BarEntry> entries = new ArrayList<>();
                List<String> labels = new ArrayList<>();
                List<Integer> barColors = new ArrayList<>();
                int totalCount = 0;
                List<String> topEmotionColors = new ArrayList<>();

                for (int i = 0; i < response.length(); i++) {
                    try {
                        org.json.JSONObject obj = response.getJSONObject(i);
                        String emotion = obj.getString("emotion");
                        int count = obj.getInt("count");
                        totalCount += count;

                        entries.add(new BarEntry(i, count));
                        labels.add(getEmotionNameByColor(emotion));
                        barColors.add(Color.parseColor(emotion));
                        topEmotionColors.add(emotion);
                    } catch (org.json.JSONException e) {
                        e.printStackTrace();
                    }
                }

                if (totalCount == 0) {
                    barChart.clear();
                    barChart.invalidate();
                    return;
                }

                for (BarEntry entry : entries) {
                    entry.setY((entry.getY() / totalCount) * 100f);
                }

                BarDataSet dataSet = new BarDataSet(entries, "감정 Top 3 (%)");
                dataSet.setColors(barColors);
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
            },
            error -> {
                error.printStackTrace();
                Toast.makeText(context, "감정 통계 불러오기 실패", Toast.LENGTH_SHORT).show();
            });

        queue.add(request);
    }

    private void fetchTopWordsForEmotions(int year, int month) {
        SharedPreferences userPrefs = context.getSharedPreferences("auth", Context.MODE_PRIVATE);
        int userId = userPrefs.getInt("user_id", -1);
        if (userId == -1) {
            Toast.makeText(context, "로그인 정보가 없습니다", Toast.LENGTH_SHORT).show();
            return;
        }

        String url = "http://10.0.2.2:3000/monthly-top-emotions-with-words?user_id=" + userId +
                "&year=" + year + "&month=" + month;

        com.android.volley.RequestQueue queue = com.android.volley.toolbox.Volley.newRequestQueue(context);
        com.android.volley.toolbox.JsonArrayRequest request = new com.android.volley.toolbox.JsonArrayRequest(
                com.android.volley.Request.Method.GET, url, null,
                response -> {
                    Log.d("TopWordsResponse", "서버 응답: " + response.toString());
                    TextView textTopWords = getView().findViewById(R.id.tv_emotion_words_summary);
                    if (textTopWords != null) {
                        // Sort response array by descending count
                        List<org.json.JSONObject> sortedList = new ArrayList<>();
                        for (int i = 0; i < response.length(); i++) {
                            try {
                                sortedList.add(response.getJSONObject(i));
                            } catch (org.json.JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        sortedList.sort((o1, o2) -> {
                            try {
                                return Integer.compare(o2.getInt("count"), o1.getInt("count"));
                            } catch (org.json.JSONException e) {
                                e.printStackTrace();
                                return 0; // fallback for sorting when error occurs
                            }
                        });

                        if (sortedList.isEmpty()) {
                            textTopWords.setText("저장된 일기가 없습니다.");
                            return;
                        }

                        StringBuilder sb = new StringBuilder();
                        for (org.json.JSONObject item : sortedList) {
                            try {
                                String label = getEmotionNameByColor(item.optString("emotion", "").trim().toUpperCase());
                                // String color = item.optString("emotion", "");
                                int count = item.optInt("count", 0);
                                // sb.append(String.format("[%s %s] (%d회)\n", label, color, count));
                                sb.append(String.format("[%s] (%d회)\n", label, count));
                                org.json.JSONArray wordsArr = item.optJSONArray("words");
                                if (wordsArr != null && wordsArr.length() > 0) {
                                    sb.append("  단어: ");
                                    for (int j = 0; j < wordsArr.length(); j++) {
                                        sb.append(wordsArr.getString(j));
                                        if (j < wordsArr.length() - 1) sb.append(", ");
                                    }
                                    sb.append("\n");
                                }
                            } catch (org.json.JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        textTopWords.setText(sb.toString());
                    }
                },
                error -> {
                    error.printStackTrace();
                    Log.e("TopWordsError", "에러 발생: " + error.toString());
                }
        );
        queue.add(request);
    }

    private String getEmotionNameByColor(String color) {
        switch (color.toUpperCase()) {
            case "#F4CE6F": return "기쁨";
            case "#F3AAA8": return "행복";
            case "#D68056": return "뿌듯";
            case "#828BBF": return "피곤";
            case "#C2C3C5": return "우울";
            case "#8DB6EA": return "슬픔";
            case "#D5595B": return "분노";
            case "#976BB2": return "불안";
            case "#6B9973": return "평온";
            default: return "기타";
        }
    }
}
