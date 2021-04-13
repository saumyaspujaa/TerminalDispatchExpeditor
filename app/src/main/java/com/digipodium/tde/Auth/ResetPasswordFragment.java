package com.digipodium.tde.Auth;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.digipodium.tde.R;
import com.digipodium.tde.databinding.FragmentResetPasswordBinding;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;


public class ResetPasswordFragment extends Fragment {


    private FragmentResetPasswordBinding binding;
    private FirebaseAuth auth;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_reset_password, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        auth = FirebaseAuth.getInstance();
        binding = FragmentResetPasswordBinding.bind(view);
        binding.btnResetPassword.setOnClickListener(view1 -> {
            String p1 = binding.editNewPassword.getText().toString();
            String p2 = binding.editConfirmPassword.getText().toString();
            if (p1.length() >= 8) {
                if (p2.equals(p1)) {
                    binding.pbForgot.setVisibility(View.VISIBLE);
                    auth.confirmPasswordReset(p1, p2).addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            String successMsg = "Your password is changed";
                            updateUI(successMsg);
                        } else {
                            String errorMsg = task.getException().getMessage();
                            updateUI(errorMsg);
                        }
                    });
                }else{
                    binding.editConfirmPassword.setError("Passwords do not match");
                    binding.editConfirmPassword.requestFocus();
                }
            }else{
                binding.editNewPassword.setError("Password is too small, read the instructions");
                binding.editNewPassword.requestFocus();
            }

        });
    }

    private void updateUI(String msg) {
        binding.pbForgot.setVisibility(View.GONE);
        Snackbar.make(binding.btnResetPassword, msg, Snackbar.LENGTH_INDEFINITE).show();
    }
}