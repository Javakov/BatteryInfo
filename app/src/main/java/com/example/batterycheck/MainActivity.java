package com.example.batterycheck;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.batterycheck.activity.DocActivity;
import com.example.batterycheck.activity.StatusActivity;

public class MainActivity extends AppCompatActivity {
    private LinearLayout statusLayout1;
    private LinearLayout statusLayout2;
    private boolean isExpanded = false;

    private boolean isButtonClickable = true;

    private static final long ANIMATION_DURATION = 1500;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button statusButton = findViewById(R.id.statusButton);
        statusLayout1 = findViewById(R.id.statusLayout1);
        statusLayout2 = findViewById(R.id.statusLayout2);
        Button docButton = findViewById(R.id.docButton);

        Intent intent = getIntent();
        if (intent != null && intent.getBooleanExtra("isExpanded", false)) {
            resetLayout();
        }

        showInstructionDialog();

        statusButton.setOnClickListener(view -> {
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
                    Intent intent12 = new Intent(MainActivity.this, StatusActivity.class);
                    startActivity(intent12);
                }
            }.start();
        });

        docButton.setOnClickListener(view -> {
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
                }

                @Override
                public void onFinish() {
                    Intent intent1 = new Intent(MainActivity.this, DocActivity.class);
                    startActivity(intent1);
                }
            }.start();
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
        animator.addUpdateListener(valueAnimator -> {
            view.getLayoutParams().height = (int) valueAnimator.getAnimatedValue();
            view.requestLayout();
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

    private void showInstructionDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_instruction, null);
        builder.setView(dialogView);

        AlertDialog dialog = builder.create();

        Button skipButton = dialogView.findViewById(R.id.skipButton);
        skipButton.setOnClickListener(v -> dialog.dismiss());

        Button rustoreButton = dialogView.findViewById(R.id.rustoreButton);
        rustoreButton.setOnClickListener(v -> {
            String url = "https://apps.rustore.ru/app/com.example.batterycheck";
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            startActivity(intent);
        });
        dialog.show();
    }
}