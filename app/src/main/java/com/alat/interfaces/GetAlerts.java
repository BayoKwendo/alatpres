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

public interface GetAlerts {
    @Headers("Content-Type: application/json")

    // @FormUrlEncoded
    @POST("api/alert/read.php")
    Call<ResponseBody> GetAlert(@Body Map<String, String> body);


}


