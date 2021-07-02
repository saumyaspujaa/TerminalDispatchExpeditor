package com.digipodium.tde.admin;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.digipodium.tde.Constants;
import com.digipodium.tde.R;
import com.digipodium.tde.databinding.FragmentAdminReportDetailBinding;
import com.digipodium.tde.models.UserModel;
import com.google.firebase.firestore.FirebaseFirestore;


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
        FirebaseFirestore.getInstance().collection(Constants.COL_USERS).document(data.getUid()).get().addOnSuccessListener(documentSnapshot -> {
            UserModel userModel = documentSnapshot.toObject(UserModel.class);
            bind.btn.setOnClickListener(view1 -> {
                composeEmail(new String[]{userModel.email}, "In response - " + data.getTitle());
            });
            bind.btn.setOnClickListener(view1 -> {
                dialPhoneNumber(userModel.phone);
            });
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

    public void dialPhoneNumber(String phoneNumber) {
        Intent intent = new Intent(Intent.ACTION_DIAL);
        intent.setData(Uri.parse("tel:" + phoneNumber));
        if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
            startActivity(intent);
        }
    }
}