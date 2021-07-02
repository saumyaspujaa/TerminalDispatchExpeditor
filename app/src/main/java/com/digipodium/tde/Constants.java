package com.digipodium.tde;

import com.google.android.gms.wallet.WalletConstants;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/**
 * This file contains several constants you must edit before proceeding.
 * Please take a look at PaymentsUtil.java to see where the constants are used and to potentially
 * remove ones not relevant to your integration.
 *
 * <p>Required changes:
 * <ol>
 * <li> Update SUPPORTED_NETWORKS and SUPPORTED_METHODS if required (consult your processor if
 *      unsure)
 * <li> Update CURRENCY_CODE to the currency you use.
 * <li> Update SHIPPING_SUPPORTED_COUNTRIES to list the countries where you currently ship. If this
 *      is not applicable to your app, remove the relevant bits from PaymentsUtil.java.
 * <li> If you're integrating with your {@code PAYMENT_GATEWAY}, update
 *      PAYMENT_GATEWAY_TOKENIZATION_NAME and PAYMENT_GATEWAY_TOKENIZATION_PARAMETERS per the
 *      instructions they provided. You don't need to update DIRECT_TOKENIZATION_PUBLIC_KEY.
 * <li> If you're using {@code DIRECT} integration, please edit protocol version and public key as
 *      per the instructions.
 */
public class Constants {

    public static final String PRIMARY_CHANNEL_ID = "primary_notification_channel";

    public static final String COL_USERS = "Users";
    public static final String COL_DELIVERY = "Deliveries";
    public static final String COL_DLV_PERSON = "DeliveryPerson";
    public static final String COL_REPORTS = "Reports";
}