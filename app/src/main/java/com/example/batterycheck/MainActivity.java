package com.example.batterycheck;

import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.os.Bundle;
import android.os.Handler;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private TextView batteryInfoTextView;
    private TextView BATTERY_HEALTH_GOOD;
    private TextView BATTERY_HEALTH_OVERHEAT;
    private TextView BATTERY_HEALTH_DEAD;
    private TextView BATTERY_HEALTH_OVER_VOLTAGE;
    private TextView BATTERY_HEALTH_UNSPECIFIED_FAILURE;
    private TextView BATTERY_HEALTH_COLD;
    private TextView BATTERY_HEALTH_UNKNOWN;
    private Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        batteryInfoTextView = findViewById(R.id.battery_info_textview);
        BATTERY_HEALTH_GOOD = findViewById(R.id.BATTERY_HEALTH_GOOD);
        BATTERY_HEALTH_OVERHEAT = findViewById(R.id.BATTERY_HEALTH_OVERHEAT);
        BATTERY_HEALTH_DEAD = findViewById(R.id.BATTERY_HEALTH_DEAD);
        BATTERY_HEALTH_OVER_VOLTAGE = findViewById(R.id.BATTERY_HEALTH_OVER_VOLTAGE);
        BATTERY_HEALTH_UNSPECIFIED_FAILURE = findViewById(R.id.BATTERY_HEALTH_UNSPECIFIED_FAILURE);
        BATTERY_HEALTH_COLD = findViewById(R.id.BATTERY_HEALTH_COLD);
        BATTERY_HEALTH_UNKNOWN = findViewById(R.id.BATTERY_HEALTH_UNKNOWN);

        handler = new Handler();
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                updateBatteryInfo();
                handler.postDelayed(this, 1000); // Обновление каждую секунду (1000 миллисекунд)
            }
        };
        handler.postDelayed(runnable, 1000); // Запуск первого обновления через секунду (1000 миллисекунд)
    }

    private void updateBatteryInfo() {
        IntentFilter intentFilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        Intent batteryStatus = registerReceiver(null, intentFilter);

        int status = batteryStatus.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
        boolean isCharging = status == BatteryManager.BATTERY_STATUS_CHARGING ||
                status == BatteryManager.BATTERY_STATUS_FULL;

        int level = batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
        int scale = batteryStatus.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
        float batteryPercent = (level / (float) scale) * 100;

        int health = batteryStatus.getIntExtra(BatteryManager.EXTRA_HEALTH, BatteryManager.BATTERY_HEALTH_UNKNOWN);
        String healthStatus;
        switch (health) {
            case BatteryManager.BATTERY_HEALTH_GOOD:
                healthStatus = "Хорошее (Good)";
            break;
            case BatteryManager.BATTERY_HEALTH_OVERHEAT:
                healthStatus = "Перегрето (Overheated)";
            break;
            case BatteryManager.BATTERY_HEALTH_DEAD:
                healthStatus = "Разряжена (Dead)";
            break;
            case BatteryManager.BATTERY_HEALTH_OVER_VOLTAGE:
                healthStatus = "Перенапряжение (Over Voltage)";
            break;
            case BatteryManager.BATTERY_HEALTH_UNSPECIFIED_FAILURE:
                healthStatus = "Неуказанная неисправность (Unspecified Failure)";
            break;
            case BatteryManager.BATTERY_HEALTH_COLD:
                healthStatus = "Холодное (Cold)";
            break;
            default:
                healthStatus = "Неизвестно (Unknown)";
            break;
        }

        String batteryInfo = "Уровень заряда батареи: " + batteryPercent + "%" + "\n" + "\n"+
                "Зарядка: " + (isCharging ? "Да" : "Нет") + "\n" + "\n" +
                "Состояние здоровья батареи: " + healthStatus;

        batteryInfoTextView.setText(batteryInfo);

    }



    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacksAndMessages(null); // Очистка всех отложенных задач при уничтожении активити
    }
}