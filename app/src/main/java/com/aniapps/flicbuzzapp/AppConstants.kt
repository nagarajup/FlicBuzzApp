package com.aniapps.flicbuzzapp

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.aniapps.flicbuzzapp.utils.PrefManager
import com.appsflyer.AFInAppEventType
import com.appsflyer.AppsFlyerLib
import com.appsflyer.AFInAppEventParameterName


@SuppressLint("Registered")
open class AppConstants : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        AppsFlyerLib.getInstance().setCustomerUserId(PrefManager.getIn().getMobile());

    }

    /*https://support.appsflyer.com/hc/en-us/articles/207032126-AppsFlyer-SDK-Integration-Android*/
    /*https://support.appsflyer.com/hc/en-us/articles/208386173-AppsFlyer-Android-Sample-App*/
    fun trackEvent(context: Activity, eventCategory: String, eventAction: String) {
        val eventValue = HashMap<String, Any>()
        eventValue["category"] = eventCategory
        eventValue["action"] = eventAction
        eventValue["user_id"] = PrefManager.getIn().getMobile()
        AppsFlyerLib.getInstance().trackEvent(context, "my_events", eventValue)

        /* CustomDataMap.put("custom_param_1", "value_of_param_1")
         AppsFlyerLib.getInstance().setAdditionalData(CustomDataMap)*/

    }
}