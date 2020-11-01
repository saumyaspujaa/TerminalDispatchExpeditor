package com.digipodium.tde.Auth;

import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.digipodium.tde.R;
import com.digipodium.tde.databinding.FragmentDeliveryPersonLoginBinding;
import com.digipodium.tde.databinding.FragmentEndUserLoginBinding;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


public class EndUserLoginFragment extends Fragment {


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_end_user_login, container, false);
    }


    private com.digipodium.tde.databinding.FragmentEndUserLoginBinding binding;
    private FirebaseAuth auth;


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding = FragmentEndUserLoginBinding.bind(view);
        auth = FirebaseAuth.getInstance();
        binding.btnUserLogin.setOnClickListener(v1 -> {
            String email = binding.editUserEmail.getText().toString();
            String password = binding.editUserPassword.getText().toString();
            if (email.length() >= 11 && password.length() >= 8) {
                binding.pbAdmin.setVisibility(View.VISIBLE);
                auth.signInWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        String successMsg = "Login Successful";
                        FirebaseUser user = task.getResult().getUser();
                        updateUI(user, successMsg);
                    } else {
                        String errorMsg = task.getException().getMessage();
                        updateUI(null, errorMsg);
                    }
                });
            }
        });
        binding.btnForgotPassword.setOnClickListener(v2 -> {
            NavHostFragment.findNavController(this)
                    .navigate(R.id.action_deliveryPersonLoginFragment_to_forgotPasswordFragment);
        });
        binding.btnUserRegister.setOnClickListener(v3 -> {
            NavHostFragment.findNavController(this)
                    .navigate(R.id.action_deliveryPersonLoginFragment_to_deliveryPersonRegisterFragment);
        });
    }

    private void updateUI(FirebaseUser user, String msg) {
        binding.pbAdmin.setVisibility(View.GONE);
        if (user != null) {
            NavHostFragment.findNavController(this).navigate(R.id.action_adminLoginFragment_to_adminDashboardFragment);
        } else {
            Snackbar.make(binding.btnUserRegister, msg, Snackbar.LENGTH_LONG).setBackgroundTint(Color.RED).show();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}