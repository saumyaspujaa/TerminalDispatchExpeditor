package com.digipodium.tde;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.digipodium.tde.databinding.FragmentReportFeebackBinding;
import com.digipodium.tde.models.ReportModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import static com.digipodium.tde.Constants.COL_REPORTS;

public class ReportFeedback extends Fragment {

    private FragmentReportFeebackBinding bind;
    private FirebaseAuth auth;
    private FirebaseFirestore db;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        return inflater.inflate(R.layout.fragment_report_feeback, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        bind = FragmentReportFeebackBinding.bind(view);
        bind.btnSaveReport.setOnClickListener(view1 -> {
            String subject = bind.editReportSubject.getText().toString();
            String detail = bind.editReportDetails.getText().toString() + "\n by " + auth.getCurrentUser().getEmail();
            db.collection(COL_REPORTS).document(auth.getCurrentUser().getUid()).set(new ReportModel(subject, detail, auth.getCurrentUser().getUid()))
                    .addOnSuccessListener(aVoid -> {
                        new AlertDialog.Builder(getActivity())
                                .setTitle("Success")
                                .setMessage("Report/Feedback sent to admin, we will get back soon to you.")
                                .setPositiveButton("OK", (dialogInterface, i) -> {
                                }).create().show();
                    })
                    .addOnFailureListener(e -> {
                        new AlertDialog.Builder(getActivity())
                                .setTitle("Failure")
                                .setMessage("failed to send feedback, service failure, check internet connection")
                                .setPositiveButton("Ok", (dialogInterface, i) -> {
                                }).create().show();
                    });
        });
    }
}