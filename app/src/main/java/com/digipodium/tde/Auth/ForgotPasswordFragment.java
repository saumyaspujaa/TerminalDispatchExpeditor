package com.digipodium.tde.Auth;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.digipodium.tde.R;
import com.digipodium.tde.databinding.FragmentForgotPasswordBinding;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ForgotPasswordFragment extends Fragment {


    private com.digipodium.tde.databinding.FragmentForgotPasswordBinding binding;
    private FirebaseAuth auth;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_forgot_password, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        auth = FirebaseAuth.getInstance();
        binding = FragmentForgotPasswordBinding.bind(view);
        binding.btnSend.setOnClickListener(v1->{
            String email = binding.editRegEmail.getText().toString();
            if (email.length()>=11){
                binding.pb.setVisibility(View.VISIBLE);
                auth.sendPasswordResetEmail(email).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        String successMsg = "Reset Password link sent, check your email";
                        updateUI(successMsg);
                    } else {
                        String errorMsg = task.getException().getMessage();
                        updateUI(errorMsg);
                    }
                });
            }else{
                binding.editRegEmail.setError("invalid email");
                binding.editRegEmail.requestFocus();
            }
        });
    }

    private void updateUI(String msg) {
        binding.pb.setVisibility(View.GONE);
        Snackbar.make(binding.btnSend, msg, Snackbar.LENGTH_LONG).show();
    }
}