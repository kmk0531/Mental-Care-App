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
    }

    private void showEmotionDialog(String day) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_emotion_entry, null);
        builder.setView(dialogView);
        builder.setTitle("감정 기록 - " + textMonth.getText() + " " + day + "일");

        EditText diaryEdit = dialogView.findViewById(R.id.edit_diary);

        // Set up CheckBoxes and EditTexts for percentage-based multi-emotion selection
        CheckBox[] checkBoxes = {
            dialogView.findViewById(R.id.cb_calm),
            dialogView.findViewById(R.id.cb_happy),
            dialogView.findViewById(R.id.cb_sad),
            dialogView.findViewById(R.id.cb_angry),
            dialogView.findViewById(R.id.cb_anxious),
            dialogView.findViewById(R.id.cb_joy),
            dialogView.findViewById(R.id.cb_proud),
            dialogView.findViewById(R.id.cb_tired),
            dialogView.findViewById(R.id.cb_depressed)
        };

        // Set tag for each emotion checkbox to the corresponding hex color
        checkBoxes[0].setTag("#008000"); // 평온
        checkBoxes[1].setTag("#FFFF00"); // 기쁨
        checkBoxes[2].setTag("#87CEEB"); // 슬픔
        checkBoxes[3].setTag("#FF1493"); // 분노
        checkBoxes[4].setTag("#800080"); // 불안
        checkBoxes[5].setTag("#FFC0CB"); // 행복
        checkBoxes[6].setTag("#FFA500"); // 뿌듯
        checkBoxes[7].setTag("#0000FF"); // 피곤
        checkBoxes[8].setTag("#808080"); // 우울

        EditText[] percentFields = {
            dialogView.findViewById(R.id.et_calm_percentage),
            dialogView.findViewById(R.id.et_happy_percentage),
            dialogView.findViewById(R.id.et_sad_percentage),
            dialogView.findViewById(R.id.et_angry_percentage),
            dialogView.findViewById(R.id.et_anxious_percentage),
            dialogView.findViewById(R.id.et_joy_percentage),
            dialogView.findViewById(R.id.et_proud_percentage),
            dialogView.findViewById(R.id.et_tired_percentage),
            dialogView.findViewById(R.id.et_depressed_percentage)
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
                                String emotion = getEmotionNameByColor(obj.optString("emotion", ""));
                                String diary = obj.optString("diary", "");
                                String createdAt = obj.optString("created_at", "");
                                diaryList.add("[" + emotion + "] " + createdAt + "\n" + diary);
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

                fetchWordsForTopEmotions(year, month, userId, topEmotionColors);
            },
            error -> {
                error.printStackTrace();
                Toast.makeText(context, "감정 통계 불러오기 실패", Toast.LENGTH_SHORT).show();
            });

        queue.add(request);
    }

    // Modified: Show all available words per emotion instead of top 3
    private void fetchWordsForTopEmotions(int year, int month, int userId, List<String> emotions) {
        StringBuilder wordSummary = new StringBuilder();
        com.android.volley.RequestQueue queue = com.android.volley.toolbox.Volley.newRequestQueue(context);
        final int[] completedCount = {0};

        for (int i = 0; i < emotions.size(); i++) {
            String emotionHex = emotions.get(i);
            String encodedEmotion = "";
            try {
                encodedEmotion = URLEncoder.encode(emotionHex, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }

            String url = "http://10.0.2.2:3000/top-words-by-emotion?user_id=" + userId + "&year=" + year + "&month=" + month + "&emotion=" + encodedEmotion;
            final String emotionName = getEmotionNameByColor(emotionHex);

            // Use JsonArrayRequest, as the response is a JSON array
            com.android.volley.toolbox.JsonArrayRequest wordRequest = new com.android.volley.toolbox.JsonArrayRequest(
                com.android.volley.Request.Method.GET, url, null,
                response -> {
                    try {
                        wordSummary.append(emotionName).append(": ");
                        if (response.length() == 0) {
                            wordSummary.append("없음\n");
                        } else {
                            for (int j = 0; j < response.length(); j++) {
                                org.json.JSONObject wordObj = response.getJSONObject(j);
                                // The real word may be inside a nested object, depending on your API
                                // Try to extract actual word string
                                String word = null;
                                if (wordObj.has("word") && wordObj.get("word") instanceof String) {
                                    word = wordObj.getString("word");
                                } else if (wordObj.has("count") && wordObj.get("count") instanceof org.json.JSONObject) {
                                    org.json.JSONObject countObj = wordObj.getJSONObject("count");
                                    if (countObj.has("word")) {
                                        word = countObj.getString("word");
                                    }
                                }
                                if (word != null) {
                                    wordSummary.append(word);
                                    if (j < response.length() - 1) wordSummary.append(", ");
                                }
                            }
                            wordSummary.append("\n");
                        }
                    } catch (org.json.JSONException e) {
                        e.printStackTrace();
                    }

                    completedCount[0]++;
                    if (completedCount[0] == emotions.size()) {
                        new AlertDialog.Builder(context)
                            .setTitle("감정별 주요 키워드")
                            .setMessage(wordSummary.toString())
                            .setPositiveButton("확인", null)
                            .show();
                    }
                },
                error -> {
                    error.printStackTrace();
                    wordSummary.append(emotionName).append(": 오류 발생\n");
                    completedCount[0]++;
                    if (completedCount[0] == emotions.size()) {
                        new AlertDialog.Builder(context)
                            .setTitle("감정별 주요 키워드")
                            .setMessage(wordSummary.toString())
                            .setPositiveButton("확인", null)
                            .show();
                    }
                }
            );
            queue.add(wordRequest);
        }
    }

    private String getEmotionNameByColor(String color) {
        switch (color) {
            case "#FFFF00": return "기쁨";
            case "#FFC0CB": return "행복";
            case "#FFA500": return "뿌듯";
            case "#0000FF": return "피곤";
            case "#808080": return "우울";
            case "#87CEEB": return "슬픔";
            case "#FF1493": return "분노";
            case "#800080": return "불안";
            case "#008000": return "평온";
            default: return "기타";
        }
    }
}
