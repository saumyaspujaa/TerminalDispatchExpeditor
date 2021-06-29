package com.digipodium.tde.deliveryguy;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.digipodium.tde.R;
import com.digipodium.tde.databinding.FragmentDeliveryPersonDashboardBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;


public class DeliveryPersonDashboardFragment extends Fragment {


    private com.digipodium.tde.databinding.FragmentDeliveryPersonDashboardBinding binding;
    private FirebaseAuth auth;
    private FirebaseFirestore db;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        return inflater.inflate(R.layout.fragment_delivery_person_dashboard, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding = FragmentDeliveryPersonDashboardBinding.bind(view);

        binding.textDlvLogout.setOnClickListener(v1 -> {
            auth.signOut();
            NavHostFragment.findNavController(this)
                    .navigate(R.id.action_deliveryPersonDashboardFragment_to_userChoiceFragment2);
        });
        binding.resetPassword.setOnClickListener(v2 -> {
            NavHostFragment.findNavController(this)
                    .navigate(R.id.action_deliveryPersonDashboardFragment_to_resetPasswordFragment);
        });
    }

}