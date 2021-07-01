package com.digipodium.tde.deliveryguy;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.digipodium.tde.R;
import com.digipodium.tde.databinding.FragmentDeliveryPersonDashboardBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Objects;


public class DeliveryPersonDashboardFragment extends Fragment {


    private FirebaseAuth auth;
    private CollectionReference activeDeliverDb;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        auth = FirebaseAuth.getInstance();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        activeDeliverDb = db.collection("CurrentDeliveries");
        return inflater.inflate(R.layout.fragment_delivery_person_dashboard, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        com.digipodium.tde.databinding.FragmentDeliveryPersonDashboardBinding binding = FragmentDeliveryPersonDashboardBinding.bind(view);
        binding.textDlvLogout.setOnClickListener(v1 -> {
            auth.signOut();
            NavHostFragment.findNavController(this)
                    .navigate(R.id.action_deliveryPersonDashboardFragment_to_userChoiceFragment2);
        });
        binding.resetPassword.setOnClickListener(v2 -> NavHostFragment.findNavController(this).navigate(R.id.action_deliveryPersonDashboardFragment_to_resetPasswordFragment));
        binding.currentRequest.setOnClickListener(view1 -> {
            try {
                activeDeliverDb.whereEqualTo("personId", Objects.requireNonNull(auth.getCurrentUser()).getUid()).get().addOnSuccessListener(queryDocumentSnapshots -> {
                    String deliveryId = queryDocumentSnapshots.getDocuments().get(0).getId();
                    NavHostFragment.findNavController(this).navigate(DeliveryPersonDashboardFragmentDirections.actionDeliveryPersonDashboardFragmentToDCurrentDelivery(deliveryId));
                });
            } catch (Exception e) {
                Toast.makeText(getActivity(), "no current delivery data available", Toast.LENGTH_SHORT).show();
            }
        });
        binding.availableRequest.setOnClickListener(view1 -> NavHostFragment.findNavController(this).navigate(R.id.action_deliveryPersonDashboardFragment_to_DViewAvailableDeliveries));
        binding.editProfile.setOnClickListener(view1 -> NavHostFragment.findNavController(this).navigate(R.id.action_deliveryPersonDashboardFragment_to_DEditProfile));
        binding.ViewCompletedRequest.setOnClickListener(view1 -> NavHostFragment.findNavController(this).navigate(R.id.action_deliveryPersonDashboardFragment_to_DViewCompletedDeliveries));
    }

}