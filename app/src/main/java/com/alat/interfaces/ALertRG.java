package com.alat.interfaces;

import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface ALertRG {
    @Headers("Content-Type: application/json")

    // @FormUrlEncoded
    @POST("api/alert/getRG.php")
    Call<ResponseBody> getrg(@Body Map<String, String> body);


}


