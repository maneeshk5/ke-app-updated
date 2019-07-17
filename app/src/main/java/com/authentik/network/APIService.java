package com.authentik.network;

import com.authentik.model.APIResponse;
import com.authentik.model.AssetsQues;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.HeaderMap;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.PartMap;
import retrofit2.http.Query;

public interface APIService {

    @POST("user/public/login")
    Call<APIResponse> login(@Body HashMap<String, Object> body);

    @GET("asset/questionsList")
    Call<APIResponse> getQuestionsList(@HeaderMap Map<String, String> headers);

    @Multipart
    @POST("asset/add")
    Call<APIResponse> sendAnswers(@HeaderMap Map<String, String> headers,@Part List<MultipartBody.Part> files, @Query("data[]") List<String> partMap);
}
