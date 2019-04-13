package com.aniapps.flicbuzz.networkcall;

import android.app.Dialog;
import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import com.aniapps.flicbuzz.BuildConfig;
import com.aniapps.flicbuzz.R;
import com.aniapps.flicbuzz.utils.PrefManager;
import okhttp3.OkHttpClient;
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

    Dialog dialog;

    //RetrofitCallBack
    public void doBackProcess(final Context context, Map<String, String> postParams,
                              String from, APIResponse api_res) {
        this.context = context;
        this.params = postParams;
        this.from = from;
        dialog = ProgressDialog.Companion.progressDialog(context);
        if (from.length() == 0 && null != context) {
            try {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        dialog.show();
                        //  CTEProgress.getInstance().show(context);
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
        apiService.coreApiResult(context.getResources().getString(R.string.core_live) + "/" + postParams.get("action"), postParams).enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, final Response<String> res) {
                Log.e("RES", "res" + res.body());
                if (res.isSuccessful()) {
                    if (from.length() == 0) {
                        try {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    dialog.dismiss();
                                    //  CTEProgress.getInstance().dismiss((Activity) context);
                                }
                            });
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    try {
                        int status = 0;
                        if (null != res.body() && !res.body().equals("")) {
                            /*try {
                                JSONObject jObj = new JSONObject(res.body());
                                status = jObj.getInt("status");
                            } catch (JSONException e) {
                                Log.e("JSON Parser", "Error parsing data [" + e.getMessage() + "] " + status);
                            }*/
                            //  Log.e(TAG, "No Cypt Results:" + res.body());

                            //if (status == 1) {
                            api_res.onSuccess(res.body().trim());
                            //}
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
                                dialog.dismiss();
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
}
