package com.example.batterycheck.helper;

import android.content.Context;
import android.content.Intent;
import android.os.BatteryManager;

public class UpdateBatteryInfo {
    public String updateBatteryInfo(Context context, Intent intent) {
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

        double maxBatteryCapacitymAh = 100.0 * mAhChargeCounter / batteryPercent;

        int currentNow = batteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CURRENT_NOW);
        double mAhCurrentNow = currentNow / 1000.0;

        int batteryTemperature = intent.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, -1);
        double celsBatteryTemperature = batteryTemperature / 10.0;

        int batteryVoltage = intent.getIntExtra(BatteryManager.EXTRA_VOLTAGE, -1);
        double voltBatteryVoltage = batteryVoltage / 1000.0;

        return "Заряд: " + batteryPercent + "% " + "/ " + mAhChargeCounter + "mAh" + "\n" +
                "Состояние: " + chargingStatus + "\n" +
                "Источник: " + plugInfo + "\n" +
                "Здоровье: " + healthStatus + "\n" +
                "Макс. заряд: " + "≈" + maxBatteryCapacitymAh + " mAh" + "\n" +
                "Текущ. ток: " + mAhCurrentNow + " mA" + "\n" +
                "Текущ. напряж: " + voltBatteryVoltage + " V" + "\n" +
                "Текущ. темп: " + celsBatteryTemperature + " °C";
    }

    public String getHealthStatusString(int health) {
        return switch (health) {
            case BatteryManager.BATTERY_HEALTH_GOOD -> "Хорошее (Good)";
            case BatteryManager.BATTERY_HEALTH_OVERHEAT -> "Перегрето (Overheated)";
            case BatteryManager.BATTERY_HEALTH_DEAD -> "Разряжена (Dead)";
            case BatteryManager.BATTERY_HEALTH_OVER_VOLTAGE -> "Перенапряжение (Over Voltage)";
            case BatteryManager.BATTERY_HEALTH_UNSPECIFIED_FAILURE ->
                    "Неуказанная неисправность (Unspecified Failure)";
            case BatteryManager.BATTERY_HEALTH_COLD -> "Холодное (Cold)";
            default -> "Неизвестно (Unknown)";
        };
    }

    public String getPlugInfo(int plugId){
        return switch (plugId) {
            case BatteryManager.BATTERY_PLUGGED_AC -> "Розетка перемен. тока";
            case BatteryManager.BATTERY_PLUGGED_DOCK -> "Док-станция";
            case BatteryManager.BATTERY_PLUGGED_USB -> "USB-порт";
            case BatteryManager.BATTERY_PLUGGED_WIRELESS -> "Беспроводной";
            default -> "Нет источника питания";
        };
    }
}
