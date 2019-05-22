package com.aniapps.flicbuzzapp.activities

import android.app.*
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.support.annotation.RequiresApi
import android.support.v7.app.AppCompatActivity
import com.aniapps.flicbuzzapp.R
import com.aniapps.flicbuzzapp.networkcall.APIResponse
import com.aniapps.flicbuzzapp.networkcall.RetrofitClient
import com.aniapps.flicbuzzapp.player.LandingPage
import com.aniapps.flicbuzzapp.utils.PrefManager
import org.json.JSONObject
import android.support.v4.app.NotificationCompat
import android.graphics.Bitmap
import com.google.android.exoplayer2.util.NotificationUtil.createNotificationChannel
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.graphics.Color


class Spalsh : AppCompatActivity() {
    private var t: Thread? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        if (!PrefManager.getIn().login) {
            Handler().postDelayed({

                val i = Intent(this@Spalsh, IntroductionScreen::class.java)
                startActivity(i)
                overridePendingTransition(R.anim.left_slide_in, R.anim.left_slide_out)

                finish()
            }, 2000)
        } else {
            ApiCall()
        }

    }

    fun ApiCall() {
        val params = HashMap<String, String>()
        params["action"] = "home"
        var jsonObject: JSONObject
        RetrofitClient.getInstance().doBackProcess(this@Spalsh, params, "online", object : APIResponse {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            override fun onSuccess(result: String) {
                try {
                    jsonObject = JSONObject(result)
                    val status = jsonObject.getInt("status")
                    if (status == 1) {
                        PrefManager.getIn().login = true
                        PrefManager.getIn().saveUserId(jsonObject.getString("user_id"))
                        PrefManager.getIn().setPayment_data(jsonObject.getString("payment_data"))
                        PrefManager.getIn()
                            .setSubscription_start_date(jsonObject.getString("subscription_start_date"))
                        PrefManager.getIn().setSubscription_end_date(jsonObject.getString("subscription_end_date"))
                        PrefManager.getIn().setPlan(jsonObject.getString("plan"))
                        PrefManager.getIn().setPayment_mode(jsonObject.getString("payment_mode"))
                        PrefManager.getIn().setDeveloper_mode(jsonObject.getString("developer_mode"))
                        PrefManager.getIn().setServer_version_mode(jsonObject.getString("server_version_mode"))
                        PrefManager.getIn().setShow_splash_message(jsonObject.getString("show_splash_message"))
                        PrefManager.getIn().setSplash_message(jsonObject.getString("splash_message"))
                        val userObject = jsonObject.getJSONObject("data")
                        PrefManager.getIn().setName(userObject.getString("name"))
                        PrefManager.getIn().setEmail(userObject.getString("email"))
                        PrefManager.getIn().setMobile(userObject.getString("mobile"))
                        PrefManager.getIn().setGender(userObject.getString("gender"))
                        PrefManager.getIn().setCity(userObject.getString("city"))
                        PrefManager.getIn().setPincode(userObject.getString("pincode"))
                        PrefManager.getIn().setDob(userObject.getString("dob"))
                        PrefManager.getIn().setProfile_pic(userObject.getString("profile_pic"))
                        if (PrefManager.getIn().getPayment_mode() == "1" && !PrefManager.getIn().getPlan().equals(
                                "expired",
                                ignoreCase = true
                            )) {
                            val intent = Intent(this@Spalsh, LandingPage::class.java)
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                            startActivity(intent)
                            overridePendingTransition(R.anim.left_slide_in, R.anim.left_slide_out)
                        } else {
                            val intent = Intent(this@Spalsh, PaymentScreen_New::class.java)
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                            startActivity(intent)
                            overridePendingTransition(R.anim.left_slide_in, R.anim.left_slide_out)
                        }
                        finish()
                    }else if(status == 90){
                        alertDialog(this@Spalsh)
                    }

                } catch (e: Exception) {
                    e.printStackTrace()
                }

            }

            override fun onFailure(res: String) {
            }
        })
    }
    fun alertDialog(context: Context) {
        val builder = AlertDialog.Builder(context)
        builder.setMessage("This Account is activated in another device, Please logout and try again.")
        builder.setTitle("Notice")
        builder.setCancelable(false)
        builder.setPositiveButton("OK") { dialog, which ->
            PrefManager.getIn().clearLogins();
            val i = Intent(this@Spalsh, SignIn::class.java)
            startActivity(i)
            overridePendingTransition(R.anim.left_slide_in, R.anim.left_slide_out)
            finish()
        }
        //builder.setNegativeButton("NO", null);
        builder.show()
    }


}
