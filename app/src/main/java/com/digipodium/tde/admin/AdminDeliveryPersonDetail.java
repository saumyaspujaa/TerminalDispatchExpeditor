package com.digipodium.tde.admin;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.digipodium.tde.Constants;
import com.digipodium.tde.R;
import com.digipodium.tde.databinding.FragmentAdminDeliveryPersonDetailBinding;
import com.digipodium.tde.models.DeliveryPersonModel;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import org.jetbrains.annotations.NotNull;

import pub.devrel.easypermissions.EasyPermissions;


public class AdminDeliveryPersonDetail extends Fragment {

    private FirebaseAuth auth;
    private FirebaseFirestore db;
    private FragmentAdminDeliveryPersonDetailBinding binding;
    private @NonNull
    String deliveryPersonId;
    private DeliveryPersonModel person;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        return inflater.inflate(R.layout.fragment_admin_delivery_person_detail, container, false);
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding = FragmentAdminDeliveryPersonDetailBinding.bind(view);
        deliveryPersonId = AdminDeliveryPersonDetailArgs.fromBundle(getArguments()).getUid();
        db.collection(Constants.COL_DLV_PERSON).document(deliveryPersonId).get().addOnSuccessListener(documentSnapshot -> {
            person = documentSnapshot.toObject(DeliveryPersonModel.class);
            binding.address.setText(person.address);
            binding.textAadhar.setText(person.aadhar);
            binding.textCity.setText(person.city);
            binding.textEmail.setText(person.email);
            binding.textDlv.setText(person.fullName);
            binding.textPhone.setText(person.phone);
            binding.textUId.setText(deliveryPersonId);
            binding.textApproved.setText(String.format("Approved %s", String.valueOf(person.approved)));
            binding.textOnDelivery.setText(String.format("On Delivery %s", String.valueOf(person.ondelivery)));
            binding.transport.setText(person.transport);
            binding.fabApprove.setOnClickListener(view1 -> {
                db.collection(Constants.COL_DLV_PERSON).document(deliveryPersonId).update("approved", true).addOnSuccessListener(unused -> {
                    Snackbar.make(binding.getRoot(), "successfully activated account", BaseTransientBottomBar.LENGTH_LONG).show();
                    String msg = "Your account has been activated, you can now access the dashboard and make deliveries";
                    commnicateTask(msg, msg);
                });
            });
            binding.fabRemove.setOnClickListener(view1 -> {
                db.collection(Constants.COL_DLV_PERSON).document(deliveryPersonId).update("approved", false).addOnSuccessListener(unused -> {
                    Snackbar.make(binding.getRoot(), "successfully deactivated account", BaseTransientBottomBar.LENGTH_LONG).show();
                });
            });
        });
    }

    private void commnicateTask(String notificationMsg, String emailMsg) {
        Toast.makeText(getActivity(), "Notification sent", Toast.LENGTH_SHORT).show();

        try {
            sendSMSMessage(person.phone, notificationMsg);
            new AlertDialog.Builder(getActivity()).setMessage("do  you want to send email also").setTitle("send Email?").setPositiveButton("Yes", (dialogInterface1, i1) -> {
                composeEmail(new String[]{person.email}, "Your account has been activated " + person.fullName, emailMsg);
            }).setNegativeButton("no", null).create().show();
        } catch (Exception e) {
            Toast.makeText(getActivity(), "could not send notification due to " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    public void composeEmail(String[] addresses, String subject, String text) {
        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.putExtra(Intent.EXTRA_EMAIL, addresses);
        intent.putExtra(Intent.EXTRA_SUBJECT, subject);
        intent.putExtra(Intent.EXTRA_TEXT, text);
        if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
            startActivity(intent);
        }
    }

    protected void sendSMSMessage(String phoneNo, String message) {
        if (EasyPermissions.hasPermissions(getActivity(), Manifest.permission.SEND_SMS)) {
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(phoneNo, null, message, null, null);
        }
    }
}