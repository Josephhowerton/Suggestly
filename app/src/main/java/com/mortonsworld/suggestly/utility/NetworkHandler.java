package com.mortonsworld.suggestly.utility;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import androidx.appcompat.app.AlertDialog;

import com.mortonsworld.suggestly.R;

public class NetworkHandler {
    public static Boolean isNetworkConnectionActive(Activity activity){
        ConnectivityManager manager = (ConnectivityManager) activity.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnectedOrConnecting());
    }

    public static AlertDialog notifyBadConnectionAndTerminate(Activity activity){
        return new AlertDialog.Builder(activity).setTitle(activity.getString(R.string.title_network_error))
                .setMessage(activity.getString(R.string.message_network_error_terminate))
                .setCancelable(false)
                .setPositiveButton(activity.getString(R.string.title_continue), (dialog, which) -> {
                    dialog.dismiss();
                    activity.finish();
                }).show();
    }

    public static AlertDialog notifyBadConnectionAndGoBack(Activity activity, String message){
        return new AlertDialog.Builder(activity).setTitle(activity.getString(R.string.title_network_error))
                .setMessage(String.format(activity.getString(R.string.message_network_error_dismiss), message))
                .setCancelable(false)
                .setPositiveButton(activity.getString(R.string.title_continue), (dialog, which) -> {
                    dialog.dismiss();
                    activity.onBackPressed();
                }).show();
    }

    public static AlertDialog notifyBadConnectionAndDismiss(Activity activity, String message){
        return new AlertDialog.Builder(activity).setTitle(activity.getString(R.string.title_network_error))
                .setMessage(String.format(activity.getString(R.string.message_network_error_dismiss), message))
                .setCancelable(false)
                .setPositiveButton(activity.getString(R.string.title_continue), (dialog, which) -> {
                    dialog.dismiss();
                }).show();
    }
}
