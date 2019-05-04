package com.aniapps.flicbuzz.networkcall;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;
import com.aniapps.flicbuzz.BuildConfig;
import com.aniapps.flicbuzz.R;
import com.aniapps.flicbuzz.utils.FlickLoading;
import com.aniapps.flicbuzz.utils.PrefManager;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import org.json.JSONObject;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

import java.util.Map;
import java.util.concurrent.TimeUnit;

/*
 * Created by NagRaj_Pilla on 4/12/2017.
 * REST CTE Service
 */

public class RetrofitClient extends AppCompatActivity {
    private static Retrofit retrofit = null;
    private static RetrofitClient uniqInstance;
    private boolean flagNoCrypt, sessionFlag = false, crt_flag = false;
    private Context context;
    private Map<String, String> params;
    private APIService apiService;
    private String TAG = "##Retrofit##", encrypted = "", from = "", decrypted = "",
            encryptedData = "", keyencrypted = "", keydecrypted = "", upToNCharacters = "";

    public static RetrofitClient getInstance() {
        if (uniqInstance == null) {
            uniqInstance = new RetrofitClient();
        }
        return uniqInstance;
    }

    private static Retrofit getClient(Context context) {
        if (retrofit == null) {
            OkHttpClient okHttpClient = null;

            okHttpClient = new OkHttpClient.Builder()
                    .readTimeout(90, TimeUnit.SECONDS)
                    .connectTimeout(90, TimeUnit.SECONDS)
                    .build();
            retrofit = new Retrofit.Builder()
                    .baseUrl(context.getResources().getString(R.string.base))
                    .addConverterFactory(new RetrofitConverter())
                    .client(okHttpClient)
                    .build();
            //  Log.e("##Retrofit##", "WITHOUT CRT");

        }
        return retrofit;
    }


    //RetrofitCallBack
    public void doBackProcess(final Context context, Map<String, String> postParams,
                              String from, APIResponse api_res) {
        this.context = context;
        this.params = postParams;
        this.from = from;

        if (from.length() == 0 && null != context) {
            try {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                          FlickLoading.getInstance().show(context);
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        getNoCryptCoreRes(context, from, postParams, api_res);


    }


    //NoCryptCore
    private void getNoCryptCoreRes(final Context context, final String from,
                                   final Map<String, String> postParams,
                                   final APIResponse api_res) {
        this.context = context;
        apiService = RetrofitClient.getClient(context).create(APIService.class);
        postParams.put("version_code", "" + BuildConfig.VERSION_CODE);
        postParams.put("device_id", PrefManager.getIn().getDeviceId());
        if (!postParams.get("action").equals("login") && !postParams.get("action").equals("verify_otp")&& !postParams.get("action").equals("resend_otp")) {
            postParams.put("user_id", PrefManager.getIn().getUserId());
        }
        postParams.put("language", PrefManager.getIn().getLanguage().toLowerCase());
        Log.e("#API#", "Post Params" + postParams);
        apiService.coreApiResult(context.getResources().getString(R.string.core_live) + postParams.get("action"), postParams).enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, final Response<String> res) {
              //  Log.e("RES", "res" + res.body());
                if (from.length() == 0) {
                    try {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                FlickLoading.getInstance().dismiss((Activity) context);
                            }
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                if (res.isSuccessful()) {
                    try {
                        if (null != res.body() && !res.body().equals("")) {
                            api_res.onSuccess(res.body().trim());
                        } else {
                            Toast.makeText(context, "Res" + res.body(), Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        api_res.onFailure(e.getMessage());
                    }
                } else {
                    api_res.onFailure("");
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                if (from.length() == 0) {
                    try {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                FlickLoading.getInstance().dismiss((Activity) context);
                            }
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                retrofit = null;
                api_res.onFailure(t.getMessage());
            }
        });
    }
    //Images
    public void getNoCryptResImages(final Context context, final Map<String, String> postParams,
                                    final MultipartBody.Part body, final APIResponse api_res) {
        apiService = RetrofitClient.getClient(context).create(APIService.class);
        postParams.put("version_code", "" + BuildConfig.VERSION_CODE);
        postParams.put("device_id", PrefManager.getIn().getDeviceId());
        postParams.put("user_id", PrefManager.getIn().getUserId());
        Log.e(TAG, "post params" + postParams);
        if (body != null) {
            apiService.uploadImage(context.getResources().getString(R.string.core_live)+ postParams.get("action"), body, postParams).enqueue(new Callback<String>() {
                @Override
                public void onResponse(Call<String> call, final Response<String> res) {
                   // Log.e("RES", "res" + res.body());
                    if (res.isSuccessful()) {
                        try {
                            if (null != res.body() && !res.body().equals("")) {
                                JSONObject jObj = new JSONObject(res.body());
                                int status = jObj.getInt("status");
                                switch (status) {
                                    case 25:
                                        retrofit = null;
                                        break;
                                    default:
                                        api_res.onSuccess(res.body());
                                        break;
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            api_res.onFailure(e.getMessage());
                        }
                    } else {
                        api_res.onFailure(res.message());
                    }
                }

                @Override
                public void onFailure(Call<String> call, Throwable t) {
                   // Log.e("RES", "failure" + t.getMessage());
                    retrofit = null;
                    api_res.onFailure(t.getMessage());
                }
            });
        }
    }
}
