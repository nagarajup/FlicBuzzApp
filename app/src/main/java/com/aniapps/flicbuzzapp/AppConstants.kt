package com.aniapps.flicbuzzapp

import android.Manifest
import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

import com.aniapps.flicbuzzapp.utils.PrefManager
import com.crashlytics.android.Crashlytics
import com.google.firebase.analytics.FirebaseAnalytics
import io.branch.referral.util.BRANCH_STANDARD_EVENT
import io.branch.referral.util.BranchEvent


@SuppressLint("Registered")
open class AppConstants : AppCompatActivity() {
    public var mFirebaseAnalytics: FirebaseAnalytics? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

      //  AppsFlyerLib.getInstance().setCustomerUserId(PrefManager.getIn().getMobile());
        Crashlytics.setUserIdentifier(PrefManager.getIn().getMobile())

        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this@AppConstants);
        mFirebaseAnalytics!!.setUserProperty("user_id", PrefManager.getIn().getMobile());
        mFirebaseAnalytics!!.setUserId(PrefManager.getIn().getMobile());


    }

    /*https://support.appsflyer.com/hc/en-us/articles/207032126-AppsFlyer-SDK-Integration-Android*/
    /*https://support.appsflyer.com/hc/en-us/articles/208386173-AppsFlyer-Android-Sample-App*/
    fun trackEvent(context: Activity, eventCategory: String, eventAction: String) {
       /* val eventValue = HashMap<String, Any>()
        eventValue["category"] = eventCategory
        eventValue["action"] = eventAction
        eventValue["user_id"] = PrefManager.getIn().getMobile()
        AppsFlyerLib.getInstance().trackEvent(context, "my_events", eventValue)*/

        /* CustomDataMap.put("custom_param_1", "value_of_param_1")
         AppsFlyerLib.getInstance().setAdditionalData(CustomDataMap)*/

        BranchEvent("my_events")
            .addCustomDataProperty("Category", eventCategory)
            .addCustomDataProperty("Action", eventAction)
            .logEvent(context);


        if (null == mFirebaseAnalytics) {
            mFirebaseAnalytics = FirebaseAnalytics.getInstance(context)
        }
        val bundle = Bundle()
        bundle.putString("Category", eventCategory)
        bundle.putString("Action", eventAction)
        mFirebaseAnalytics!!.logEvent("my_events", bundle)

    }

    companion object {
        @JvmStatic
        fun trackEvent() {

        }
    }


}