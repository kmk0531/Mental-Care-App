package com.example.tp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class PagedPSSActivity extends AppCompatActivity{
    private String[] questions = new String[]{
            "1. 예상치 못했던 일 때문에 당황했던 적이 얼마나 있었습니까?",
            "2. 인생에서 중요한 일들을 조절할 수 없다는 느낌을 얼마나 경험하였습니까?",
            "3. 신경이 예민해지고 스트레스를 받고 있다는 느낌을 얼마나 경험하였습니까?",
            "4. 당신의 개인적 문제들을 다루는데 있어서 얼마나 자주 자신감을 느끼셨습니까?",
            "5. 일상의 일들이 당신의 생각대로 진행되고 있다는 느낌을 얼마나 경험하였습니까?",
            "6. 당신이 꼭 해야 하는 일을 처리할 수 없다고 생각한 적이 얼마나 있었습니까?",
            "7. 일상생활의 짜증을 얼마나 자주 잘 다스릴 수 있었습니까?",
            "8. 최상의 컨디션이라고 얼마나 자주 느끼셨습니까?",
            "9. 당신이 통제할 수 없는 일 때문에 화가 난 경험이 얼마나 있었습니까?",
            "10. 어려운 일들이 너무 많이 쌓여서 극복하지 못할 것 같은 느낌을 얼마나 자주 경험하셨습니까?"
    };

    private String[] options = new String[]{"전혀 없었다", "거의 없었다", "때때로 있었다", "자주 있었다", "매우 자주 있었다"};
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
        if (totalScore <= 13) {
            result = "정상" +
                    "심리적으로 안정된 상태로 보입니다.";
        } else if (totalScore <= 16) {
            result = "가벼운 스트레스: 스트레스의 영향을 받기 시작했으나 심각한 수준은 아닙니다. 스트레스를 해소하기 위해 나만의 방법을 찾아보는 것이 좋겠습니다.";
        } else if (totalScore <= 18) {
            result = "중간 정도 스트레스: 지속적인 스트레스는 정신과적인 어려움으로 이뤄질 수 있기에 스트레스를 해소하기 위한 적극적인 노력이 필요합니다. 스스로 필요하다고 생각되면 전문가에게 도움을 요청해 보십시오.";
        } else {
            result = "심한 스트레스: 심한 스트레스를 받고 있으며 일상생활에서 어려움을 겪고 있는 것으로 파악됩니다. 가능한 빨리 전문가의 도움을 받기를 권유 드립니다.";
        }

        Intent intent = new Intent(this, ResultActivity.class);
        intent.putExtra("score", totalScore);
        intent.putExtra("description", result);
        intent.putExtra("testType", "PSS 스트레스 검사");
        startActivity(intent);
        finish();
    }
}
