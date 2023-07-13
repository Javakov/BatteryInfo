package com.example.batterycheck;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

public class MainActivity extends AppCompatActivity {

    private TextView batteryInfoTextView;
    private BroadcastReceiver batteryInfoReceiver;

    private static final String CHANNEL_ID = "battery_channel";
    private static final int NOTIFICATION_ID = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        batteryInfoTextView = findViewById(R.id.battery_info_textview);

        createNotificationChannel();

        batteryInfoReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                updateBatteryInfo(intent);
            }
        };

        IntentFilter intentFilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        registerReceiver(batteryInfoReceiver, intentFilter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(batteryInfoReceiver);
    }

    private void updateBatteryInfo(Intent intent) {
        int level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
        int scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
        float batteryPercent = (level / (float) scale) * 100;

        boolean isCharging = intent.getIntExtra(BatteryManager.EXTRA_STATUS, -1) == BatteryManager.BATTERY_STATUS_CHARGING;
        String chargingStatus = isCharging ? "Да" : "Нет";

        int health = intent.getIntExtra(BatteryManager.EXTRA_HEALTH, BatteryManager.BATTERY_HEALTH_UNKNOWN);
        String healthStatus = getHealthStatusString(health);

        String batteryInfo = "Уровень заряда батареи: " + batteryPercent + "%" + "\n\n" +
                "Зарядка: " + chargingStatus + "\n\n" +
                "Состояние здоровья батареи: " + healthStatus;

        batteryInfoTextView.setText(batteryInfo);

        showNotification(batteryInfo);
    }

    private String getHealthStatusString(int health) {
        switch (health) {
            case BatteryManager.BATTERY_HEALTH_GOOD:
                return "Хорошее (Good)";
            case BatteryManager.BATTERY_HEALTH_OVERHEAT:
                return "Перегрето (Overheated)";
            case BatteryManager.BATTERY_HEALTH_DEAD:
                return "Разряжена (Dead)";
            case BatteryManager.BATTERY_HEALTH_OVER_VOLTAGE:
                return "Перенапряжение (Over Voltage)";
            case BatteryManager.BATTERY_HEALTH_UNSPECIFIED_FAILURE:
                return "Неуказанная неисправность (Unspecified Failure)";
            case BatteryManager.BATTERY_HEALTH_COLD:
                return "Холодное (Cold)";
            default:
                return "Неизвестно (Unknown)";
        }
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Battery Channel";
            String description = "Notification Channel for Battery Updates";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }
    }

    private void showNotification(String batteryInfo) {
        // Используем HTML-разметку для добавления переносов строк
        String formattedBatteryInfo = batteryInfo.replace("\n\n", "<br>");

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle("Информация о батарее:")
                .setStyle(new NotificationCompat.BigTextStyle().bigText(Html.fromHtml(formattedBatteryInfo)))
                .setPriority(NotificationCompat.PRIORITY_HIGH);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            builder.setChannelId(CHANNEL_ID);

        }

        Notification notification = builder.build();
        notification.flags |= Notification.FLAG_ONGOING_EVENT; // Предотвращает смахивание уведомления

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        notificationManager.notify(NOTIFICATION_ID, notification);
    }
}