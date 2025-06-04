package com.example.tp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;


public class PagedPHQ9Activity extends AppCompatActivity{
    private String[] questions = new String[]{
            "1. 기분이 가라앉거나, 우울하거나, 희망이 없다고 느꼈다.",
            "2. 평소 하던 일에 대한 흥미가 없어지거나 즐거움을 느끼지 못했다.",
            "3. 잠들기가 어렵거나 자꾸 깼다/혹은 너무 많이 잤다.",
            "4. 평소보다 식욕이 줄었다/혹은 평소보다 많이 먹었다.",
            "5. 말과 행동이 느려지거나 너무 안절부절못했다.",
            "6. 피곤하고 기운이 없었다.",
            "7. 실패했다는 생각이 들거나 자신과 가족을 실망시켰다.",
            "8. 집중할 수가 없었다.",
            "9. 죽고 싶거나 자해할 생각이 들었다."
    };

    private String[] options = new String[]{"전혀 아니다", "여러 날 동안", "일주일 이상", "거의 매일"};
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
            result = "우울 아님" +
                    "유의한 수준의 우울이 시사되진 않습니다.";
        } else if (totalScore <= 9) {
            result = "가벼운 우울" +
                    "경미한 수준의 우울감이 있으나 일상생활에 큰 지장은 없습니다.";
        } else if (totalScore <= 19) {
            result = "중간정도의 우울" +
                    "이러한 수준의 우울감은 흔히 신체적, 심리적 대처자원을 저하시키며 개인의 일상생활을 어렵게 만들기도 합니다. 가까운 지역센터나 전문기관을 방문하여 보다 상세한 평가와 도움을 받아보시기 바랍니다.";
        } else {
            result = "심한 우울" +
                    "전문기관의 치료적 개입과 평가가 요구됩니다.";
        }

        Intent intent = new Intent(this, ResultActivity.class);
        intent.putExtra("score", totalScore);
        intent.putExtra("description", result);
        intent.putExtra("testType", "PHQ-9 우울 검사");
        startActivity(intent);
        finish();
    }
}
