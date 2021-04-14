package com.digipodium.tde;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.digipodium.tde.databinding.FragmentAdminViewReportsBinding;
import com.digipodium.tde.models.Report;

import org.jetbrains.annotations.NotNull;
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.LinkedList;

public class AdminViewReports extends Fragment {

    private ArrayList<Report> mDataList;
    private FragmentAdminViewReportsBinding bind;

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
        ItemAdapter adapter = new ItemAdapter(getActivity(), mDataList);
        bind.recyclerView.setAdapter(adapter);
        initializeData();
    }

    private void initializeData() {

    }

    public class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.ItemHolder> {

        private final ArrayList<Report> mDatalist;
        private LayoutInflater inflater;

        public ItemAdapter(Context c, ArrayList<Report> mDatalist) {
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
            Report report = mDatalist.get(position);
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
                textReportTitle = iv.findViewById(R.id.textReportTitle);

                this.adapter = adapter;
                itemView.setOnClickListener(view -> {
                    Report report = mDatalist.get(getAdapterPosition());
                    AdminViewReportsDirections.ActionAdminViewReportsToAdminReportDetailFragment directions = AdminViewReportsDirections.actionAdminViewReportsToAdminReportDetailFragment(report.title, report.detail);
                    NavHostFragment.findNavController(AdminViewReports.this).navigate(directions);
                });
            }

            void bindTo(Report report) {
                textReportTitle.setText(report.title);
            }
        }
    }

}