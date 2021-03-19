package com.mortonsworld.suggestly.ui.init;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.NavDestination;
import androidx.navigation.Navigation;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import com.mortonsworld.suggestly.R;
import com.mortonsworld.suggestly.utility.Config;

public class InitializeActivity extends AppCompatActivity implements NavController.OnDestinationChangedListener {
    NavController navController;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_initialize);
        navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        navController.addOnDestinationChangedListener(this);

        Intent intent = getIntent();
        if(intent != null){
            int whereTo = intent.getIntExtra(Config.INITIALIZE, 0);
            navigate(whereTo);
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == android.R.id.home){
            navController.popBackStack();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDestinationChanged(@NonNull NavController controller, @NonNull NavDestination destination, @Nullable Bundle arguments) {

    }

    public void navigate(int whereTo){
        switch (whereTo){
            case Config.LOCATION_FLAG:
                Navigation.findNavController(this, R.id.nav_host_fragment).navigate(R.id.navigation_location_services);
                break;
            case Config.REFRESH_FLAG:
                Navigation.findNavController(this, R.id.nav_host_fragment).navigate(R.id.navigation_finalize);
        }
    }
}