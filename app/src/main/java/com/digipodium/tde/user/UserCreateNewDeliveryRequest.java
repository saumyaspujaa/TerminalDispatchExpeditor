package com.digipodium.tde.user;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.bumptech.glide.Glide;
import com.digipodium.tde.R;
import com.digipodium.tde.databinding.FragmentUserCreateNewDeliveryRequestBinding;
import com.digipodium.tde.models.DeliveryModel;
import com.digipodium.tde.models.UserModel;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.mapbox.api.geocoding.v5.models.CarmenFeature;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.camera.CameraPosition;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.plugins.places.picker.PlacePicker;
import com.mapbox.mapboxsdk.plugins.places.picker.model.PlacePickerOptions;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;

import static com.digipodium.tde.Constants.COL_DELIVERY;
import static com.digipodium.tde.Constants.COL_USERS;


public class UserCreateNewDeliveryRequest extends Fragment {

    static final int REQUEST_IMAGE_GET = 1;
    private static final int REQUEST_START_LOC_CODE = 5678;
    private static final int REQUEST_DISPATCH_LOC_CODE = 5679;
    private FragmentUserCreateNewDeliveryRequestBinding bind;
    private LatLng LUCKNOW_COORDINATES;
    private FirebaseFirestore fs;
    private FirebaseAuth auth;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Mapbox.getInstance(getActivity(), getString(R.string.mapbox_access_token));
        fs = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        return inflater.inflate(R.layout.fragment_user_create_new_delivery_request, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        bind = FragmentUserCreateNewDeliveryRequestBinding.bind(view);
        bind.btnConfirmDeliveryCreation.setOnClickListener(view1 -> {
            String deliveryDetails = bind.deliveryDetail.getText().toString();
            String startLocationAddr = bind.textStartLoc.getText().toString();
            String dispatchLocationAddr = bind.textDispatchLoc.getText().toString();
            Uri fullPhotoUri = (Uri) bind.productImg.getTag();
            bind.pbCreate.setVisibility(View.VISIBLE);
            if (deliveryDetails.length() > 10 && startLocationAddr != null && dispatchLocationAddr != null && fullPhotoUri != null) {
                String imgString = encodeImgToString(fullPhotoUri);
                double[] dispatchCoords = (double[]) bind.textDispatchLoc.getTag();
                double[] startCoords = (double[]) bind.textStartLoc.getTag();
                String uid = auth.getCurrentUser().getUid();
                fs.collection(COL_USERS).document(uid).get().addOnSuccessListener(documentSnapshot -> {
                    UserModel user = documentSnapshot.toObject(UserModel.class);
                    DeliveryModel delivery = new DeliveryModel(
                            user.fullName,
                            user.phone,
                            user.address,
                            user.city,
                            user.email,
                            deliveryDetails,
                            startLocationAddr,
                            dispatchLocationAddr,
                            Arrays.toString(startCoords),
                            Arrays.toString(dispatchCoords),
                            imgString,
                            "created"
                    );
                    fs.collection(COL_DELIVERY).add(delivery).addOnSuccessListener(documentReference -> {
                        bind.pbCreate.setVisibility(View.GONE);
                        startActivityForResult(new Intent(getActivity(), CheckoutActivity.class), 22);
                    }).addOnFailureListener(e -> {
                        bind.pbCreate.setVisibility(View.GONE);
                        new AlertDialog.Builder(getActivity())
                                .setTitle("Failure")
                                .setMessage("failed to create a job, server error, please contact admin")
                                .setPositiveButton("retry from start", (dialogInterface, i) -> {
                                    NavHostFragment.findNavController(this).navigate(R.id.action_createNewDeliveryRequest_to_userDashboardFragment);
                                }).create().show();
                    });

                }).addOnFailureListener(e -> {
                    bind.pbCreate.setVisibility(View.GONE);
                    new AlertDialog.Builder(getActivity())
                            .setTitle("Failure")
                            .setMessage("failed to get user details, Authentication error, please contact Admin")
                            .setPositiveButton("retry from start", (dialogInterface, i) -> {
                                NavHostFragment.findNavController(this).navigate(R.id.action_createNewDeliveryRequest_to_userDashboardFragment);
                            }).create().show();
                });

            }
        });

        bind.btnSelectProductImages.setOnClickListener(view1 -> {
            selectImage();
        });

        bind.selectStartLoc.setOnClickListener(view1 -> {
            goToPickerActivity(REQUEST_START_LOC_CODE);
        });
        bind.selectDestination.setOnClickListener(view1 -> {
            goToPickerActivity(REQUEST_DISPATCH_LOC_CODE);
        });
    }

    //encode image to base64 string
    private String encodeImgToString(Uri photoUri) {
        Bitmap bitmap = null;
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), photoUri);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 80, baos);
            byte[] imageBytes = baos.toByteArray();
            String imageString = Base64.encodeToString(imageBytes, Base64.DEFAULT);
            return imageString;
        } catch (IOException e) {
            e.printStackTrace();
            Snackbar.make(bind.getRoot(), "failed to load img", BaseTransientBottomBar.LENGTH_LONG).show();
        }
        return null;
    }

    //decode base64 string to image
    private Bitmap decodeImgString(String imageString) {
        byte[] imageBytes = Base64.decode(imageString, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
    }

    private void goToPickerActivity(int requestCode) {
        LUCKNOW_COORDINATES = new LatLng(26.8467, 80.9462);
        startActivityForResult(
                new PlacePicker.IntentBuilder()
                        .accessToken(getString(R.string.mapbox_access_token))
                        .placeOptions(PlacePickerOptions.builder()
                                .includeDeviceLocationButton(true)
                                .statingCameraPosition(new CameraPosition.Builder()
                                        .target(LUCKNOW_COORDINATES).zoom(16).build())
                                .build())
                        .build(getActivity()), requestCode);
    }


    public void selectImage() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
            startActivityForResult(intent, REQUEST_IMAGE_GET);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_GET && resultCode == Activity.RESULT_OK) {
            Bitmap thumbnail = data.getParcelableExtra("data");
            Uri fullPhotoUri = data.getData();
            Glide.with(this).load(fullPhotoUri).into(bind.productImg);
            bind.productImg.setTag(fullPhotoUri);
        }
        if (requestCode == REQUEST_START_LOC_CODE && resultCode == Activity.RESULT_OK) {
            CarmenFeature carmenFeature = PlacePicker.getPlace(data);
            if (carmenFeature != null) {
                try {
                    double lat = new JSONObject(carmenFeature.toJson()).getJSONObject("geometry").getJSONArray("coordinates").getDouble(0);
                    double lng = new JSONObject(carmenFeature.toJson()).getJSONObject("geometry").getJSONArray("coordinates").getDouble(1);
                    bind.textStartLoc.setTag(new double[]{lat, lng});
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                bind.textStartLoc.setText(String.format(getString(R.string.selected_start_place_info), carmenFeature.placeName()));
            }
        }
        if (requestCode == REQUEST_DISPATCH_LOC_CODE && resultCode == Activity.RESULT_OK) {
            CarmenFeature carmenFeature = PlacePicker.getPlace(data);
            if (carmenFeature != null) {
                try {
                    double lat = new JSONObject(carmenFeature.toJson()).getJSONObject("geometry").getJSONArray("coordinates").getDouble(0);
                    double lng = new JSONObject(carmenFeature.toJson()).getJSONObject("geometry").getJSONArray("coordinates").getDouble(1);
                    bind.textDispatchLoc.setTag(new double[]{lat, lng});
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                bind.textDispatchLoc.setText(String.format(getString(R.string.selected_dispatch_place_info), carmenFeature.placeName()));
            }
        }
        if (requestCode == 22 && resultCode == Activity.RESULT_OK) {
            Toast.makeText(getContext(), "success", Toast.LENGTH_SHORT).show();

            new AlertDialog.Builder(getActivity())
                    .setTitle("Success")
                    .setMessage("Created a delivery job, you will be notified as soon as someone from TDE accepts the job. If your job is not accepted in one week, it will be removed")
                    .setPositiveButton("Continue", (dialogInterface, i) -> {

                        NavHostFragment.findNavController(this).navigate(R.id.action_createNewDeliveryRequest_to_userDashboardFragment);
                    }).create().show();
        }
    }


}