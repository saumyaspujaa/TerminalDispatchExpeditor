package com.digipodium.tde;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.digipodium.tde.databinding.FragmentAdminDashboardBinding;
import com.google.firebase.auth.FirebaseAuth;


public class AdminDashboardFragment extends Fragment {


    private com.digipodium.tde.databinding.FragmentAdminDashboardBinding binding;
    private FirebaseAuth auth;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_admin_dashboard, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding = FragmentAdminDashboardBinding.bind(view);
        auth = FirebaseAuth.getInstance();
        binding.textAdmLogout.setOnClickListener(v1 -> {
            auth.signOut();
            NavHostFragment.findNavController(this)
                    .navigate(R.id.action_adminDashboardFragment_to_userChoiceFragment2);
        });

    }
}