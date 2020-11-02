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
import com.digipodium.tde.databinding.FragmentEndUserRegisterBinding;
import com.digipodium.tde.models.UserModel;
import com.digipodium.tde.utils.AadharCardValidation;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.auth.User;


public class EndUserRegisterFragment extends Fragment {


    private FirebaseAuth auth;
    private com.digipodium.tde.databinding.FragmentEndUserRegisterBinding binding;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_end_user_register, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        auth = FirebaseAuth.getInstance();
        binding = FragmentEndUserRegisterBinding.bind(view);
        binding.floatingActionButton.setOnClickListener(view1 -> {
            String fullName = binding.editUserFullName.getText().toString();
            String email = binding.editUserEmail.getText().toString();
            String password = binding.editUserPassword.getText().toString();
            String cPassword = binding.editUserConfirmPassword.getText().toString();
            String city = binding.editUserCity.getText().toString();
            String phone = binding.editUserPhone.getText().toString();
            String address = binding.editUserAddress.getText().toString();
            String aadhar = binding.editAadhar.getText().toString();
            if (fullName.trim().length() >= 3 && fullName.trim().length() <= 50) {
                if (email.trim().length() >= 11) {
                    if (aadhar.length() == 12 && AadharCardValidation.validateVerhoeff(aadhar)) {
                        if (password.length() >= 8) {
                            if (cPassword.equals(password)) {
                                binding.progressBar.setVisibility(View.VISIBLE);
                                auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
                                    if (task.isSuccessful()) {
                                        String uid = task.getResult().getUser().getUid();
                                        UserModel user = new UserModel(fullName, email, aadhar, phone, city, address, uid);
                                        addProfileInfo(user, task.getResult().getUser());
                                    } else {
                                        Snackbar.make(binding.floatingActionButton, task.getException().getMessage(), Snackbar.LENGTH_LONG).show();
                                        updateUI(null);
                                    }
                                });
                            } else {
                                binding.editUserConfirmPassword.setError("Passwords do not match");
                                binding.editUserConfirmPassword.requestFocus();
                            }
                        } else {
                            binding.editUserPassword.setError("Password is too small, read the instructions");
                            binding.editUserPassword.requestFocus();
                        }

                    } else {
                        binding.editAadhar.setError("invalid Aadhar");
                        binding.editAadhar.requestFocus();
                    }
                } else {
                    binding.editUserEmail.setError("invalid Email");
                    binding.editUserEmail.requestFocus();
                }
            } else {
                binding.editUserFullName.setError("length must be between 3 - 50 chars");
                binding.editUserFullName.requestFocus();
            }
        });
    }

    private void updateUI(FirebaseUser user) {
        binding.progressBar.setVisibility(View.GONE);
        if (user != null) {
            auth.signOut();
            NavHostFragment.findNavController(this)
                    .navigate(R.id.action_endUserRegisterFragment_to_endUserLoginFragment);
        }
    }

    private void addProfileInfo(UserModel user, FirebaseUser userAcc) {
        CollectionReference users = FirebaseFirestore.getInstance().collection("Users");
        users.add(user).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                updateUI(userAcc);
            } else {
                String errorMsg = task.getException().getMessage();
                updateUI(null);
            }
        });
    }
}