package com.alat.interfaces;

import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface FriendReq {
    @Headers("Content-Type: application/json")

    // @FormUrlEncoded
    @POST("api/rg/findRequests.php")
    Call<ResponseBody> Requests(@Body Map<String, String> body);
}


