package com.digipodium.tde.models;

public class PaymentModel {

    public String personId;
    public String deliveryId;
    public int priceToBePayed;
    public boolean isPayed;

    public PaymentModel(String personId, String deliveryId, int priceToBePayed, boolean isPayed) {
        this.personId = personId;
        this.deliveryId = deliveryId;
        this.priceToBePayed = priceToBePayed;
        this.isPayed = isPayed;
    }

    public PaymentModel() {

    }
}
