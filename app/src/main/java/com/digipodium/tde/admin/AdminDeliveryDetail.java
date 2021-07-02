package com.digipodium.tde.admin;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.bumptech.glide.Glide;
import com.digipodium.tde.Constants;
import com.digipodium.tde.R;
import com.digipodium.tde.databinding.FragmentAdminDeliveryDetailBinding;
import com.digipodium.tde.models.DeliveryModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import org.jetbrains.annotations.NotNull;

public class AdminDeliveryDetail extends Fragment {

    private FirebaseAuth auth;
    private FirebaseFirestore db;
    private com.digipodium.tde.databinding.FragmentAdminDeliveryDetailBinding bind;
    private @NonNull
    String deliveryId;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        return inflater.inflate(R.layout.fragment_admin_delivery_detail, container, false);
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        bind = FragmentAdminDeliveryDetailBinding.bind(view);
        AdminDeliveryDetailArgs args = AdminDeliveryDetailArgs.fromBundle(getArguments());
        DeliveryModel model = args.getModel();
        deliveryId = args.getDeliveryId();
        bind.textAddr.setText(model.address);
        bind.textDetails.setText(model.deliveryDetails);
        bind.textDispatch.setText(model.dispatchLocationAddr);
        bind.textDispatch.setTag(model.dispatchLoc);
        bind.textPickup.setText(model.startLocationAddr);
        bind.textPickup.setTag(model.startLoc);
        bind.textEmail.setText(model.email);
        bind.textName.setText(model.fullName);
        bind.textStatus.setText(model.status);
        Bitmap bitmap = stringToBitMap(model.img);
        Glide.with(view).load(bitmap).into(bind.img);
        try {
            bind.textDate.setText(String.valueOf(model.timestamp.toString()));
        } catch (Exception e) {
            bind.textDate.setText("");
        }
        bind.fabComplete.setOnClickListener(view1 -> {
            db.collection(Constants.COL_DELIVERY).document(deliveryId).delete().addOnSuccessListener(documentSnapshot -> {
                NavHostFragment.findNavController(this).navigate(R.id.action_adminDeliveryDetail_to_adminViewDeliveries);
            }).addOnFailureListener(this::showError);
        });
    }

    private void showError(Exception e) {
        Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
    }

    public Bitmap stringToBitMap(String encodedString) {
        try {
            byte[] encodeByte = Base64.decode(encodedString, Base64.DEFAULT);
            return BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
        } catch (Exception e) {
            e.getMessage();
            return null;
        }
    }
}