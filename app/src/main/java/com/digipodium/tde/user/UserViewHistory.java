package com.digipodium.tde.user;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.digipodium.tde.R;
import com.digipodium.tde.databinding.CardUserHistoryBinding;
import com.digipodium.tde.databinding.FragmentUserViewHistoryBinding;
import com.digipodium.tde.models.DeliveryModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;


public class UserViewHistory extends Fragment {


    private @NonNull
    FragmentUserViewHistoryBinding bind;
    private FirebaseAuth auth;
    private FirebaseFirestore db;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        return inflater.inflate(R.layout.fragment_user_view_history, container, false);
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        bind = FragmentUserViewHistoryBinding.bind(view);
        bind.recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        CollectionReference ref = db.collection("Deliveries");
        List<DeliveryModel> deliveries = new ArrayList<>();
        DeliveryAdapter adapter = new DeliveryAdapter(this, R.layout.card_user_history, deliveries, ref);
        bind.recyclerView.setAdapter(adapter);
        ref.whereEqualTo("email", auth.getCurrentUser().getEmail())
                .whereEqualTo("status", "complete")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for (DocumentSnapshot document : queryDocumentSnapshots.getDocuments()) {
                        DeliveryModel model = document.toObject(DeliveryModel.class);
                        deliveries.add(model);
                    }
                    adapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                });
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
            CardUserHistoryBinding bind;

            public Holder(@NonNull @NotNull View itemView) {
                super(itemView);
                bind = CardUserHistoryBinding.bind(itemView);
                bind.viewDelivery.setText("Action");
                bind.viewDelivery.setOnClickListener(view -> {
                    DeliveryModel model = deliveries.get(getAdapterPosition());
                    new AlertDialog.Builder(fragment.getContext())
                            .setTitle("What do you want to do")
                            .setAdapter(new ArrayAdapter<String>(fragment.getContext(),
                                            android.R.layout.simple_list_item_1,
                                            new String[]{"Delete", "View Status"}),
                                    (dialogInterface, i) -> {
                                        switch (i) {
                                            case 0:
                                                ref.whereEqualTo("dispatchLoc", model.dispatchLoc).whereEqualTo("startLoc", model.startLoc).get().addOnSuccessListener(queryDocumentSnapshots -> {
                                                    for (DocumentSnapshot document : queryDocumentSnapshots.getDocuments()) {
                                                        ref.document(document.getId()).delete();
                                                    }
                                                });
                                                break;
                                            case 1:
                                                Toast.makeText(fragment.getContext(), model.status, Toast.LENGTH_SHORT).show();
                                                break;
                                        }
                                    })
                            .create()
                            .show();
                });
            }

            public void bind(DeliveryModel model) {
                bind.textReportTitle.setText(model.address);
                bind.txtDeliveryStart.setText(model.startLocationAddr);
                bind.txtDeliveryDispatch.setText(model.dispatchLocationAddr);
            }
        }
    }
}