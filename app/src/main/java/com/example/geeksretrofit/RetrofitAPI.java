package com.example.geeksretrofit;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface RetrofitAPI {

    // as we are making a post request to post a data
    // so we are annotating it with post
    // and along with that we are passing a parameter as users

    String BASE_URL = "https://didbin.ir/api/";

    @Multipart
    @POST("upload/")
    @Headers("Authorization: Token 94cfa1de40a1fd967be648b265b467aec2d49fc1")
    //on below line we are creating a method to post our data.
    Call<ResponseBody> createPost(@Part("description") RequestBody description,
                                  @Part("date_published") RequestBody date_published,
                                  @Part MultipartBody.Part file
                                 );  //Here we used ResponseBody the class of okhttp,
                                    //change to our ResponseBody if need
}