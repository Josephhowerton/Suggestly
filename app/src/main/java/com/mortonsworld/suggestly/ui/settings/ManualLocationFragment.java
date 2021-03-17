package com.mortonsworld.suggestly.ui.settings;

import android.app.AlertDialog;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.mortonsworld.suggestly.R;
import com.mortonsworld.suggestly.adapter.LocationAutoCompleteAdapter;
import com.mortonsworld.suggestly.databinding.FragmentManualLocationBinding;

public class ManualLocationFragment extends Fragment implements SearchView.OnQueryTextListener, LocationAutoCompleteAdapter.LocationSearchAdapterListener {
    private FragmentManualLocationBinding binding;
    private SettingsViewModel settingsViewModel;
    private LocationAutoCompleteAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_manual_location, container, false);

        binding.searchView.setIconifiedByDefault(false);
        binding.searchView.setOnQueryTextListener(this);
        adapter = new LocationAutoCompleteAdapter(this);
        binding.recyclerView.setAdapter(adapter);
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false));
        binding.recyclerView.setItemAnimator(new DefaultItemAnimator());

        initializeSearchView();

        return binding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        settingsViewModel = new ViewModelProvider(this).get(SettingsViewModel.class);
        settingsViewModel.subscribeToAutoCompleteResults().observe(requireActivity(),
                autocompletePredictions -> adapter.setPredictions(autocompletePredictions));
    }

    private void initializeSearchView(){
        SearchView searchView = binding.searchView;
        searchView.setActivated(true);
        searchView.setIconified(false);
        searchView.setOnQueryTextListener(this);
    }


    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        settingsViewModel.searchManualLocationInput(newText);
        if(newText.isEmpty()){
            binding.recyclerView.setVisibility(View.GONE);
            binding.poweredByGoogle.setVisibility(View.GONE);
        }else{
            binding.recyclerView.setVisibility(View.VISIBLE);
            binding.poweredByGoogle.setVisibility(View.VISIBLE);
        }
        return true;
    }

    @Override
    public void onRowItemSelected(String address) {
        settingsViewModel.convertAddressToCoordinates(address).observe(requireActivity(), isSuccess -> {
            if(isSuccess){
                showOnSuccessDialog();
            }else{
                showOnFailureDialog();
            }
        });
    }

    private void showOnSuccessDialog(){
        new AlertDialog.Builder(requireContext()).setTitle("Location Updated")
                .setMessage("Using your new location to find suggestions in your area.")
                .setCancelable(false)
                .setPositiveButton("CONTINUE", (dialog, which) -> {
                    navigateBack();
                }).show();
    }

    private void showOnFailureDialog(){
        new AlertDialog.Builder(requireContext()).setTitle("Location Update Failed")
                .setMessage( "This is embarrassing! We apologize for the error, try again later")
                .setCancelable(false)
                .setPositiveButton("CONTINUE", (dialogInterface, i) -> {
                    dialogInterface.dismiss();
                    navigateBack();
                })
                .show();
    }

    private void navigateBack(){
        requireActivity().getSupportFragmentManager()
                .popBackStack();
    }
}