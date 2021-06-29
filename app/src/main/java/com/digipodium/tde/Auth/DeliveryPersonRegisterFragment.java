package com.digipodium.tde.Auth;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.digipodium.tde.R;
import com.digipodium.tde.databinding.FragmentDeliveryPersonRegisterBinding;
import com.digipodium.tde.models.DeliveryPersonModel;
import com.digipodium.tde.models.UserModel;
import com.digipodium.tde.utils.AadharCardValidation;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import org.jetbrains.annotations.NotNull;


public class DeliveryPersonRegisterFragment extends Fragment {

    private FirebaseAuth auth;
    private FirebaseFirestore db;
    private com.digipodium.tde.databinding.FragmentDeliveryPersonRegisterBinding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        return inflater.inflate(R.layout.fragment_delivery_person_register, container, false);
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding = FragmentDeliveryPersonRegisterBinding.bind(view);
        binding.floatingActionButton.setOnClickListener(v -> {
            String fullName = binding.editDlvFullName.getText().toString();
            String phone = binding.editDlvPhone.getText().toString();
            String email = binding.editDlvEmail.getText().toString();
            String city = binding.editDlvCity.getText().toString();
            String aadhar = binding.editAadhar.getText().toString();
            String address = binding.editDlvAddress.getText().toString();
            String age = binding.editDlvAge.getText().toString();
            String password = binding.editDlvPassword.getText().toString();
            String cpassword = binding.editDlvConfrirmPassword.getText().toString();
            String transport = binding.editDlvTransport.getText().toString();
            if (fullName.trim().length() >= 3 && fullName.trim().length() <= 50) {
                if (email.trim().length() >= 11) {
                    if (aadhar.length() == 12 && AadharCardValidation.validateVerhoeff(aadhar)) {
                        if (password.length() >= 8) {
                            if (cpassword.equals(password)) {
                                binding.pbar.setVisibility(View.VISIBLE);
                                auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
                                    if (task.isSuccessful()) {
                                        String uid = task.getResult().getUser().getUid();
                                        DeliveryPersonModel user = new DeliveryPersonModel(fullName, phone, email, city, aadhar, address, age, transport, uid, password, false, false);
                                        addProfileInfo(user, task.getResult().getUser());
                                    } else {
                                        Snackbar.make(binding.floatingActionButton, task.getException().getMessage(), Snackbar.LENGTH_LONG).show();
                                        updateUI(null);
                                    }
                                });
                            } else {
                                binding.editDlvConfrirmPassword.setError("Passwords do not match");
                                binding.editDlvConfrirmPassword.requestFocus();
                            }
                        } else {
                            binding.editDlvPassword.setError("Password is too small, read the instructions");
                            binding.editDlvPassword.requestFocus();
                        }

                    } else {
                        binding.editAadhar.setError("invalid Aadhar, please check length");
                        binding.editAadhar.requestFocus();
                    }
                } else {
                    binding.editDlvEmail.setError("invalid Email");
                    binding.editDlvEmail.requestFocus();
                }
            } else {
                binding.editDlvFullName.setError("length must be between 3 - 50 chars");
                binding.editDlvFullName.requestFocus();
            }
        });
    }

    private void addProfileInfo(DeliveryPersonModel user, FirebaseUser userAcc) {
        CollectionReference users = FirebaseFirestore.getInstance().collection("DeliveryPerson");
        users.document(userAcc.getUid()).set(user).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                updateUI(userAcc);
            } else {
                String errorMsg = task.getException().getMessage();
                updateUI(null);
            }
        });
    }

    private void updateUI(FirebaseUser user) {
        binding.pbar.setVisibility(View.GONE);
        if (user != null) {
            auth.signOut();
            NavHostFragment.findNavController(this).navigate(R.id.action_deliveryPersonRegisterFragment_to_deliveryPersonLoginFragment);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        updateUI(auth.getCurrentUser());
    }
}