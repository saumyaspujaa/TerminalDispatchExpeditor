package com.digipodium.tde.models;

public class ActiveDeliveryModel {

    public String personId;
    public String deliveryId;
    public long timestamp;

    public ActiveDeliveryModel() {
    }

    public ActiveDeliveryModel(String personId, String deliveryId, long timestamp) {
        this.personId = personId;
        this.deliveryId = deliveryId;
        this.timestamp = timestamp;
    }
}
