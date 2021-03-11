package com.mortonsworld.suggest.ui.location;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;

import android.app.AlertDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mortonsworld.suggest.R;
import com.mortonsworld.suggest.adapter.LocationAutoCompleteAdapter;
import com.mortonsworld.suggest.databinding.FragmentManualLocationBinding;

public class ManualLocationFragment extends Fragment implements SearchView.OnQueryTextListener, LocationAutoCompleteAdapter.LocationSearchAdapterListener {
    private FragmentManualLocationBinding binding;
    private ManualLocationViewModel mViewModel;
    private LocationAutoCompleteAdapter adapter;

    public static ManualLocationFragment newInstance() {
        return new ManualLocationFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_manual_location, container, false);
        ((AppCompatActivity) requireActivity()).setSupportActionBar(binding.toolbar);
        ActionBar actionBar = ((AppCompatActivity) requireActivity()).getSupportActionBar();
        if(actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowHomeEnabled(true);
        }

        binding.searchView.setIconifiedByDefault(false);
        binding.searchView.setOnQueryTextListener(this);
        adapter = new LocationAutoCompleteAdapter(this);
        binding.recyclerView.setAdapter(adapter);
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false));
        binding.recyclerView.setItemAnimator(new DefaultItemAnimator());

        return binding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(ManualLocationViewModel.class);
        mViewModel.listenForAutoCompleteResults().observe(requireActivity(),
                autocompletePredictions -> adapter.setPredictions(autocompletePredictions));
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        mViewModel.searchManualLocationInput(newText);
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
        mViewModel.convertAddressToCoordinates(address).observe(requireActivity(), isSuccess -> {
            if(isSuccess){
                navigateToFinalizeFragment();
            }else{
                showAddressConversionFailure();
            }
        });
    }

    private void showAddressConversionFailure(){
        new AlertDialog.Builder(requireContext()).setTitle("Failed to Convert to Geocode")
                .setMessage("Once an address is entered Suggest tries to geocode it. However there was an error during this conversion. Try Again?")
                .setCancelable(false)
                .setNegativeButton("LATER", (dialogInterface, i) -> {
                    requireActivity().getSupportFragmentManager().popBackStack();
                    dialogInterface.dismiss();
                })
                .setPositiveButton("CONTINUE", (dialogInterface, i) -> {
                    dialogInterface.dismiss();
                })
                .show();
    }

    public void navigateToFinalizeFragment(){
        Navigation.findNavController(requireActivity(), R.id.nav_host_fragment).navigate(R.id.navigation_finalize);
    }
}