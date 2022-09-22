package com.app.suggestly.ui.push;

import android.app.Application;
import androidx.lifecycle.AndroidViewModel;
import com.app.suggestly.app.Repository;

public class PushNotificationViewModel extends AndroidViewModel {

    private final Repository repository;

    public PushNotificationViewModel(Application application){
        super(application);
        repository = Repository.getInstance(application);
    }

    public void updatePushNotificationPermission(){
        repository.hasAskForPushNotificationsPreviously();
    }

    public void enablePushNotifications(Application application){
        repository.enablePushNotifications(application);
    }

    public void disablePushNotifications(){
        repository.disablePushNotifications();
    }
}