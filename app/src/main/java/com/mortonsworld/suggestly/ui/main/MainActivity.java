package com.mortonsworld.suggestly.ui.main;

import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.app.Fragment;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.mortonsworld.suggestly.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.mortonsworld.suggestly.ui.home.HomeFragment;
import com.mortonsworld.suggestly.ui.home.HomeViewModel;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.NavDestination;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

public class MainActivity extends AppCompatActivity implements NavController.OnDestinationChangedListener, View.OnClickListener{
    private BottomNavigationView navView;
    private OnReloadListener listener;
    private FloatingActionButton FAB;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        navView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_search, R.id.navigation_library)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        navController.addOnDestinationChangedListener(this);
        NavigationUI.setupWithNavController(navView, navController);

        FAB = findViewById(R.id.reload_floating_action_button);
        FAB.setOnClickListener(view -> listener.onReloadClickListener());
        HomeViewModel homeViewModel = new ViewModelProvider(this).get(HomeViewModel.class);
        homeViewModel.getLoad().observe(this, aBoolean -> {
            if(aBoolean) {
                FAB.setVisibility(View.VISIBLE);
            }else{
                FAB.setVisibility(View.GONE);
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == android.R.id.home){
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDestinationChanged(@NonNull NavController controller, @NonNull NavDestination destination, @Nullable Bundle arguments) {
        if(R.id.navigation_home == destination.getId() || R.id.navigation_search == destination.getId()
                || R.id.navigation_library == destination.getId()){
            navView.setVisibility(View.VISIBLE);
            return;
        }
        navView.setVisibility(View.GONE);
    }

    @Override
    public void onClick(View v) {
        listener.onReloadClickListener();
    }

    public void setOnReloadClickListener(OnReloadListener listener){
        this.listener = listener;
    }

    public void showReloadVenueOptionListener(){
        ObjectAnimator scaleDown = ObjectAnimator.ofPropertyValuesHolder(FAB,
                PropertyValuesHolder.ofFloat("scaleX", 1.2f),
                PropertyValuesHolder.ofFloat("scaleY", 1.2f));
        scaleDown.setDuration(310);
        scaleDown.setRepeatCount(ObjectAnimator.INFINITE);
        scaleDown.setRepeatMode(ObjectAnimator.REVERSE);
        scaleDown.start();
    }

    public interface OnReloadListener {
        void onReloadClickListener();
    }


}