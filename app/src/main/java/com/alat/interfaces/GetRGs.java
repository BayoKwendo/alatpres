package com.alat.interfaces;

import android.annotation.SuppressLint;
import android.widget.TextView;


import org.jetbrains.annotations.Nullable;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface GetRGs {

    @SuppressLint("StaticFieldLeak")

    @GET("api/rg/read.php")

    Call<String> getRG(
            @Query("group_name") String firstname,
            @Query("alerts") int  second
    );


}


