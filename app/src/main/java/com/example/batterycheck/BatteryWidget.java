package com.example.batterycheck;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.os.CountDownTimer;
import android.widget.RemoteViews;

public class BatteryWidget extends AppWidgetProvider {

    private CountDownTimer countDownTimer;

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        super.onUpdate(context, appWidgetManager, appWidgetIds);

        // Обновление всех экземпляров виджета
        for (int appWidgetId : appWidgetIds) {
            updateWidget(context, appWidgetManager, appWidgetId);
        }

        // Инициализация CountDownTimer
        countDownTimer = new CountDownTimer(Long.MAX_VALUE, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                // Обновление виджета каждую секунду
                for (int appWidgetId : appWidgetIds) {
                    updateWidget(context, appWidgetManager, appWidgetId);
                }
            }

            @Override
            public void onFinish() {
                // Завершение таймера
            }
        };

        // Запуск обновления виджета
        countDownTimer.start();

        // Регистрация BroadcastReceiver для получения обновлений состояния батареи
        IntentFilter filter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        context.getApplicationContext().registerReceiver(batteryInfoReceiver, filter);
    }

    private void updateWidget(Context context, AppWidgetManager appWidgetManager, int appWidgetId) {
        // Создание RemoteViews для виджета
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.battery_widget);

        // Получение информации о батарее
        IntentFilter intentFilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        Intent batteryStatus = context.registerReceiver(null, intentFilter);
        if (batteryStatus != null) {
            String batteryInfo = updateBatteryInfo(context, batteryStatus);

            // Установка текста в RemoteViews
            views.setTextViewText(R.id.battery_info_textview, batteryInfo);
        }

        // Обновление виджета
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    private String updateBatteryInfo(Context context, Intent intent) {
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
        BatteryManager batteryManager = (BatteryManager) context.getSystemService(Context.BATTERY_SERVICE);
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

        String batteryInfo = "Заряд: " + level + "% " + "/ " + mAhChargeCounter + "mAh" + "\n" +
                "Состояние: " + chargingStatus + "\n" +
                "Источник: " + plugInfo + "\n" +
                "Здоровье: " + healthStatus + "\n" +
                "Макс. заряд: " + "≈" + maxBatteryCapacitymAh + " mAh" + "\n" +
                "Текущ. ток: " + mAhCurrentNow + " mA";


        return batteryInfo;
    }

    // Добавлен BroadcastReceiver для получения обновлений состояния батареи
    private BroadcastReceiver batteryInfoReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
            ComponentName thisAppWidget = new ComponentName(context.getPackageName(), BatteryWidget.class.getName());
            int[] appWidgetIds = appWidgetManager.getAppWidgetIds(thisAppWidget);

            for (int appWidgetId : appWidgetIds) {
                updateWidget(context, appWidgetManager, appWidgetId);
            }
        }
    };

    @Override
    public void onEnabled(Context context) {
        super.onEnabled(context);

        // Отправка широковещательного намерения
        Intent intent = new Intent(context, MainActivity.class);
        intent.setAction("com.example.batterycheck.BATTERY_UPDATE");
        context.sendBroadcast(intent);
    }

    @Override
    public void onDisabled(Context context) {
        super.onDisabled(context);
        // Остановка обновления виджета при отключении виджета
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
        
        // Отмена регистрации BroadcastReceiver при отключении виджета
        context.getApplicationContext().unregisterReceiver(batteryInfoReceiver);

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

    private String getPlugInfo(int plugId){
        switch (plugId){
            case BatteryManager.BATTERY_PLUGGED_AC:
                return "Зарядное устройство, которое подключено к розетке переменного тока.";
            case BatteryManager.BATTERY_PLUGGED_DOCK:
                return "Док-станция.";
            case BatteryManager.BATTERY_PLUGGED_USB:
                return "USB-порт.";
            case BatteryManager.BATTERY_PLUGGED_WIRELESS:
                return "Беспроводной.";
            default:
                return "Нет источника питания.";
        }
    }
}

