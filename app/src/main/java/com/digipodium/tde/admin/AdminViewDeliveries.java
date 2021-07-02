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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.digipodium.tde.R;
import com.digipodium.tde.databinding.CardDlvCompleteDeliveryAdminBinding;
import com.digipodium.tde.databinding.FragmentAdminViewDeliveriesBinding;
import com.digipodium.tde.models.DeliveryModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;


public class AdminViewDeliveries extends Fragment {


    private FragmentAdminViewDeliveriesBinding bind;
    private FirebaseAuth auth;
    private FirebaseFirestore db;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        return inflater.inflate(R.layout.fragment_admin_view_deliveries, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        bind = FragmentAdminViewDeliveriesBinding.bind(view);
        bind.deliveryRecycler.setLayoutManager(new LinearLayoutManager(getActivity()));
        CollectionReference ref = db.collection("Deliveries");
        List<ModelWrap> deliveries = new ArrayList<>();
        DeliveryAdapter adapter = new DeliveryAdapter(this, R.layout.card_dlv_complete_delivery_admin, deliveries, ref);
        bind.deliveryRecycler.setAdapter(adapter);
        ref.get().addOnSuccessListener(queryDocumentSnapshots -> {
            for (DocumentSnapshot document : queryDocumentSnapshots.getDocuments()) {
                DeliveryModel model = document.toObject(DeliveryModel.class);
                deliveries.add(new ModelWrap(model, document.getId()));
            }
            bind.pbar.setVisibility(View.GONE);
            adapter.notifyDataSetChanged();
        }).addOnFailureListener(e -> {
            Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
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

    private class DeliveryAdapter extends RecyclerView.Adapter<DeliveryAdapter.Holder> {

        private final List<ModelWrap> deliveries;
        private final int layout;
        private final LayoutInflater inflater;
        private final Fragment fragment;
        private final CollectionReference ref;

        public DeliveryAdapter(Fragment fragment, int layout, List<ModelWrap> deliveries, CollectionReference ref) {
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
            ModelWrap model = deliveries.get(position);
            holder.bind(model.getModel());
        }

        @Override
        public int getItemCount() {
            return deliveries.size();
        }

        class Holder extends RecyclerView.ViewHolder {

            CardDlvCompleteDeliveryAdminBinding bind;

            public Holder(@NonNull @NotNull View itemView) {
                super(itemView);
                bind = CardDlvCompleteDeliveryAdminBinding.bind(itemView);
                bind.fabView.setOnClickListener(view -> {
                    ModelWrap wrap = deliveries.get(getAdapterPosition());
                    AdminViewDeliveriesDirections.ActionAdminViewDeliveriesToAdminDeliveryDetail dir = AdminViewDeliveriesDirections.actionAdminViewDeliveriesToAdminDeliveryDetail(wrap.getModel(), wrap.id);
                    NavHostFragment.findNavController(fragment).navigate(dir);
                });
            }

            public void bind(DeliveryModel model) {
                bind.textReportTitle.setText(model.address);
                bind.txtDeliveryStart.setText(model.startLocationAddr);
                bind.txtDeliveryDispatch.setText(model.dispatchLocationAddr);
                bind.textpriceamt.setText(String.valueOf(model.price));
                Bitmap bitmap = stringToBitMap(model.img);
                Glide.with(bind.imgItem).load(bitmap).placeholder(R.drawable.ic_baseline_card_giftcard_24).centerCrop().into(bind.imgItem);
            }
        }
    }

    private class ModelWrap extends DeliveryModel {

        private final DeliveryModel model;
        private final String id;

        public ModelWrap(DeliveryModel model, String id) {
            this.model = model;
            this.id = id;
        }

        public DeliveryModel getModel() {
            return model;
        }

        public String getId() {
            return id;
        }
    }
}