package com.example.tp;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.Calendar;
import java.util.List;

public class CalendarAdapter extends BaseAdapter {
    private Context context;
    private List<String> dayList;
    private LayoutInflater inflater;
    private int year;
    private int month;
    private java.util.Map<String, String> emotionColorMap;

    public CalendarAdapter(Context context, List<String> dayList, int year, int month, java.util.Map<String, String> emotionColorMap) {
        this.context = context;
        this.dayList = dayList;
        this.year = year;
        this.month = month;
        this.emotionColorMap = emotionColorMap;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return dayList.size();
    }

    @Override
    public Object getItem(int position) {
        return dayList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View gridItem = inflater.inflate(R.layout.calendar_day, parent, false);
        TextView dayText = gridItem.findViewById(R.id.day_text);
        String day = dayList.get(position);
        dayText.setText(day);

        if (!day.isEmpty()) {
            String fullDate = String.format("%04d-%02d-%02d", year, month + 1, Integer.parseInt(day));
            String colorHex = emotionColorMap.get(fullDate);
            if (colorHex != null) {
                gridItem.setBackgroundColor(Color.parseColor(colorHex));
            }
        }

        return gridItem;
    }
}
