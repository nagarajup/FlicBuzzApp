package com.aniapps.flicbuzzapp.activities

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.support.annotation.RequiresApi
import android.support.v7.app.AppCompatActivity
import android.widget.Toast
import com.aniapps.flicbuzzapp.R
import com.aniapps.flicbuzzapp.networkcall.APIResponse
import com.aniapps.flicbuzzapp.networkcall.RetrofitClient
import com.aniapps.flicbuzzapp.player.LandingPage
import com.aniapps.flicbuzzapp.utils.PrefManager
import com.aniapps.flicbuzzapp.utils.Utility
import org.json.JSONObject


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

            if (Utility.isConnectingToInternet(this@Spalsh)) {
                ApiCall()
            } else {
                Toast.makeText(this@Spalsh, "No Internet", Toast.LENGTH_SHORT).show()
            }
        }

    }

    fun ApiCall() {
        val params = HashMap<String, String>()
        params["action"] = "home"
        params["language"] = PrefManager.getIn().language.toLowerCase()
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
                        PrefManager.getIn().setGateway(jsonObject.getString("gateway"))
                        val userObject = jsonObject.getJSONObject("data")
                        PrefManager.getIn().setName(userObject.getString("name"))
                        PrefManager.getIn().setEmail(userObject.getString("email"))
                        PrefManager.getIn().setMobile(userObject.getString("mobile"))
                        PrefManager.getIn().setGender(userObject.getString("gender"))
                        PrefManager.getIn().setCity(userObject.getString("city"))
                        PrefManager.getIn().setPincode(userObject.getString("pincode"))
                        PrefManager.getIn().setDob(userObject.getString("dob"))
                        PrefManager.getIn().setProfile_pic(userObject.getString("profile_pic"))
                        PrefManager.getIn().setSubscription_auto_renew(jsonObject.getString("subscription_auto_renew"))
                        if (PrefManager.getIn().getPayment_mode() == "1" && !PrefManager.getIn().getPlan().equals(
                                "expired",
                                ignoreCase = true
                            )
                        ) {
                            val intent = Intent(this@Spalsh, LandingPage::class.java)
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                            startActivity(intent)
                            overridePendingTransition(R.anim.left_slide_in, R.anim.left_slide_out)
                        } else {
                            val intent = Intent(this@Spalsh, PaymentScreen_Razor::class.java)
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                            startActivity(intent)
                            overridePendingTransition(R.anim.left_slide_in, R.anim.left_slide_out)
                        }
                        try {
                            runOnUiThread {
                                PrefManager.getIn().sendRegistrationToServer(
                                    this@Spalsh,
                                    PrefManager.getIn().getFcm_token()
                                )
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                        finish()
                    } else if (status == 90) {
                        alertDialog(this@Spalsh)
                    } else if (status == 14) {
                        Utility.alertDialog(this@Spalsh, jsonObject.getString("message"))
                    }

                } catch (e: Exception) {
                    Utility.alertDialog(this@Spalsh,"Alert!",e.message);
                    e.printStackTrace()
                }

            }

            override fun onFailure(res: String) {
                Utility.alertDialog(this@Spalsh,"Alert!",res);
            }
        })
    }

    fun alertDialog(context: Context) {
        /*val builder = AlertDialog.Builder(context)
        builder.setMessage("This Account is activated in another device, Please logout and try again.")
        builder.setTitle("Notice")
        builder.setCancelable(false)
        builder.setPositiveButton("OK") { dialog, which ->*/
        PrefManager.getIn().clearLogins();
        val i = Intent(this@Spalsh, SignIn::class.java)
        startActivity(i)
        overridePendingTransition(R.anim.left_slide_in, R.anim.left_slide_out)
        finish()
        /* }
         //builder.setNegativeButton("NO", null);
         builder.show()*/
    }


}
