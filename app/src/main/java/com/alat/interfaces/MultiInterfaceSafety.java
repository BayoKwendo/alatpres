package com.alat.interfaces;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface MultiInterfaceSafety {

//    String IMAGEURL = "https://youthsofhope.co.ke/api/";
    @Multipart

    @POST("api/submitAlatRequestFile.php")
    Call<String> uploadImage(
            @Part MultipartBody.Part file,
            @Part("name") String _nname,
            @Part("msisdn") String msisdn,
            @Part("location") String location,
            @Part("alat_type") String alat_type,
            @Part("msisdn_provider") String msisdn_provider,
            @Part("userid") String userid,
            @Part("filename") RequestBody name

    );

}
