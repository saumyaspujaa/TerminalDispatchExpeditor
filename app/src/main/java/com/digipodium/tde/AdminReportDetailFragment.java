package com.digipodium.tde;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.digipodium.tde.databinding.FragmentAdminReportDetailBinding;


public class AdminReportDetailFragment extends Fragment {

    private FragmentAdminReportDetailBinding bind;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_admin_report_detail, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        bind = FragmentAdminReportDetailBinding.bind(view);
        AdminReportDetailFragmentArgs data = AdminReportDetailFragmentArgs.fromBundle(getArguments());
        bind.textTitle.setText(data.getTitle());
        bind.textDetails.setText(data.getDetail());
        bind.btn.setOnClickListener(view1 -> {
            composeEmail(new String[]{data.getTitle()}, data.getTitle());
        });
    }

    public void composeEmail(String[] addresses, String subject) {
        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setData(Uri.parse("mailto:")); // only email apps should handle this
        intent.putExtra(Intent.EXTRA_EMAIL, addresses);
        intent.putExtra(Intent.EXTRA_SUBJECT, subject);
        if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
            startActivity(intent);
        }
    }
}