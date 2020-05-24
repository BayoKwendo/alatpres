package com.alat.interfaces;

import android.annotation.SuppressLint;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface GetAlertPost {

    @SuppressLint("StaticFieldLeak")
    @GET("api/alert/read_one.php/")
    Call<String> getAlert(

            @Query("id") String ids,
            @Query("alert_type") String alert_type,
            @Query("fullname") String name,
            @Query("rg") String rg,
            @Query("location") String location,
            @Query("created") String created1);
}
