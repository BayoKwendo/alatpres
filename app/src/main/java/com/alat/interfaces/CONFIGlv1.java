package com.alat.interfaces;

import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface CONFIGlv1 {
    @Headers("Content-Type: application/json")

    // @FormUrlEncoded
    @POST("api/configurelevel1.php")
    Call<ResponseBody> config(@Body Map<String, String> body);
}


