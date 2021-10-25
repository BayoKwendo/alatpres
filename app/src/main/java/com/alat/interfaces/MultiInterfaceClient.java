package com.alat.interfaces;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface MultiInterfaceClient {
    String IMAGEURL = "https://youthsofhope.co.ke/api/";
    @Multipart
    @POST("api/alert/upLoadClient.php")
    Call<String> uploadImage(
            @Part MultipartBody.Part file,
            @Part("alert_name") String alert_name,
            @Part("fullname") String fullname,
            @Part("alert_type") String alert_trype,
            @Part("rg") String rg,
            @Part("rl") String rl,
            @Part("mssdn") String mssdn,
            @Part("userid") String userid,
            @Part("location") String location,
            @Part("notes") String notes,
            @Part("filename") RequestBody name
    ); }
