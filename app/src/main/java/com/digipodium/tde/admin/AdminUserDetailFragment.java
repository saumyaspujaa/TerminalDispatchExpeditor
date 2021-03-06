package com.digipodium.tde.admin;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.digipodium.tde.R;
import com.digipodium.tde.databinding.FragmentAdminUserDetailBinding;
import com.digipodium.tde.models.UserModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import org.jetbrains.annotations.NotNull;


public class AdminUserDetailFragment extends Fragment {

    private FirebaseAuth auth;
    private FirebaseFirestore db;
    private com.digipodium.tde.databinding.FragmentAdminUserDetailBinding binding;
    private @NonNull
    UserModel user;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        return inflater.inflate(R.layout.fragment_admin_user_detail, container, false);
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding = FragmentAdminUserDetailBinding.bind(view);
        user = AdminUserDetailFragmentArgs.fromBundle(getArguments()).getUser();
        binding.address.setText(user.address);
        binding.textAadhar.setText(user.aadhar);
        binding.textCity.setText(user.city);
        binding.textEmail.setText(user.email);
        binding.textName.setText(user.fullName);
        binding.textPhone.setText(user.phone);
        binding.textUId.setText(user.id);

    }
}