package com.example.tp;

import android.os.Bundle;
import android.content.Intent;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class PagedGAD7Activity extends AppCompatActivity{
    private String[] questions = new String[]{
            "1. 초조하거나 불안하거나 조마조마하게 느낀다.",
            "2. 걱정하는 것을 멈추거나 조절할 수가 없다.",
            "3. 여러 가지 것들에 대해 걱정을 너무 많이한다.",
            "4. 편하게 있기가 어렵다.",
            "5. 너무 안절부절 못해서 가만히 있기 힘들다.",
            "6. 쉽게 짜증이 나거나 쉽게 성을 내게 된다.",
            "7. 끔찍한 일이 생길 것처럼 두렵게 느껴진다."
    };

    private String[] options = new String[]{"전혀 아니다", "몇일 동안", "2주 중 절반 이상", "거의 매일", "매우 자주"};
    private int currentIndex = 0;
    private int totalScore = 0;

    private TextView questionText;
    private LinearLayout optionsContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_paged);

        questionText = findViewById(R.id.question_text);
        optionsContainer = findViewById(R.id.options_container);

        showQuestion();
    }

    private void showQuestion() {
        questionText.setText(questions[currentIndex]);
        optionsContainer.removeAllViews();

        for (int i = 0; i < options.length; i++) {
            final int score = i;
            Button btn = new Button(this);
            btn.setText(options[i]);
            btn.setBackgroundResource(R.drawable.bg_option_button);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, 0, 1);
            params.setMargins(0, 12, 0, 12);
            btn.setLayoutParams(params);
            btn.setTextColor(getColor(android.R.color.black));

            btn.setOnClickListener(v -> {
                totalScore += score;
                currentIndex++;
                if (currentIndex < questions.length) {
                    showQuestion();
                } else {
                    showResult();
                }
            });

            optionsContainer.addView(btn);
        }
    }

    private void showResult() {
        String result;
        if (totalScore <= 4) {
            result = "정상" +
                    "주의가 필요할 정도의 과도한 걱정이나 불안을 보고하지 않았습니다.";
        } else if (totalScore <= 9) {
            result = "경미한 수준" +
                    "다소 경미한 수준의 걱정과 불안을 보고하였습니다. 주의 깊은 관찰과 관심이 필요합니다.";
        } else if (totalScore <= 14) {
            result = "중간 수준" +
                    "주의가 필요한 수준의 과도한 걱정과 불안을 보고하였습니다. 전문가의 도움을 받아보시길 권해 드립니다.";
        } else {
            result = "심한 불안" +
                    "일상생활에 지장을 초래할 정도의 과도하고 심한 걱정과 불안을 보고하였습니다. 추가적인 평가나 정신건강 전문가의 도움을 받아 보시기를 권해드립니다.";
        }

        Intent intent = new Intent(this, ResultActivity.class);
        intent.putExtra("score", totalScore);
        intent.putExtra("description", result);
        intent.putExtra("testType", "GAD-7 불안 검사");
        startActivity(intent);
        finish();
    }
}
