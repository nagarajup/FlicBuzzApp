package com.aniapps.flicbuzzapp.networkcall;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.*;

import java.util.Map;

/**
 * Created by NagRaj_Pilla on 4/13/2017.
 * Service call
 */

public interface APIService {
    @FormUrlEncoded
    @POST
    Call<String> getApiResult(@Url String url, @FieldMap Map<String, String> fields);

    @FormUrlEncoded
    @POST
    Call<String> coreApiResult(@Url String coreBaseUrl, @FieldMap Map<String, String> fields);

    @Multipart
    @POST
    Call<String> uploadImage(@Url String baseUrl, @Part MultipartBody.Part file, @PartMap() Map<String, String> fields);
}
