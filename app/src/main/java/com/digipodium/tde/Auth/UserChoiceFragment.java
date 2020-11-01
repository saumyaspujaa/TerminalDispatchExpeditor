package com.digipodium.tde.Auth;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.digipodium.tde.R;
import com.digipodium.tde.databinding.FragmentUserChoiceBinding;


public class UserChoiceFragment extends Fragment {

    private com.digipodium.tde.databinding.FragmentUserChoiceBinding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_user_choice, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding = FragmentUserChoiceBinding.bind(view);
        binding.btnDeliveryLogin.setOnClickListener(v1 -> {
            NavHostFragment.findNavController(this)
                    .navigate(R.id.action_userChoiceFragment2_to_deliveryPersonLoginFragment);
        });
        binding.btnAdminLogin.setOnClickListener(v2 -> {
            NavHostFragment.findNavController(this)
                    .navigate(R.id.action_userChoiceFragment2_to_adminLoginFragment);
        });
        binding.btnUserLogin.setOnClickListener(v3 -> {
            NavHostFragment.findNavController(this)
                    .navigate(R.id.action_userChoiceFragment2_to_endUserLoginFragment);
        });
    }
}