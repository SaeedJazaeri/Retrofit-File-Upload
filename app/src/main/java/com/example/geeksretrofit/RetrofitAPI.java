package com.example.geeksretrofit;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface RetrofitAPI {


    String BASE_URL = "https://didbin.ir/api/";


    @POST("upload/")
    @Headers({"Authorization: Token 94cfa1de40a1fd967be648b265b467aec2d49fc1"})
    @Multipart
    Call<okhttp3.ResponseBody> createPost(@Part("description") RequestBody description,
                                                 @Part("date_published") RequestBody date_published,
                                                 @Part MultipartBody.Part file
    );

}