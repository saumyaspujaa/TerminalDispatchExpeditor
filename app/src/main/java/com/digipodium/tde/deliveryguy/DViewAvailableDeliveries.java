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
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.digipodium.tde.R;
import com.digipodium.tde.databinding.CardNewDeliveryBinding;
import com.digipodium.tde.databinding.FragmentDViewAvailableDeliveriesBinding;
import com.digipodium.tde.models.DeliveryModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;


public class DViewAvailableDeliveries extends Fragment {

    private FragmentDViewAvailableDeliveriesBinding bind;
    private FirebaseFirestore db;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        CollectionReference dlvPersonDb = db.collection("DeliveryPerson");
        return inflater.inflate(R.layout.fragment_d_view_available_deliveries, container, false);
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        bind = FragmentDViewAvailableDeliveriesBinding.bind(view);
        bind.recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        CollectionReference ref = db.collection("Deliveries");
        List<DeliveryModel> deliveries = new ArrayList<>();
        DeliveryAdapter adapter = new DeliveryAdapter(this, R.layout.card_new_delivery, deliveries, ref);
        bind.recyclerView.setAdapter(adapter);
        ref.whereEqualTo("status", "created").get().addOnSuccessListener(queryDocumentSnapshots -> {
            for (DocumentSnapshot document : queryDocumentSnapshots.getDocuments()) {
                DeliveryModel model = document.toObject(DeliveryModel.class);
                deliveries.add(model);
            }
            bind.progressBar2.setVisibility(View.GONE);
            adapter.notifyDataSetChanged();
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

            private final @NonNull
            CardNewDeliveryBinding bind;

            public Holder(@NonNull @NotNull View itemView) {
                super(itemView);
                bind = CardNewDeliveryBinding.bind(itemView);
                bind.viewDelivery.setOnClickListener(view -> {
                    bind.viewDelivery.setEnabled(false);
                    DeliveryModel model = deliveries.get(getAdapterPosition());
                    ref.whereEqualTo("dispatchLoc", model.dispatchLoc)
                            .whereEqualTo("startLoc", model.startLoc)
                            .whereEqualTo("deliveryDetails", model.deliveryDetails)
                            .get()
                            .addOnSuccessListener(queryDocumentSnapshots -> {
                                String deliveryId = queryDocumentSnapshots.getDocuments().get(0).getId();
                                NavHostFragment.findNavController(fragment).navigate(DViewAvailableDeliveriesDirections.actionDViewAvailableDeliveriesToDeliverDetailFragment(deliveryId));
                            });
                });
            }

            public void bind(DeliveryModel model) {
                bind.textPrice.setText(String.valueOf(model.price));
                Bitmap bitmap = stringToBitMap(model.img);
                Glide.with(bind.imgItem).load(bitmap).into(bind.imgItem);
                bind.txtstatue.setText(model.startLocationAddr);
                bind.txtapp.setText(model.dispatchLocationAddr);
                bind.dlvpersonname.setText(String.format("%s delivery request", model.fullName));
            }
        }
    }
}