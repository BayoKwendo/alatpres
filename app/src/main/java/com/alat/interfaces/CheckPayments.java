package com.alat.interfaces;

import android.annotation.SuppressLint;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface CheckPayments {

    @SuppressLint("StaticFieldLeak")
    @GET("api/transactions/read.php")
    Call<String> checkPay(

            @Query("userid") String userid);

}
