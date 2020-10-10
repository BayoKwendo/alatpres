package com.alat.interfaces;

import android.annotation.SuppressLint;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface GetClients {

    @SuppressLint("StaticFieldLeak")

    @POST("api/client/readClient.php")

    Call<String> getRG(
       @Body Map<String, String> body);

}


