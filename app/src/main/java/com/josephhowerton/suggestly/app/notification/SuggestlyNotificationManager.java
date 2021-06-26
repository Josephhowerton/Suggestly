package com.josephhowerton.suggestly.app.notification;

import android.app.AlarmManager;
import android.app.Application;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import com.josephhowerton.suggestly.utility.Config;

import java.util.Calendar;

public class SuggestlyNotificationManager{
    private static AlarmManager alarmManager;
    private static PendingIntent pendingIntent;

    public static void enablePushNotifications(Application application){
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 12);
        alarmManager = (AlarmManager) application.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(application, SuggestlyNotificationReceiver.class);
        pendingIntent = PendingIntent.getBroadcast(application, Config.NOTIFICATION_CHANNEL_ID, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);
    }

    public static void disablePushNotifications(){
        if(alarmManager != null && pendingIntent != null){
            alarmManager.cancel(pendingIntent);
        }
    }
}
