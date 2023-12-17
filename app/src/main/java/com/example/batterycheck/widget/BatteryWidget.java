package com.example.batterycheck.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.CountDownTimer;
import android.util.Log;
import android.widget.RemoteViews;

import com.example.batterycheck.MainActivity;
import com.example.batterycheck.R;
import com.example.batterycheck.helper.UpdateBatteryInfo;

public class BatteryWidget extends AppWidgetProvider {

    private CountDownTimer countDownTimer;

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        super.onUpdate(context, appWidgetManager, appWidgetIds);

        for (int appWidgetId : appWidgetIds) {
            updateWidget(context, appWidgetId);
        }

        countDownTimer = new CountDownTimer(Long.MAX_VALUE, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                for (int appWidgetId : appWidgetIds) {
                    updateWidget(context, appWidgetId);
                }
            }

            @Override
            public void onFinish() {
            }
        };

        countDownTimer.start();

        IntentFilter filter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        context.getApplicationContext().registerReceiver(batteryInfoReceiver, filter);
    }

    private void updateWidget(Context context, int appWidgetId) {
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.battery_widget);
        UpdateBatteryInfo helper = new UpdateBatteryInfo();

        IntentFilter intentFilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        Intent batteryStatus = context.registerReceiver(null, intentFilter);
        if (batteryStatus != null) {
            String batteryInfo = helper.updateBatteryInfo(context, batteryStatus);
            views.setTextViewText(R.id.battery_info_textview, batteryInfo);
        }

        // Обработка нажатия на виджет
        Intent intent = new Intent(context, BatteryWidget.class);
        intent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, new int[]{appWidgetId});
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                context, appWidgetId, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        views.setOnClickPendingIntent(R.id.battery_info_textview, pendingIntent);

        appWidgetManager.updateAppWidget(appWidgetId, views);
        Log.d("BatteryWidget", "Widget updated: " + appWidgetId);
    }

    private final BroadcastReceiver batteryInfoReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
            ComponentName thisAppWidget = new ComponentName(context.getPackageName(), BatteryWidget.class.getName());
            int[] appWidgetIds = appWidgetManager.getAppWidgetIds(thisAppWidget);

            for (int appWidgetId : appWidgetIds) {
                updateWidget(context, appWidgetId);
            }
        }
    };

    @Override
    public void onEnabled(Context context) {
        super.onEnabled(context);

        Intent intent = new Intent(context, MainActivity.class);
        intent.setAction("com.example.batterycheck.BATTERY_UPDATE");
        context.sendBroadcast(intent);

        // Запуск таймера
        if (countDownTimer != null) {
            countDownTimer.start();
        }
    }

    @Override
    public void onDisabled(Context context) {
        super.onDisabled(context);
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }

        context.getApplicationContext().unregisterReceiver(batteryInfoReceiver);
    }
}

