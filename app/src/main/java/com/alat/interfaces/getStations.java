package com.alat.interfaces;

import android.annotation.SuppressLint;

import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface getStations {
    @Headers({
            "Accept: application/json",
            "Content-Type: application/json"
    })
    @SuppressLint("StaticFieldLeak")
    @GET("api/client/readAllSt.php")
    Call<String> getRG(
            @Query("group_name") String firstname,
            @Query("alerts") int  second
    );
}
