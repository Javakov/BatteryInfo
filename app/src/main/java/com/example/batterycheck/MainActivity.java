package com.example.batterycheck;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private Button statusButton;
    private Button docButton;

    private LinearLayout statusLayout1;
    private LinearLayout statusLayout2;
    private boolean isExpanded = false;

    private boolean isButtonClickable = true;

    private static final long ANIMATION_DURATION = 1500; // Продолжительность анимации в 5 секунд

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        statusButton = findViewById(R.id.statusButton);
        statusLayout1 = findViewById(R.id.statusLayout1);
        statusLayout2 = findViewById(R.id.statusLayout2);
        docButton = findViewById(R.id.docButton);

        // Проверяем значение флага isExpanded в Intent
        Intent intent = getIntent();
        if (intent != null && intent.getBooleanExtra("isExpanded", false)) {
            // Если флаг установлен, сбрасываем состояние LinearLayout без запуска анимации
            resetLayout();
        }

        statusButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isButtonClickable) {
                    return;
                }
                isButtonClickable = false;

                if (isExpanded) {
                    collapseLayout1();
                } else {
                    expandLayout1();
                    resetLayout();
                }

                new android.os.CountDownTimer(ANIMATION_DURATION, ANIMATION_DURATION) {
                    @Override
                    public void onTick(long millisUntilFinished) {
                        // Ничего не делаем на протяжении задержки
                    }

                    @Override
                    public void onFinish() {
                        Intent intent = new Intent(MainActivity.this, StatusActivity.class);
                        startActivity(intent);
                    }
                }.start();
            }
        });

        docButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isButtonClickable) {
                    return;
                }
                isButtonClickable = false;

                if (isExpanded) {
                    collapseLayout2();
                } else {
                    expandLayout2();
                    resetLayout();
                }

                new android.os.CountDownTimer(ANIMATION_DURATION, ANIMATION_DURATION) {
                    @Override
                    public void onTick(long millisUntilFinished) {
                        // Ничего не делаем на протяжении задержки
                    }

                    @Override
                    public void onFinish() {
                        Intent intent = new Intent(MainActivity.this, DocActivity.class);
                        startActivity(intent);
                    }
                }.start();
            }
        });
    }

    private void expandLayout1() {
        animateLayoutHeight(statusLayout1, 220, 1500);
        isExpanded = true;
    }

    private void collapseLayout1() {
        animateLayoutHeight(statusLayout1, 1500, 220);
        isExpanded = false;
    }

    private void expandLayout2() {
        animateLayoutHeight(statusLayout2, 220, 1500);
        isExpanded = true;
    }

    private void collapseLayout2() {
        animateLayoutHeight(statusLayout2, 1500, 220);
        isExpanded = false;
    }
    private void animateLayoutHeight(final View view, int startHeight, int endHeight) {
        ValueAnimator animator = ValueAnimator.ofInt(startHeight, endHeight);
        animator.setDuration(ANIMATION_DURATION);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                int value = (int) valueAnimator.getAnimatedValue();
                view.getLayoutParams().height = value;
                view.requestLayout();
            }
        });
        animator.start();

        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                if (isExpanded) {
                    Intent intent = new Intent(MainActivity.this, StatusActivity.class);
                    startActivity(intent);
                }
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        resetLayout();
        isButtonClickable = true;
    }

    @Override
    protected void onRestart() {
        resetLayout();
        super.onRestart();
        isButtonClickable = true;
    }

    private void resetLayout() {
        statusLayout1.getLayoutParams().height = LinearLayout.LayoutParams.WRAP_CONTENT;
        statusLayout1.requestLayout();
        statusLayout2.getLayoutParams().height = LinearLayout.LayoutParams.WRAP_CONTENT;
        statusLayout2.requestLayout();
        isExpanded = false;
    }

}

