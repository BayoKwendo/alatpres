package com.alat.interfaces;

import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface deleteConfig2 {
    @Headers("Content-Type: application/json")

    // @FormUrlEncoded
    @POST("api/exitRP2.php")
    Call<ResponseBody> config(@Body Map<String, String> body);
}


