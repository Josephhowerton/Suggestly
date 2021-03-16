package com.mortonsworld.suggestly.ui.splash;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.os.Bundle;

import com.mortonsworld.suggestly.R;
import com.mortonsworld.suggestly.model.user.User;
import com.mortonsworld.suggestly.ui.init.InitializeActivity;
import com.mortonsworld.suggestly.ui.main.MainActivity;
import com.mortonsworld.suggestly.utility.DistanceCalculator;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Arrays;
import java.util.List;

public class SplashActivity extends AppCompatActivity {
    private final int RC_SIGN_IN = 0;
    private SplashViewModel viewModel;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new ViewModelProvider(this).get(SplashViewModel.class);
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if(firebaseUser == null){
            goToAuthActivity();
        }else{
            User user = new User(firebaseUser);
            checkIfUserInRoom(user);
        }
    }

    public void goToAuthActivity(){
        List<AuthUI.IdpConfig> providers = Arrays.asList(
                new AuthUI.IdpConfig.EmailBuilder().build(),
                new AuthUI.IdpConfig.PhoneBuilder().build(),
                new AuthUI.IdpConfig.GoogleBuilder().build()
        );

        Intent intent = AuthUI.getInstance()
                .createSignInIntentBuilder()
                .setAvailableProviders(providers)
                .setTheme(R.style.Theme_Suggest_NoActionBar)
                .setLogo(R.drawable.suggest_logo)
                .setIsSmartLockEnabled(false)
                .build();
        startActivityForResult(intent, RC_SIGN_IN);
    }

    public void goToMainActivity(){
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    public void goToInitializeActivity(){
        Intent intent = new Intent(this, InitializeActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    public void authenticateUser(IdpResponse response){
        viewModel.authenticateUser(response).observe(this, target -> {
            if(FirebaseAuth.getInstance().getCurrentUser() != null){
                User user = new User(FirebaseAuth.getInstance().getCurrentUser());
                if(target) checkIfUserInRoom(user);
                else createUser(user);
            }
        });
    }

    public void checkIfUserInRoom(User user){
        viewModel.checkIfUserInRoom(user).observe(this, target -> {
            if(target){
                readUserLocation(user);
            }else{
                createUser(user);
            }
        });
    }

    public void readUserLocation(User user){
        viewModel.readUserLocation(user).observe(this, target -> {
            if(DistanceCalculator.hasValidLocation(target.lat, target.lng)){
                isFoursquareTableFresh(target.lat, target.lng);
            }else{
                goToInitializeActivity();
            }
        });
    }
    public void isFoursquareTableFresh(double lat, double lng){
        viewModel.isFoursquareTableFresh(lat, lng).observe(this, isFresh ->{
            if(isFresh){
                goToMainActivity();
            }else{
                goToInitializeActivity();
            }
        });
    }

    public void createUser(User user){
        viewModel.createUser(user).observe(this, target -> goToInitializeActivity());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == RC_SIGN_IN){
            IdpResponse response = IdpResponse.fromResultIntent(data);
            if(resultCode == RESULT_OK){
                authenticateUser(response);
            }else{
                goToAuthActivity();
            }
        }
    }
}