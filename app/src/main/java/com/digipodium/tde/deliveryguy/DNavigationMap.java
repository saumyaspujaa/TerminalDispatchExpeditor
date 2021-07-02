package com.digipodium.tde.deliveryguy;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.digipodium.tde.Constants;
import com.digipodium.tde.R;
import com.digipodium.tde.databinding.FragmentDNavigationMapBinding;
import com.digipodium.tde.models.DeliveryModel;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.mapbox.api.directions.v5.DirectionsCriteria;
import com.mapbox.api.directions.v5.MapboxDirections;
import com.mapbox.api.directions.v5.models.BannerInstructions;
import com.mapbox.api.directions.v5.models.DirectionsResponse;
import com.mapbox.api.directions.v5.models.DirectionsRoute;
import com.mapbox.geojson.Point;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.navigation.base.internal.VoiceUnit;
import com.mapbox.navigation.core.MapboxNavigation;
import com.mapbox.navigation.ui.NavigationViewOptions;
import com.mapbox.navigation.ui.OnNavigationReadyCallback;
import com.mapbox.navigation.ui.listeners.NavigationListener;
import com.mapbox.navigation.ui.map.NavigationMapboxMap;

import org.jetbrains.annotations.NotNull;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DNavigationMap extends Fragment implements OnNavigationReadyCallback, NavigationListener {

    NavigationMapboxMap navigationMapboxMap;
    MapboxNavigation mapboxNavigation;
    private FirebaseAuth auth;
    private CollectionReference deliveryDb;
    private FragmentDNavigationMapBinding bind;
    private @NonNull
    String deliveryId;
    private DeliveryModel deliveryModel;

    private Point startPoint;
    private Point endPoint;
    private DirectionsRoute directionsRoute;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        auth = FirebaseAuth.getInstance();
        deliveryDb = FirebaseFirestore.getInstance().collection(Constants.COL_DELIVERY);
        Mapbox.getInstance(getActivity(), getString(R.string.mapbox_access_token));
        return inflater.inflate(R.layout.fragment_d_navigation_map, container, false);
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        bind = FragmentDNavigationMapBinding.bind(view);
        deliveryId = DNavigationMapArgs.fromBundle(getArguments()).getDeliveryId();
        Task<DocumentSnapshot> task = deliveryDb.document(deliveryId).get();
        task.addOnSuccessListener(documentSnapshot -> {
            deliveryModel = documentSnapshot.toObject(DeliveryModel.class);
            bind.navigationView.onCreate(savedInstanceState);
            bind.navigationView.initialize(this);
        });

    }

    @NotNull
    private Point getPoint(String startLoc) {
        String[] coordStr = startLoc.split(",");
        String substring = coordStr[0].substring(1, coordStr[0].length());
        double lat1 = Double.parseDouble(substring);
        String substring1 = coordStr[1].substring(1, coordStr[1].length() - 2);
        double lng1 = Double.parseDouble(substring1);
        return Point.fromLngLat(lat1, lng1);
    }


    @Override
    public void onNavigationReady(boolean isRunning) {
        navigationMapboxMap = bind.navigationView.retrieveNavigationMapboxMap();
        mapboxNavigation = bind.navigationView.retrieveMapboxNavigation();
        startPoint = getPoint(deliveryModel.startLoc);
        endPoint = getPoint(deliveryModel.dispatchLoc);
        MapboxDirections directions = MapboxDirections.builder()
                .accessToken(getString(R.string.mapbox_access_token))
                .origin(startPoint)
                .steps(true)
                .destination(endPoint)
                .overview(DirectionsCriteria.OVERVIEW_FULL)
                .profile(DirectionsCriteria.PROFILE_DRIVING_TRAFFIC)
                .voiceInstructions(true)
                .voiceUnits(VoiceUnit.METRIC)
                .bannerInstructions(true)
                .alternatives(true)
                .enableRefresh(true)
                .build();
        directions.enqueueCall(new Callback<DirectionsResponse>() {
            @Override
            public void onResponse(Call<DirectionsResponse> call, Response<DirectionsResponse> response) {
                if (response.body() == null) {
                    alert("no Routes found", "make sure you set the right user and access token.", "close navigation").show();
                } else if (response.body().routes().size() < 1) {
                    alert("Location not found", "the direction could not be retrieved", "close navigation").show();
                }
                directionsRoute = response.body().routes().get(0);
                NavigationViewOptions op = NavigationViewOptions.builder(getActivity())
                        .directionsRoute(directionsRoute)
                        .navigationListener(DNavigationMap.this)
                        .build();
                try {
                    bind.navigationView.startNavigation(op);
                    bind.dataLoadProgress.setVisibility(View.GONE);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<DirectionsResponse> call, Throwable t) {
                alert("Location not found", t.getMessage(), "close navigation").show();
            }
        });
        if (isRunning) {
            if (bind.navigationView.retrieveNavigationMapboxMap() != null) {

            }
        } else {
            Toast.makeText(getActivity(), "not running", Toast.LENGTH_SHORT).show();
        }
    }

    @NotNull
    private AlertDialog alert(String title, String message, String btnText) {
        return new AlertDialog.Builder(getActivity())
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton(btnText, (dialogInterface, i) -> {
                    NavHostFragment.findNavController(DNavigationMap.this).navigate(DNavigationMapDirections.actionDNavigationMapToDCurrentDelivery(deliveryId));
                }).create();
    }

    @Override
    public void onCancelNavigation() {

    }

    @Override
    public void onNavigationFinished() {
        AlertDialog dialog = alert("Location reached", "you have reached your target destination", "close navigation");
        dialog.show();
    }

    @Override
    public void onNavigationRunning() {

    }
}