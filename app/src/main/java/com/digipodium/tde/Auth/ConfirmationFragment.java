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
import com.digipodium.tde.databinding.FragmentConfirmationBinding;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.jetbrains.annotations.NotNull;


public class ConfirmationFragment extends Fragment {


    public static final String DELIVERY_PERSON = "DeliveryPerson";
    private FirebaseFirestore db;
    private FirebaseAuth auth;
    private Boolean approved;
    private com.digipodium.tde.databinding.FragmentConfirmationBinding bind;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        return inflater.inflate(R.layout.fragment_confirmation, container, false);
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        bind = FragmentConfirmationBinding.bind(view);
        bind.btnExit.setOnClickListener(view1 -> getActivity().finish());
        Task<DocumentSnapshot> deliveryPerson = db.collection(DELIVERY_PERSON).document(auth.getCurrentUser().getUid()).get();
        deliveryPerson.addOnSuccessListener(documentSnapshot -> {
            approved = documentSnapshot.getBoolean("approved");
            if (approved) {
                NavHostFragment.findNavController(this).navigate(R.id.action_confirmationFragment_to_deliveryPersonDashboardFragment);
            } else {
                Snackbar.make(view, "You are not approved yet", BaseTransientBottomBar.LENGTH_INDEFINITE).show();
            }
        }).addOnFailureListener(e -> {
            Snackbar.make(view, e.getMessage(), BaseTransientBottomBar.LENGTH_INDEFINITE).show();
        });

    }
}