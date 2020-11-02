package com.digipodium.tde.Auth;

import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavHost;
import androidx.navigation.fragment.NavHostFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.digipodium.tde.R;
import com.digipodium.tde.databinding.FragmentAdminLoginBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


public class AdminLoginFragment extends Fragment {

    private com.digipodium.tde.databinding.FragmentAdminLoginBinding binding;
    private FirebaseAuth auth;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_admin_login, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding = FragmentAdminLoginBinding.bind(view);
        auth = FirebaseAuth.getInstance();
        binding.btnAdmLogin.setOnClickListener(v1 -> {
            String email = binding.editAdmEmail.getText().toString();
            String password = binding.editAdmPassword.getText().toString();
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
    }

    private void updateUI(FirebaseUser user, String msg) {
        binding.pbAdmin.setVisibility(View.GONE);
        if (user != null) {
            NavHostFragment.findNavController(this).navigate(R.id.action_adminLoginFragment_to_adminDashboardFragment);
        } else {
            Snackbar.make(binding.btnAdmLogin, msg, Snackbar.LENGTH_LONG).setBackgroundTint(Color.RED).show();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onStart() {
        super.onStart();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user!=null){
            updateUI(user,"authenticating...");
        }
    }
}