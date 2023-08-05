package com.example.batterycheck;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {

    private TextView batteryInfoTextView;
    private BroadcastReceiver batteryInfoReceiver;
    private ScheduledExecutorService executorService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        batteryInfoTextView = findViewById(R.id.battery_info_textview);
        batteryInfoReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                updateBatteryInfo(intent);
            }
        };

        IntentFilter intentFilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        registerReceiver(batteryInfoReceiver, intentFilter);

        executorService = Executors.newSingleThreadScheduledExecutor();
        executorService.scheduleAtFixedRate(updateBatteryRunnable, 0, 1, TimeUnit.SECONDS);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(batteryInfoReceiver);
        executorService.shutdown();
    }

    private void updateBatteryInfo(Intent intent) {
        int level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
        int scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
        float batteryPercent = (level / (float) scale) * 100;

        boolean isCharging = intent.getIntExtra(BatteryManager.EXTRA_STATUS, -1) == BatteryManager.BATTERY_STATUS_CHARGING;
        String chargingStatus = isCharging ? "Заряжается" : "Не заряжается";

        int health = intent.getIntExtra(BatteryManager.EXTRA_HEALTH, BatteryManager.BATTERY_HEALTH_UNKNOWN);
        String healthStatus = getHealthStatusString(health);

        int plug = intent.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1);
        String plugInfo = getPlugInfo(plug);

        // Получение информации о каждой константе
        BatteryManager batteryManager = (BatteryManager) getSystemService(Context.BATTERY_SERVICE);
        int chargeCounter = batteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CHARGE_COUNTER);
        double mAhChargeCounter = chargeCounter / 1000.0;

        double batteryCapacitymAh = level * mAhChargeCounter / 100.0;
        double maxBatteryCapacitymAh = batteryCapacitymAh + mAhChargeCounter;

        int currentAverage = batteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CURRENT_AVERAGE);
        double mAhCurrentAverage = currentAverage / 1000.0;

        int currentNow = batteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CURRENT_NOW);
        double mAhCurrentNow = currentNow / 1000.0;

        long energyCounter = batteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_ENERGY_COUNTER);
        double vtEnergyCounter = energyCounter / 1000000000.0;

        String batteryInfo = "Уровень заряда батареи (%): " + level + "%" + "\n\n" +
                "Уровень заряда батареи (mAh): " + mAhChargeCounter + " mAh" + "\n\n" +
                "Состояние: " + chargingStatus + "\n\n" +
                "Источник питания: " + plugInfo + "\n\n" +
                "Состояние здоровья батареи: " + healthStatus + "\n\n" +
                "Максимальный уровень заряда батареи (mAh): " + " ≈" + maxBatteryCapacitymAh + " mAh" + "\n\n" +
                "Количество зарядки, которую аккумулятор уже использовал или отдал (mAh): " + batteryCapacitymAh + " mAh" +  "\n\n" +
                "Средний ток батареи: " + mAhCurrentAverage + " mA" + "\n\n" +
                "Текущий ток батареи: " + mAhCurrentNow + " mA" + "\n\n" +
                "Оставшийся заряд батареи (Wh): " + vtEnergyCounter + " Wh";

        batteryInfoTextView.setText(batteryInfo);
    }

    private final Runnable updateBatteryRunnable = new Runnable() {
        @Override
        public void run() {
            IntentFilter intentFilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
            Intent batteryStatus = registerReceiver(null, intentFilter);
            if (batteryStatus != null) {
                updateBatteryInfo(batteryStatus);
            }
        }
    };

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

    private String getPlugInfo(int plugId){
        switch (plugId){
            case BatteryManager.BATTERY_PLUGGED_AC:
                return "Зарядное устройство, которое подключено к розетке переменного тока.";
            case BatteryManager.BATTERY_PLUGGED_DOCK:
                return "Док-станция.";
            case BatteryManager.BATTERY_PLUGGED_USB:
                return "USB-порт (от компьютера, ноутбука и т.д.)";
            case BatteryManager.BATTERY_PLUGGED_WIRELESS:
                return "Беспроводной.";
            default:
                return "Нет источника питания.";
        }
    }

    private String getMahInfo(int mahId){
        switch (mahId){
            case BatteryManager.BATTERY_PROPERTY_CAPACITY:
                return "Оставшаяся емкость аккумулятора в виде целого процента от общей емкости";
            case BatteryManager.BATTERY_PROPERTY_CHARGE_COUNTER:
                return "Емкость аккумулятора в микроампер-часах, как целое число.";
            case BatteryManager.BATTERY_PROPERTY_CURRENT_AVERAGE:
                return "Средний ток батареи в микроамперах, как целое число.";
            case BatteryManager.BATTERY_PROPERTY_CURRENT_NOW:
                return "Оставшаяся емкость аккумулятора в виде целого процента от общей емкости";
            case BatteryManager.BATTERY_PROPERTY_ENERGY_COUNTER:
                return "Оставшаяся емкость аккумулятора в виде целого процента от общей емкости";
            case BatteryManager.BATTERY_PROPERTY_STATUS:
                return "Оставшаяся емкость аккумулятора в виде целого процента от общей емкости";
            default:
                return "error";
        }
    }
}