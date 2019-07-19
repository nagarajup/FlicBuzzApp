package com.aniapps.flicbuzzapp.networkcall;

import android.app.Activity;
import android.content.Context;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.aniapps.flicbuzzapp.BuildConfig;
import com.aniapps.flicbuzzapp.R;
import com.aniapps.flicbuzzapp.utils.*;
import com.google.gson.Gson;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import org.json.JSONObject;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

import java.net.URLEncoder;
import java.util.HashMap;
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
        postParams.put("api_key", "fb2019v1.0");
        postParams.put("device_id", Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID));
        if (!postParams.get("action").equals("login")) {
            postParams.put("user_id", PrefManager.getIn().getUserId());
        }
        postParams.put("from_source", "android");

        String device_id = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);

        String SessionKey = CryptSession.getKey(device_id + BuildConfig.VERSION_CODE);
        upToNCharacters = SessionKey.substring(0, Math.min(SessionKey.length(), 16));
        final CryptoHandler cte_crypt_login = new CryptoHandler(upToNCharacters);

        Gson gson = new Gson();
        String jsonSringParams = gson.toJson(postParams);
        Map<String, String> postEncryptedDataParams = new HashMap<>();
        //Log.e(TAG, "Get Session" + Pref.getIn().getUser_name() + "##" + Pref.getIn().getPsw().toUpperCase());
        Log.e(TAG, "jsonSringParams" + jsonSringParams);

        try {
           // postEncryptedDataParams.put("encrypted_data", cte_crypt_login.encrypt(jsonSringParams));
            postEncryptedDataParams.put("encrypted_data", URLEncoder.encode(cte_crypt_login.encrypt(jsonSringParams), "utf-8"));
            postEncryptedDataParams.put("version_code", "" + BuildConfig.VERSION_CODE);
            postEncryptedDataParams.put("device_id", "" + device_id);


           // Log.e("#Encryption#", "res encrypt222" + cte_crypt_login.decrypt("QFkpr6cYBKeKnZ74IWkFTg=="));

        } catch (Exception e) {
            e.printStackTrace();
        }


        Log.e("RES", "postparams" + postParams);
        apiService.coreApiResult(context.getResources().getString(R.string.core_live) + postParams.get("action"), postEncryptedDataParams).enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, final Response<String> res) {
                Log.e("RES", "res" + res.body());
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
                            JSONObject jObj = new JSONObject(res.body());
                            int status = jObj.getInt("status");
                            String type = jObj.getString("type");
                            if (type.equals("encrypted")) {
                                encrypted = jObj.getString("data");
                                decrypted = cte_crypt_login.decrypt(encrypted);
                               // Log.e("RES", "resres encrypted" + encrypted);
                               // Log.e("RES", "resres decrypted" + decrypted);
                                api_res.onSuccess(decrypted);
                            } else {
                                api_res.onSuccess(res.body().trim());
                            }
                        } else {
                         //   Toast.makeText(context, "Res" + res.body(), Toast.LENGTH_SHORT).show();
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
        postParams.put("api_key", "fb2019v1.0");
        // postParams.put("device_id", PrefManager.getIn().getDeviceId());
        postParams.put("device_id", Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID));
        postParams.put("user_id", PrefManager.getIn().getUserId());
        //Log.e(TAG, "post params" + postParams);
        if (body != null) {
            apiService.uploadImage(context.getResources().getString(R.string.core_live) + postParams.get("action"), body, postParams).enqueue(new Callback<String>() {
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
