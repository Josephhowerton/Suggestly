package com.mortonsworld.suggest.utility;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class PermissionManager {
    private final SharedPreferences sharedPreferenceSource;
    private final SharedPreferences.Editor editor;

    public PermissionManager(Application application){
        sharedPreferenceSource = application.getSharedPreferences("DEFAULT", Context.MODE_PRIVATE);
        editor = sharedPreferenceSource.edit();
    }

    private boolean shouldAskPermission(){
        return (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M);
    }

    public boolean shouldAskPermission(Context context, String permission){
        if(shouldAskPermission()){
            int permissionResult = ContextCompat.checkSelfPermission(context, permission);
            return permissionResult != PackageManager.PERMISSION_GRANTED;
        }
        return false;
    }

    public void checkPermission(Context context, String permission, PermissionAskListener permissionAskListener){
        if(shouldAskPermission(context,permission)){
            if (ActivityCompat.shouldShowRequestPermissionRationale((AppCompatActivity) context, permission)){
                permissionAskListener.onPermissionPreviouslyDenied();
            }else{
                if(isFirstTimeAsking(permission)){
                    firstTimeAsking(permission, false);
                    permissionAskListener.onNeedPermission();
                }else{
                    permissionAskListener.onPermissionPreviouslyDeniedWithNeverAskAgain();
                }
            }
        }
        else{
            permissionAskListener.onPermissionGranted();
        }

    }

    public void isLocationPermissionNeeded(Context context, String permission, LocationPermissionListener locationPermissionListener){
        if(shouldAskPermission(context, permission)){
            locationPermissionListener.onLocationPermissionDenied();
        }else{
            locationPermissionListener.onLocationPermissionGranted();
        }
    }

    public void  firstTimeAsking(String permission, boolean isFirstTimeAsking){
        updateSharedPreferences(permission, isFirstTimeAsking);
    }

    public boolean isFirstTimeAsking(String permission){
        return getValueFromSharedPreferences(permission, true);
    }

    private void updateSharedPreferences(String key, Boolean target){
        editor.putBoolean(key, target);
        editor.apply();
    }

    private Boolean getValueFromSharedPreferences(String key, Boolean defaultValue){
        return sharedPreferenceSource.getBoolean(key, defaultValue);
    }

    public interface LocationPermissionListener{
        void onLocationPermissionGranted();
        void onLocationPermissionDenied();
    }

    public interface PermissionAskListener{
        void onNeedPermission();
        void onPermissionPreviouslyDenied();
        void onPermissionPreviouslyDeniedWithNeverAskAgain();
        void onPermissionGranted();
    }
}