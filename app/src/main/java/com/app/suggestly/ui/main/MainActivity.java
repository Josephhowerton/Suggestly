package com.app.suggestly.ui.main;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.NavDestination;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.app.suggestly.R;

public class MainActivity extends AppCompatActivity implements NavController.OnDestinationChangedListener, View.OnClickListener{
    private BottomNavigationView navView;
    private OnReloadListener listener;
    private MainViewModel mViewModel;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
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

        mViewModel = new ViewModelProvider(this).get(MainViewModel.class);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mViewModel.removeSuggestlySearch();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == android.R.id.home){
            onBackPressed();
            return true;
        }
        else if(item.getItemId() == R.id.back_home){
            int id = getFragmentManager().getBackStackEntryAt(0).getId();
            getFragmentManager().popBackStack(id, 0);
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

    public interface OnReloadListener {
        void onReloadClickListener();
    }


}