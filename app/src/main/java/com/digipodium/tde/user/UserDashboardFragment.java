package com.digipodium.tde.user;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.digipodium.tde.R;
import com.digipodium.tde.databinding.FragmentUserDashboardBinding;
import com.google.firebase.auth.FirebaseAuth;


public class UserDashboardFragment extends Fragment {


    private com.digipodium.tde.databinding.FragmentUserDashboardBinding binding;
    private FirebaseAuth auth;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_user_dashboard, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding = FragmentUserDashboardBinding.bind(view);
        auth = FirebaseAuth.getInstance();
        binding.textUserLogout.setOnClickListener(v1 -> {
            auth.signOut();
            NavHostFragment.findNavController(this)
                    .navigate(R.id.action_userDashboardFragment_to_userChoiceFragment2);
        });
        binding.resetUserPassword.setOnClickListener(v2 -> {
            NavHostFragment.findNavController(this)
                    .navigate(R.id.action_userDashboardFragment_to_resetPasswordFragment);
        });
        binding.newRequest.setOnClickListener(view1 -> {
            NavHostFragment.findNavController(this).navigate(R.id.action_userDashboardFragment_to_createNewDeliveryRequest);
        });
        binding.editUserProfile.setOnClickListener(view1 -> {
            NavHostFragment.findNavController(this).navigate(R.id.action_userDashboardFragment_to_userEditProfile);
        });
        binding.currentDeliveryReq.setOnClickListener(view1 -> {
            NavHostFragment.findNavController(this).navigate(R.id.action_userDashboardFragment_to_userCurrentDeliveryRequest);
        });
        binding.viewHistory.setOnClickListener(view1 -> {
            NavHostFragment.findNavController(this).navigate(R.id.action_userDashboardFragment_to_userViewHistory);
        });
        binding.report.setOnClickListener(view1 -> {
            NavHostFragment.findNavController(this).navigate(R.id.action_userDashboardFragment_to_reportFeeback);
        });

    }
}