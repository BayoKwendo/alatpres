package com.alat.ui.braintree.models;

import com.google.gson.annotations.SerializedName;

public class ClientToken {

    @SerializedName("client_token")
    private String mClientToken;

    public String getClientToken() {
        return mClientToken;
    }
}
