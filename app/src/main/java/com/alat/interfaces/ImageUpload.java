package com.alat.interfaces;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface ImageUpload {

    String IMAGEURL = "https://youthsofhope.co.ke/api/image/";
    @FormUrlEncoded
    @POST("api/alert/upLoad.php")
    Call<String> getUserLogin(
            @Field("name") String name,
            @Field("image") String image
    );

}