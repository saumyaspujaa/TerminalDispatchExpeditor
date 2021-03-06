package com.digipodium.tde.deliveryguy;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.telephony.SmsManager;
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
import com.digipodium.tde.R;
import com.digipodium.tde.databinding.FragmentDCurrentDeliveryBinding;
import com.digipodium.tde.models.DeliveryModel;
import com.digipodium.tde.models.PaymentModel;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.jetbrains.annotations.NotNull;

import pub.devrel.easypermissions.EasyPermissions;

public class DCurrentDelivery extends Fragment {


    private FirebaseAuth auth;
    private com.digipodium.tde.databinding.FragmentDCurrentDeliveryBinding bind;
    private CollectionReference deliveryDb;
    private CollectionReference activeDeliverDb;
    private DeliveryModel model;
    private CollectionReference paymentDb;
    private CollectionReference dlvPersonDb;
    private String uid;
    private String deliveryId;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        auth = FirebaseAuth.getInstance();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        deliveryDb = db.collection("Deliveries");
        activeDeliverDb = db.collection("CurrentDeliveries");
        paymentDb = db.collection("Payments");
        dlvPersonDb = db.collection("DeliveryPerson");
        return inflater.inflate(R.layout.fragment_d_current_delivery, container, false);
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        bind = FragmentDCurrentDeliveryBinding.bind(view);
        deliveryId = DCurrentDeliveryArgs.fromBundle(getArguments()).getDeliveryId();
        Task<DocumentSnapshot> task = deliveryDb.document(deliveryId).get();
        task.addOnSuccessListener(documentSnapshot -> {
            model = documentSnapshot.toObject(DeliveryModel.class);
            bind = FragmentDCurrentDeliveryBinding.bind(view);
            bind.textAddr.setText(model.address);
            bind.textDetails.setText(model.deliveryDetails);
            bind.textDispatch.setText(model.dispatchLocationAddr);
            bind.textDispatch.setTag(model.dispatchLoc);
            bind.textPickup.setText(model.startLocationAddr);
            bind.textPickup.setTag(model.startLoc);
            bind.textEmail.setText(model.email);
            bind.textName.setText(model.fullName);
            bind.textStatus.setText(model.status);
            bind.textPrice.setText(String.valueOf(model.price));
            bind.textPrice.setText(model.phone);
            Bitmap bitmap = stringToBitMap(model.img);
            Glide.with(view).load(bitmap).into(bind.img);
            try {
                bind.textDate.setText(model.timestamp.toString());
            } catch (Exception e) {
                bind.textDate.setText("");
            }
            uid = auth.getCurrentUser().getUid();
            bind.fabComplete.setOnClickListener(view1 -> {
                AlertDialog confirmation = new AlertDialog.Builder(getActivity())
                        .setTitle("confirmation")
                        .setPositiveButton("Task complete", (dialogInterface, i) -> deliveryDb.document(deliveryId).update("status", "selected").addOnSuccessListener(unused -> activeDeliverDb.document(deliveryId).delete().addOnSuccessListener(unused1 -> deliveryDb.document(deliveryId).update("status", "complete").addOnSuccessListener(unused2 -> dlvPersonDb.document(uid).update("ondelivery", false).addOnSuccessListener(unused3 -> {
                            PaymentModel pay = new PaymentModel(uid, deliveryId, model.price, false);
                            paymentDb.document(auth.getCurrentUser().getUid()).collection("tasks").add(pay).addOnSuccessListener(unused4 -> {
                                String notificationMsg = "Deliver completed for " + model.fullName + "\n delivery product at " + model.dispatchLocationAddr;
                                String emailMsg = "Deliver completed for " + model.fullName + "\n delivery product from" + model.startLocationAddr + "to" + model.dispatchLocationAddr + "\ndetails:\n" + model.deliveryDetails + ". \nThanks for choosing us\n Admin";
                                commnicateTask(notificationMsg, emailMsg);
                                NavHostFragment.findNavController(this).navigate(R.id.action_DCurrentDelivery_to_deliveryPersonDashboardFragment);
                            });
                        })))))
                        .setNegativeButton("Task cancel", (dialogInterface, i) -> {

                        })
                        .setMessage(String.format("You confirm that you have delivered the content from pickup:\n\n%s \nsuccessfully to destination:\n\n%s", model.startLocationAddr, model.dispatchLocationAddr)).create();
                confirmation.show();
            });
            bind.fabCancel.setOnClickListener(view1 -> {
                AlertDialog confirmation = new AlertDialog.Builder(getActivity())
                        .setTitle("Cancel Task")
                        .setPositiveButton("Cancel task", (dialogInterface, i) -> deliveryDb.document(deliveryId).update("status", "selected").addOnSuccessListener(unused -> activeDeliverDb.document(deliveryId).delete().addOnSuccessListener(unused1 -> deliveryDb.document(deliveryId).update("status", "cancelled").addOnSuccessListener(unused2 -> dlvPersonDb.document(uid).update("ondelivery", false).addOnSuccessListener(unused3 -> {
                            PaymentModel pay = new PaymentModel(uid, deliveryId, -100, false);
                            paymentDb.document(auth.getCurrentUser().getUid()).collection("tasks").add(pay).addOnSuccessListener(unused4 -> {
                                Toast.makeText(getActivity(), "Transaction completed", Toast.LENGTH_SHORT).show();
                                String notificationMsg = "Deliver cancelled for " + model.fullName + "\n delivery product at " + model.dispatchLocationAddr;
                                String emailMsg = "Deliver cancelled for " + model.fullName + "\n delivery product from" + model.startLocationAddr + "to" + model.dispatchLocationAddr + "\ndetails:\n" + model.deliveryDetails + ".\n You will be refunded amount Rs." + model.price + " \nThanks for choosing us\n Admin";
                                commnicateTask(notificationMsg, emailMsg);
                                NavHostFragment.findNavController(this).navigate(R.id.action_DCurrentDelivery_to_deliveryPersonDashboardFragment);
                            });
                        })))))
                        .setNegativeButton("No", (dialogInterface, i) -> {

                        })
                        .setMessage("Do you really want to cancel this task, you will be charged a penalty amount of Rs. 100").create();
                confirmation.show();
            });
            bind.fabNav.setOnClickListener(view1 -> NavHostFragment.findNavController(this).navigate(DCurrentDeliveryDirections.actionDCurrentDeliveryToDNavigationMap(deliveryId)));
        });
    }

    private void commnicateTask(String notificationMsg, String emailMsg) {
        Toast.makeText(getActivity(), "Transaction completed", Toast.LENGTH_SHORT).show();

        try {
            sendSMSMessage(model.phone, notificationMsg);
            new AlertDialog.Builder(getActivity()).setMessage("do  you want to send email receipt also").setTitle("send Email?").setPositiveButton("Yes", (dialogInterface1, i1) -> {
                composeEmail(new String[]{model.email}, "delivery confirmation message for " + deliveryId, emailMsg);
            }).setNegativeButton("no", null).create().show();
        } catch (Exception e) {
            Toast.makeText(getActivity(), "could not send notification due to " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
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