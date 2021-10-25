package com.alat.interfaces;

import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface AddStation {
    @Headers({
            "Accept: application/json",
            "Content-Type: application/json"
    })

    @POST("api/client/createStation.php")
    Call<ResponseBody> createStation(@Body Map<String, String> body);
}
