package com.aniapps.flicbuzzapp.activities

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.aniapps.flicbuzzapp.R
import com.aniapps.flicbuzzapp.networkcall.APIResponse
import com.aniapps.flicbuzzapp.networkcall.RetrofitClient
import com.aniapps.flicbuzzapp.player.LandingPage
import com.aniapps.flicbuzzapp.utils.PrefManager
import com.aniapps.flicbuzzapp.utils.Utility
import io.branch.indexing.BranchUniversalObject
import io.branch.referral.Branch
import io.branch.referral.BranchError
import io.branch.referral.SharingHelper
import io.branch.referral.util.ContentMetadata
import io.branch.referral.util.LinkProperties
import io.branch.referral.util.ShareSheetStyle
import org.json.JSONObject
import java.util.*
import kotlin.collections.HashMap


class Spalsh : AppCompatActivity() {
    private var t: Thread? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

    }

    fun ApiCall() {
        val params = HashMap<String, String>()
        params["action"] = "home"
        params["referral_code"] = PrefManager.getIn().branchData
        params["language"] = PrefManager.getIn().language.toLowerCase()

        var jsonObject: JSONObject
        RetrofitClient.getInstance().doBackProcess(this@Spalsh, params, "online", object : APIResponse {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            override fun onSuccess(result: String) {
                Log.e("abcd","branc"+PrefManager.getIn().branchData);
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
                    } else if (status == 90 || status == 11) {
                        alertDialog(this@Spalsh)
                    } else if (status == 14) {
                        Utility.alertDialog(this@Spalsh, jsonObject.getString("message"))
                    }else if (status == 98){
                        val intent = Intent(this@Spalsh, LandingPage::class.java)
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                        startActivity(intent)
                        overridePendingTransition(R.anim.left_slide_in, R.anim.left_slide_out)
                    }else if(status==99){
                        PrefManager.getIn().setSplash_message(jsonObject.getString("splash_message"))
                        val intent = Intent(this@Spalsh, PaymentScreen_Razor::class.java)
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                        startActivity(intent)
                        overridePendingTransition(R.anim.left_slide_in, R.anim.left_slide_out)
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
        PrefManager.getIn().clearLogins();
        val i = Intent(this@Spalsh, SignIn::class.java)
        startActivity(i)
        overridePendingTransition(R.anim.left_slide_in, R.anim.left_slide_out)
        finish()

    }
    override fun onStart() {
        super.onStart()

        // Branch init
        //https://docs.branch.io/apps/android/
        Branch.getInstance().initSession(object : Branch.BranchReferralInitListener {
            override fun onInitFinished(referringParams: JSONObject, error: BranchError?) {
                if (error == null) {
                    Log.e("BRANCH SDK", referringParams.toString())
                    PrefManager.getIn().branchData=referringParams.toString(2);

                  //  Toast.makeText(this@Spalsh, referringParams.toString(2), Toast.LENGTH_SHORT).show()
                    // Retrieve deeplink keys from 'referringParams' and evaluate the values to determine where to route the user
                    // Check '+clicked_branch_link' before deciding whether to use your Branch routing logic
                } else {
                    Log.e("BRANCH SDK", error.message)
                }
            }
        }, this.intent.data, this)

        val buo = BranchUniversalObject()
            .setCanonicalIdentifier("content/12345")
            .setTitle("My Content Title")
            .setContentDescription("My Content Description")
            .setContentIndexingMode(BranchUniversalObject.CONTENT_INDEX_MODE.PUBLIC)
            .setLocalIndexMode(BranchUniversalObject.CONTENT_INDEX_MODE.PUBLIC)
            .setContentMetadata(ContentMetadata().addCustomMetadata("key1", "value1"))
        buo.listOnGoogleSearch(this)

       /* val lp = LinkProperties()
            .setChannel("facebook")
            .setFeature("sharing")
            .setCampaign("content 123 launch")
            .setStage("new user")
            .addControlParameter("desktop_url", "https://www.flicbuzz.com/")
            .addControlParameter("custom", "data")
            .addControlParameter("custom_random", ""+Calendar.getInstance().timeInMillis)

        buo.generateShortUrl(this, lp, Branch.BranchLinkCreateListener { url, error ->
            if (error == null) {
                Log.i("BRANCH SDK", "got my Branch link to share: " + url)
            }
        })*/


       /* val ss = ShareSheetStyle(this@Spalsh, "Check this out!", "This stuff is awesome: ")
            .setCopyUrlStyle(ContextCompat.getDrawable(this@Spalsh, android.R.drawable.ic_menu_send), "Copy", "Added to clipboard")
            .setMoreOptionStyle(ContextCompat.getDrawable(this@Spalsh, android.R.drawable.ic_menu_search), "Show more")
            .addPreferredSharingOption(SharingHelper.SHARE_WITH.FACEBOOK)
            .addPreferredSharingOption(SharingHelper.SHARE_WITH.EMAIL)
            .addPreferredSharingOption(SharingHelper.SHARE_WITH.MESSAGE)
            .addPreferredSharingOption(SharingHelper.SHARE_WITH.HANGOUT)
            .setAsFullWidthStyle(true)
            .setSharingTitle("Share With")

        buo.showShareSheet(this, lp, ss, object : Branch.BranchLinkShareListener {
            override fun onShareLinkDialogLaunched() {}
            override fun onShareLinkDialogDismissed() {}
            override fun onLinkShareResponse(sharedLink: String, sharedChannel: String, error: BranchError) {}
            override fun onChannelSelected(channelName: String) {}
        })*/
       // keytool -list -v -keystore "C:\Users\MXC\.android\debug.keystore" -alias androiddebugkey -storepass android -keypass android
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        this.intent = intent
    }

    override fun onResume() {
        super.onResume()
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
}
