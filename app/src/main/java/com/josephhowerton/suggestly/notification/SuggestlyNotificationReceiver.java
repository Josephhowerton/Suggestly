package com.josephhowerton.suggestly.notification;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.core.app.NotificationCompat;

import com.josephhowerton.suggestly.R;
import com.josephhowerton.suggestly.model.foursquare.Venue;
import com.josephhowerton.suggestly.room.FoursquareDao;
import com.josephhowerton.suggestly.room.RoomDB;
import com.josephhowerton.suggestly.ui.splash.SplashActivity;
import com.josephhowerton.suggestly.utility.Config;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SuggestlyNotificationReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        ExecutorService executorService = Executors.newFixedThreadPool(1);
        RoomDB roomDB = RoomDB.getInstance(context);
        FoursquareDao foursquareDao = roomDB.getFoursquareDao();
        try{
            Venue venue = executorService.submit(foursquareDao::readRandomRecommendedVenue).get();
            String title = context.getString(R.string.title_push_notification_notification);
            String message = context.getString(R.string.message_push_notification_notification, venue.getName());
            notificationBuilder(context, title, message);
        }
        catch (ExecutionException |InterruptedException e){
            //do not send
        }

    }

    public void notificationBuilder(Context context, String title , String body){
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if(Build.VERSION.SDK_INT>= Build.VERSION_CODES.O){
            final boolean enableLights = true;
            final NotificationChannel notificationChannel = new NotificationChannel(Config.NOTIFICATION_CHANNEL, Config.NOTIFICATION_CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH);
            notificationChannel.setDescription(Config.NOTIFICATION_CHANNEL_DESCRIPTION);
            notificationChannel.enableLights(enableLights);
            notificationManager.createNotificationChannel(notificationChannel);
        }

        final NotificationCompat.Builder builder = new NotificationCompat.Builder(context, Config.NOTIFICATION_CHANNEL);

        Intent intent = new Intent(context, SplashActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        PendingIntent pendingIntent = PendingIntent.getActivity(context, Config.NOTIFICATION_CHANNEL_ID, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        builder.setAutoCancel(true)
                .setContentIntent(pendingIntent)
                .setContentText(body)
                .setContentTitle(title)
                .setSmallIcon(R.drawable.suggest_logo);

        notificationManager.notify(Config.NOTIFICATION_CHANNEL_ID, builder.build());
    }
}
