package com.alat.interfaces;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface AddNFCInterface {

    // String REGIURL = "https://demonuts.com/Demonuts/JsonTest/Tennis/";
    @FormUrlEncoded

   // @Headers("Authorization: Bearer")

    @POST("NFCTag/create")
    Call<String> getUserRegi(
            @Field("device_id") String deviceId,
            @Field("tag_id") String tagId,
            @Field("admission_number") String admnNo,
            @Field("latitude") String latitude,
            @Field("longitude") String longitude);

}
