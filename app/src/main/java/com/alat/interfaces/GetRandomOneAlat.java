package com.alat.interfaces;

import android.annotation.SuppressLint;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface GetRandomOneAlat {

    @SuppressLint("StaticFieldLeak")
    @GET("api/alert/getOneRandomAlat.php")
    Call<String> getAlert(

            @Query("id") String ids,
            @Query("alert_name") String alert_name,
            @Query("mssdn") String mssdn,
            @Query("userid") String userid,
            @Query("alert_type") String alert_type,
            @Query("fullname") String name,
            @Query("rg") String rg,
            @Query("location") String location,
            @Query("attachment") String attachment,
            @Query("created") String created1,
            @Query("modified") String modified);

}
