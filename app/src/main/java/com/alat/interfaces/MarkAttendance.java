package com.alat.interfaces;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface MarkAttendance {

    // String REGIURL = "https://demonuts.com/Demonuts/JsonTest/Tennis/";
    @FormUrlEncoded

   // @Headers("Authorization: Bearer")

    @POST("attendance/create")
    Call<String> markAttendace(
            @Field("device_id") String deviceId,
            @Field("tag_id") String tagId,
            @Field("admission_number") String admnNo,
            @Field("state") String state);

}
