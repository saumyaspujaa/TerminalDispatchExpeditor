package com.digipodium.tde.deliveryguy;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.digipodium.tde.R;
import com.digipodium.tde.databinding.FragmentDEditProfileBinding;
import com.digipodium.tde.models.UserModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import org.jetbrains.annotations.NotNull;

public class DEditProfile extends Fragment {

    private com.digipodium.tde.databinding.
            FragmentDEditProfileBinding bind;
    private FirebaseAuth auth;
    private FirebaseFirestore db;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        return inflater.inflate(R.layout.fragment_d_edit_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        bind = FragmentDEditProfileBinding.bind(view);
        String uid = auth.getCurrentUser().getUid();
        DocumentReference ref = db.collection("Users").document(uid);
        Toast.makeText(getContext(), uid, Toast.LENGTH_SHORT).show();
        ref.get().addOnSuccessListener(documentSnapshot -> {
            UserModel userModel = documentSnapshot.toObject(UserModel.class);
            bind.editCity.setText(userModel.city);
            bind.editAadhar.setText(userModel.aadhar);
            bind.editAddr.setText(userModel.address);
            bind.editEmail.setText(userModel.email);
            bind.editFullname.setText(userModel.fullName);
            bind.editPhone.setText(userModel.phone);
            bind.editEmail.setEnabled(false);
            bind.editAadhar.setEnabled(false);
        });
        bind.fab.setOnClickListener(view1 -> {
            String phone = bind.editPhone.getText().toString();
            String city = bind.editCity.getText().toString();
            String addr = bind.editAddr.getText().toString();
            String name = bind.editFullname.getText().toString();
            String aadhar = bind.editAadhar.getText().toString();
            bind.fab.setVisibility(View.GONE);
            bind.status.setVisibility(View.VISIBLE);
            ref.set(new UserModel(name, bind.editEmail.getText().toString(), aadhar, phone, city, addr, uid))
                    .addOnSuccessListener(unused -> {
                        bind.status.setVisibility(View.GONE);
                        bind.fab.setVisibility(View.VISIBLE);
                        Toast.makeText(getContext(), "Success", Toast.LENGTH_SHORT).show();
                    });
        });
    }
}