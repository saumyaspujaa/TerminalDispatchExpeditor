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
import com.digipodium.tde.databinding.FragmentDeliverDetailBinding;
import com.digipodium.tde.models.ActiveDeliveryModel;
import com.digipodium.tde.models.DeliveryModel;
import com.digipodium.tde.models.DeliveryPersonModel;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

import pub.devrel.easypermissions.EasyPermissions;


public class DeliverDetailFragment extends Fragment {

    String deliveryId;
    private FirebaseAuth auth;
    private CollectionReference deliveryDb;
    private DeliveryModel model;
    private com.digipodium.tde.databinding.FragmentDeliverDetailBinding bind;
    private CollectionReference activeDeliverDb;
    private CollectionReference dlvPersonDb;
    private DeliveryPersonModel personModel;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        auth = FirebaseAuth.getInstance();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        deliveryDb = db.collection("Deliveries");
        activeDeliverDb = db.collection("CurrentDeliveries");
        dlvPersonDb = db.collection("DeliveryPerson");
        return inflater.inflate(R.layout.fragment_deliver_detail, container, false);
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        deliveryId = DeliverDetailFragmentArgs.fromBundle(getArguments()).getDeliveryId();
        Task<DocumentSnapshot> task = deliveryDb.document(deliveryId).get();
        task.addOnSuccessListener(documentSnapshot -> {
            model = documentSnapshot.toObject(DeliveryModel.class);
            bind = FragmentDeliverDetailBinding.bind(view);
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
            bind.textPhone.setText(model.phone);
            Bitmap bitmap = stringToBitMap(model.img);
            Glide.with(view).load(bitmap).into(bind.img);
            try {
                bind.textDate.setText(model.timestamp.toString());
            } catch (Exception e) {
                bind.textDate.setText("");
            }
            DocumentReference dlvPerson = dlvPersonDb.document(auth.getCurrentUser().getUid());
            dlvPerson.get().addOnSuccessListener(documentSnapshot1 -> {
                personModel = documentSnapshot1.toObject(DeliveryPersonModel.class);
            });
            bind.fabComplete.setOnClickListener(view1 -> {
                AlertDialog confirmation = new AlertDialog.Builder(getActivity())
                        .setTitle("confirmation")
                        .setPositiveButton("accept", (dialogInterface, i) -> deliveryDb.document(deliveryId).update("status", "selected").addOnSuccessListener(unused -> {
                            ActiveDeliveryModel data = new ActiveDeliveryModel(Objects.requireNonNull(auth.getCurrentUser()).getUid(), deliveryId, System.currentTimeMillis());
                            activeDeliverDb.document(deliveryId).set(data).addOnSuccessListener(unused1 -> {

                                dlvPerson.update("ondelivery", true).addOnSuccessListener(unused2 -> {
                                    String msg = "Your delivery has been selected, by: " + personModel.fullName + "\ncontact:" + personModel.phone + " & will contact soon";
                                    commnicateTask(msg, msg);
                                    AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity())
                                            .setTitle("Job Activated")
                                            .setPositiveButton("view navigation map", (dialogInterface1, i1) -> NavHostFragment.findNavController(this).navigate(DeliverDetailFragmentDirections.actionDeliverDetailFragmentToDNavigationMap(deliveryId)))
                                            .setMessage("Click ok to open navigation view");
                                    dialog.create().show();
                                });
                            });
                        }))
                        .setNegativeButton("cancel", null)
                        .setMessage(String.format("You agree to deliver the content from \n\n%s to \n\n%s location. \nUntil this job is completed you cannot take up other job", model.startLocationAddr, model.dispatchLocationAddr)).create();
                confirmation.show();
            });

        });
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

    private void commnicateTask(String notificationMsg, String emailMsg) {
        Toast.makeText(getActivity(), "Transaction started", Toast.LENGTH_SHORT).show();

        try {
            sendSMSMessage(model.phone, notificationMsg);
            new AlertDialog.Builder(getActivity()).setMessage("do  you want to send email receipt also").setTitle("send Email?").setPositiveButton("Yes", (dialogInterface1, i1) -> {
                composeEmail(new String[]{model.email}, "delivery confirmation message for " + deliveryId, emailMsg);
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