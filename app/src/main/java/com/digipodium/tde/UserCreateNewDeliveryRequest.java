package com.digipodium.tde;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.digipodium.tde.databinding.FragmentUserCreateNewDeliveryRequestBinding;
import com.mapbox.api.geocoding.v5.models.CarmenFeature;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.camera.CameraPosition;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.plugins.places.picker.PlacePicker;
import com.mapbox.mapboxsdk.plugins.places.picker.model.PlacePickerOptions;

import org.json.JSONException;
import org.json.JSONObject;


public class UserCreateNewDeliveryRequest extends Fragment {

    private FragmentUserCreateNewDeliveryRequestBinding bind;
    private static final int REQUEST_START_LOC_CODE = 5678;
    private static final int REQUEST_DISPATCH_LOC_CODE = 5679;
    static final int REQUEST_IMAGE_GET = 1;
    private LatLng LUCKNOW_COORDINATES;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Mapbox.getInstance(getActivity(), getString(R.string.mapbox_access_token));
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
            double[] dispatchCoords = (double[]) bind.textDispatchLoc.getTag();
            double[] startCoords = (double[]) bind.textStartLoc.getTag();
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
    }
}