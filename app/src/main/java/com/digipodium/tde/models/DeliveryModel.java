package com.digipodium.tde.models;

import com.google.firebase.firestore.FieldValue;

public class DeliveryModel {
    public String fullName;
    public String phone;
    public String address;
    public String city;
    public String email;
    public String deliveryDetails;
    public String startLocationAddr;
    public String dispatchLocationAddr;
    public String startLoc;
    public String dispatchLoc;
    public String img;
    public FieldValue timestamp;

    public DeliveryModel(String fullName, String phone, String address, String city,
                         String email, String deliveryDetails, String startLocationAddr,
                         String dispatchLocationAddr, String startLoc, String dispatchLoc,
                         String img, FieldValue timestamp) {
        this.fullName = fullName;
        this.phone = phone;
        this.address = address;
        this.city = city;
        this.email = email;
        this.deliveryDetails = deliveryDetails;
        this.startLocationAddr = startLocationAddr;
        this.dispatchLocationAddr = dispatchLocationAddr;
        this.startLoc = startLoc;
        this.dispatchLoc = dispatchLoc;
        this.img = img;
        this.timestamp = timestamp;
    }

    public DeliveryModel() {
    }
}
