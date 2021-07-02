package com.digipodium.tde.admin;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.digipodium.tde.Constants;
import com.digipodium.tde.R;
import com.digipodium.tde.databinding.CardUserViewBinding;
import com.digipodium.tde.databinding.FragmentAdminViewUsersBinding;
import com.digipodium.tde.models.UserModel;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;


public class AdminViewUsers extends Fragment {


    private FragmentAdminViewUsersBinding bind;
    private UserAdapter adapter;
    private List<UserModel> userList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        userList = new ArrayList<>();
        return inflater.inflate(R.layout.fragment_admin_view_users, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        bind = FragmentAdminViewUsersBinding.bind(view);
        bind.userRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new UserAdapter(this, R.layout.card_user_view, userList);
        bind.userRecycler.setAdapter(adapter);
        loadData();
    }

    private void loadData() {
        FirebaseFirestore.getInstance().collection(Constants.COL_USERS).get().addOnSuccessListener(queryDocumentSnapshots -> {
            for (DocumentSnapshot doc : queryDocumentSnapshots.getDocuments()) {
                UserModel userModel = doc.toObject(UserModel.class);
                userList.add(userModel);
            }
            bind.pbar.setVisibility(View.GONE);
            adapter.notifyDataSetChanged();
        });
    }

    private class UserAdapter extends RecyclerView.Adapter<UserAdapter.Holder> {

        private final List<UserModel> users;
        private final int layout;
        private final LayoutInflater inflater;
        private final Fragment fragment;

        public UserAdapter(Fragment fragment, int layout, List<UserModel> users) {
            this.users = users;
            this.layout = layout;
            inflater = LayoutInflater.from(fragment.getActivity());
            this.fragment = fragment;
        }

        @NonNull
        @NotNull
        @Override
        public Holder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
            return new Holder(inflater.inflate(layout, parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull @NotNull Holder holder, int position) {
            UserModel user = users.get(position);
            holder.bind(user);
        }

        @Override
        public int getItemCount() {
            return users.size();
        }

        class Holder extends RecyclerView.ViewHolder {

            private final @NonNull
            CardUserViewBinding bind;

            public Holder(@NonNull @NotNull View itemView) {
                super(itemView);
                bind = CardUserViewBinding.bind(itemView);
                bind.view.setOnClickListener(view -> {
                    UserModel model = users.get(getAdapterPosition());
                    AdminViewUsersDirections.ActionAaminViewUsersToAdminUserDetailFragment dir = AdminViewUsersDirections.actionAaminViewUsersToAdminUserDetailFragment(model);
                    NavHostFragment.findNavController(fragment).navigate(dir);
                });
            }

            public void bind(UserModel model) {
                bind.username.setText(model.fullName);
                bind.address.setText(model.address);
                bind.city.setText(model.city);
                bind.phone.setText(model.email);
            }
        }
    }
}