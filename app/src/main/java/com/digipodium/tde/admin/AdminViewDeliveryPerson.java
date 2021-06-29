package com.digipodium.tde.admin;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavDirections;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.digipodium.tde.R;
import com.digipodium.tde.databinding.CardDeliveryPersonBinding;
import com.digipodium.tde.databinding.FragmentAdminViewDeliveryPersonBinding;
import com.digipodium.tde.models.DeliveryPersonModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import static com.digipodium.tde.Auth.ConfirmationFragment.DELIVERY_PERSON;

public class AdminViewDeliveryPerson extends Fragment {

    private FragmentAdminViewDeliveryPersonBinding bind;
    private FirebaseAuth auth;
    private FirebaseFirestore db;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        return inflater.inflate(R.layout.fragment_admin_view_delivery_person, container, false);
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        bind = FragmentAdminViewDeliveryPersonBinding.bind(view);
        bind.deliveryRecycler.setLayoutManager(new LinearLayoutManager(getActivity()));
        CollectionReference ref = db.collection(DELIVERY_PERSON);
        List<DeliveryPersonModel> dpList = new ArrayList<>();
        DeliveryAdapter adapter = new DeliveryAdapter(this, R.layout.card_delivery_person, dpList, ref);
        bind.deliveryRecycler.setAdapter(adapter);
        ref.get().addOnSuccessListener(queryDocumentSnapshots -> {
            for (DocumentSnapshot document : queryDocumentSnapshots.getDocuments()) {
                dpList.add(document.toObject(DeliveryPersonModel.class));
            }
            adapter.notifyDataSetChanged();
        }).addOnFailureListener(e -> {
            Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
        });
    }

    private class DeliveryAdapter extends RecyclerView.Adapter<DeliveryAdapter.Holder> {

        private final List<DeliveryPersonModel> deliveries;
        private final int layout;
        private final LayoutInflater inflater;
        private final Fragment fragment;
        private final CollectionReference ref;

        public DeliveryAdapter(Fragment fragment, int layout, List<DeliveryPersonModel> deliveries, CollectionReference ref) {
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
            DeliveryPersonModel deliveryModel = deliveries.get(position);
            holder.bind(deliveryModel);
        }

        @Override
        public int getItemCount() {
            return deliveries.size();
        }

        class Holder extends RecyclerView.ViewHolder {

            private final @NonNull
            CardDeliveryPersonBinding bind;

            public Holder(@NonNull @NotNull View itemView) {
                super(itemView);
                bind = CardDeliveryPersonBinding.bind(itemView);
                bind.floatingActionButton3.setOnClickListener(view -> {
                    DeliveryPersonModel model = deliveries.get(getAdapterPosition());
                    NavDirections dir = AdminViewDeliveryPersonDirections.actionAdminViewDeliveryPersonToAdminDeliveryPersonDetail(model.uid);
                    NavHostFragment.findNavController(fragment).navigate(dir);
                });
            }

            public void bind(DeliveryPersonModel model) {
                bind.dlvpersonname.setText(model.fullName);
                bind.textApproved.setText(String.valueOf(model.approved));
                bind.textStatus.setText(String.valueOf(model.ondelivery));
                if (model.approved) {
                    bind.card.setCardBackgroundColor(Color.rgb(200, 255, 200));
                } else {
                    bind.card.setCardBackgroundColor(Color.rgb(255, 250, 250));
                }
            }
        }
    }
}