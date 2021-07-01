package com.digipodium.tde.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;

public class DeliveryModel implements Parcelable {
    @SuppressWarnings("unused")
    public static final Parcelable.Creator<DeliveryModel> CREATOR = new Parcelable.Creator<DeliveryModel>() {
        @Override
        public DeliveryModel createFromParcel(Parcel in) {
            return new DeliveryModel(in);
        }

        @Override
        public DeliveryModel[] newArray(int size) {
            return new DeliveryModel[size];
        }
    };
    public String status;
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
    @ServerTimestamp
    public Date timestamp;
    public int price;

    public DeliveryModel() {

    }

    public DeliveryModel(String fullName, String phone, String address, String city,
                         String email, String deliveryDetails, String startLocationAddr,
                         String dispatchLocationAddr, String startLoc, String dispatchLoc,
                         String img, int price, String status) {
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
        this.timestamp = new Date(System.currentTimeMillis());
        this.status = status;
        this.price = price;
    }

    protected DeliveryModel(Parcel in) {
        status = in.readString();
        fullName = in.readString();
        phone = in.readString();
        address = in.readString();
        city = in.readString();
        email = in.readString();
        deliveryDetails = in.readString();
        startLocationAddr = in.readString();
        dispatchLocationAddr = in.readString();
        startLoc = in.readString();
        dispatchLoc = in.readString();
        img = in.readString();
        price = in.readInt();
        long tmpTimestamp = in.readLong();
        timestamp = tmpTimestamp != -1 ? new Date(tmpTimestamp) : null;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(status);
        dest.writeString(fullName);
        dest.writeString(phone);
        dest.writeString(address);
        dest.writeString(city);
        dest.writeString(email);
        dest.writeString(deliveryDetails);
        dest.writeString(startLocationAddr);
        dest.writeString(dispatchLocationAddr);
        dest.writeString(startLoc);
        dest.writeString(dispatchLoc);
        dest.writeString(img);
        dest.writeInt(price);
        dest.writeLong(timestamp != null ? timestamp.getTime() : -1L);
    }
}