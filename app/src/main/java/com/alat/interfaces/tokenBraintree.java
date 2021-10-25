package com.alat.interfaces;

import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface tokenBraintree {

        @Headers({
                "Accept: application/json",
                "Content-Type: application/json"
        })
//    @FormUrlEncoded
        @POST("api/rg/readRG.php")
        Call<ResponseBody> viewRG(@Body Map<String, String> body);
}