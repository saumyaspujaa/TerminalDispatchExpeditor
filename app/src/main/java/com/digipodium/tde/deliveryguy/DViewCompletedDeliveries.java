package com.digipodium.tde.deliveryguy;

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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.digipodium.tde.R;
import com.digipodium.tde.databinding.CardDlvCompleteDeliveryBinding;
import com.digipodium.tde.databinding.FragmentDViewCompletedDeliveriesBinding;
import com.digipodium.tde.models.DeliveryModel;
import com.digipodium.tde.models.PaymentModel;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class DViewCompletedDeliveries extends Fragment {


    private FirebaseFirestore db;
    private CollectionReference dlvPersonDb;
    private CollectionReference deliveriesDb;
    private CollectionReference activeDeliverDb;
    private CollectionReference paymentDb;
    private com.digipodium.tde.databinding.FragmentDViewCompletedDeliveriesBinding bind;
    private FirebaseAuth auth;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        dlvPersonDb = db.collection("DeliveryPerson");
        deliveriesDb = db.collection("Deliveries");
        activeDeliverDb = db.collection("CurrentDeliveries");
        paymentDb = db.collection("Payments");
        return inflater.inflate(R.layout.fragment_d_view_completed_deliveries, container, false);
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        bind = FragmentDViewCompletedDeliveriesBinding.bind(view);
        bind.recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        List<DeliveryModel> deliveries = new ArrayList<>();
        DeliveryAdapter adapter = new DeliveryAdapter(this, R.layout.card_dlv_complete_delivery, deliveries, deliveriesDb);
        bind.recyclerView.setAdapter(adapter);
        List<String> paymentModelList = new ArrayList<>();
        paymentDb.document(auth.getCurrentUser().getUid()).collection("tasks").get().addOnSuccessListener(queryDocumentSnapshots -> {
            for (DocumentSnapshot document : queryDocumentSnapshots.getDocuments()) {
                PaymentModel paymentModel = document.toObject(PaymentModel.class);
                paymentModelList.add(paymentModel.deliveryId);
            }
        });

        deliveriesDb.whereEqualTo("status", "completed").get().addOnSuccessListener(queryDocumentSnapshots -> {
            for (DocumentSnapshot document : queryDocumentSnapshots.getDocuments()) {
                DeliveryModel model = document.toObject(DeliveryModel.class);
                String id = document.getId();
                if (paymentModelList.contains(id)) {
                    deliveries.add(model);
                }
            }
            if (deliveries.size() > 0) {
                bind.progressBar3.setVisibility(View.GONE);
                adapter.notifyDataSetChanged();
            } else {
                Snackbar.make(bind.getRoot(), "no completed delivery data available", BaseTransientBottomBar.LENGTH_INDEFINITE).show();
            }

        }).addOnFailureListener(e -> {
            Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
        });
    }

    public Bitmap stringToBitMap(String encodedString) {
        try {
            byte[] encodeByte = Base64.decode(encodedString, Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
            return bitmap;
        } catch (Exception e) {
            e.getMessage();
            return null;
        }
    }

    private class DeliveryAdapter extends RecyclerView.Adapter<DeliveryAdapter.Holder> {

        private final List<DeliveryModel> deliveries;
        private final int layout;
        private final LayoutInflater inflater;
        private final Fragment fragment;
        private final CollectionReference ref;

        public DeliveryAdapter(Fragment fragment, int layout, List<DeliveryModel> deliveries, CollectionReference ref) {
            this.deliveries = deliveries;
            this.layout = layout;
            inflater = LayoutInflater.from(fragment.getActivity());
            this.fragment = fragment;
            this.ref = ref;
        }

        @NonNull
        @NotNull
        @Override
        public DeliveryAdapter.Holder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
            return new DeliveryAdapter.Holder(inflater.inflate(layout, parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull @NotNull DeliveryAdapter.Holder holder, int position) {
            DeliveryModel deliveryModel = deliveries.get(position);
            holder.bind(deliveryModel);
        }

        @Override
        public int getItemCount() {
            return deliveries.size();
        }

        class Holder extends RecyclerView.ViewHolder {

            private final CardDlvCompleteDeliveryBinding bind;

            public Holder(@NonNull @NotNull View itemView) {
                super(itemView);
                bind = CardDlvCompleteDeliveryBinding.bind(itemView);
            }

            public void bind(DeliveryModel model) {
                bind.textReportTitle.setText(String.format("%s delivery request", model.fullName));
                Bitmap bitmap = stringToBitMap(model.img);
                Glide.with(bind.imgItem).load(bitmap).into(bind.imgItem);
                bind.textpriceamt.setText(String.valueOf(model.price));
                bind.txtDeliveryStart.setText(model.startLocationAddr);
                bind.txtDeliveryDispatch.setText(model.dispatchLocationAddr);
            }
        }
    }
}