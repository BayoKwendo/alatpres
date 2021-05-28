package com.alat.interfaces;

import android.annotation.SuppressLint;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ConfirmPayments {

    @SuppressLint("StaticFieldLeak")
    @GET("api/transactions/update.php")
    Call<String> confirmPay(
            @Query("phone") String phone,
            @Query("amount") String amount,
            @Query("userid") String userid);

}
