package com.example.mywidget;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.widget.RemoteViews;

import androidx.annotation.Nullable;

import java.util.Calendar;
import java.util.Date;

/**
 * Implementation of App Widget functionality.
 */
public class TimeWidget extends AppWidgetProvider {

    public static final String TIME_WIDGET_UPDATE = "com.example.mywidget.TimeWidget.TIME_WIDGET_UPDATE";

    private PendingIntent createUpdateIntent(Context context)
    {
        Intent intent = new Intent(TIME_WIDGET_UPDATE);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        return pendingIntent;
    }

    @Override
    public void onDisabled(Context context) {
        super.onDisabled(context);
        AlarmManager alarmManager = (AlarmManager)  context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(createUpdateIntent(context));
    }

    @Override
    public void onEnabled(Context context) {
        super.onEnabled(context);
        AlarmManager alarmManager = (AlarmManager)  context.getSystemService(Context.ALARM_SERVICE);
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.add(Calendar.SECOND, 1);

        alarmManager.setRepeating(AlarmManager.RTC, calendar.getTimeInMillis(), 1000, createUpdateIntent(context));
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);

        if(TIME_WIDGET_UPDATE.equals(intent.getAction()))
        {
            context.startService(new Intent(context, UpdateService.class));
        }
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        super.onUpdate(context, appWidgetManager, appWidgetIds);
        context.startService(new Intent(context, UpdateService.class));
    }

    public static class UpdateService extends Service{

        @Nullable
        @Override
        public IBinder onBind(Intent intent) {
            return null;
        }

        @Override
        public void onStart(Intent intent, int startId) {
            RemoteViews updateViews = buildUpdate(this);
            ComponentName widget = new ComponentName(this, TimeWidget.class);
            AppWidgetManager manager = AppWidgetManager.getInstance(this);
            manager.updateAppWidget(widget, updateViews);
        }

        private RemoteViews buildUpdate(Context context){
            RemoteViews updateViews = new RemoteViews(context.getPackageName(), R.layout.time_widget);
            Calendar c = Calendar.getInstance();
            c.setTime(new Date());
            updateViews.setTextViewText(R.id.hours, String.valueOf(c.get(Calendar.HOUR_OF_DAY)));

            int min = c.get(Calendar.MINUTE);
            updateViews.setTextViewText(R.id.mins, (min < 10 ? "0" : "") + String.valueOf(min));
            return null;
        }
    }
}