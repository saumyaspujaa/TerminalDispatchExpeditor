package com.digipodium.tde.admin;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.digipodium.tde.R;
import com.digipodium.tde.databinding.FragmentAdminViewReportsBinding;
import com.digipodium.tde.models.ReportModel;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

import static com.digipodium.tde.Constants.COL_REPORTS;

public class AdminViewReports extends Fragment {

    private ArrayList<ReportModel> mDataList;
    private FragmentAdminViewReportsBinding bind;
    private ItemAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mDataList = new ArrayList<>();
        return inflater.inflate(R.layout.fragment_admin_view_reports, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        bind = FragmentAdminViewReportsBinding.bind(view);
        bind.recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        adapter = new ItemAdapter(getActivity(), mDataList);
        bind.recyclerView.setAdapter(adapter);
        initializeData();
    }

    private void initializeData() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection(COL_REPORTS).get().addOnSuccessListener(queryDocumentSnapshots -> {
            for (DocumentSnapshot document : queryDocumentSnapshots.getDocuments()) {
                ReportModel report = document.toObject(ReportModel.class);
                mDataList.add(report);
            }
            if (mDataList.size() == 0) {
                Snackbar.make(bind.getRoot(), "No reports found", BaseTransientBottomBar.LENGTH_INDEFINITE).show();
            } else {
                adapter.notifyDataSetChanged();
            }
        });
    }

    public class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.ItemHolder> {

        private final ArrayList<ReportModel> mDatalist;
        private LayoutInflater inflater;

        public ItemAdapter(Context c, ArrayList<ReportModel> mDatalist) {
            this.mDatalist = mDatalist;
            inflater = LayoutInflater.from(c);
        }

        @Override
        public ItemAdapter.@NotNull ItemHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View mItemView = inflater.inflate(R.layout.card_adm_report, parent, false);
            return new ItemHolder(mItemView, this);
        }

        @Override
        public void onBindViewHolder(@NonNull ItemAdapter.ItemHolder holder, int position) {
            ReportModel report = mDatalist.get(position);
            holder.bindTo(report);
        }

        @Override
        public int getItemCount() {
            return mDatalist.size();
        }

        public class ItemHolder extends RecyclerView.ViewHolder {

            TextView textReportTitle;
            ItemAdapter adapter;

            public ItemHolder(@NonNull View iv, ItemAdapter adapter) {
                super(iv);
                textReportTitle = iv.findViewById(R.id.dlvpersonname);

                this.adapter = adapter;
                itemView.setOnClickListener(view -> {
                    ReportModel report = mDatalist.get(getAdapterPosition());
                    AdminViewReportsDirections.ActionAdminViewReportsToAdminReportDetailFragment directions = AdminViewReportsDirections
                            .actionAdminViewReportsToAdminReportDetailFragment(report.subject, report.detail, report.uid);
                    NavHostFragment.findNavController(AdminViewReports.this).navigate(directions);
                });
            }

            void bindTo(ReportModel report) {
                textReportTitle.setText(report.subject);
            }
        }
    }

}