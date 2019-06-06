package com.aniapps.flicbuzzapp.utils

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.google.android.gms.auth.api.phone.SmsRetriever
import com.google.android.gms.common.api.CommonStatusCodes
import com.google.android.gms.common.api.Status

/**
 * Created by NagRaj on 08-11-2017.
 */

class MySMSBroadcastReceiver : BroadcastReceiver() {
    private var otpReceiver: OTPReceiveListener? = null
    fun initOTPListener(receiver: OTPReceiveListener) {
        this.otpReceiver = receiver
    }

    override fun onReceive(context: Context, intent: Intent) {
        if (SmsRetriever.SMS_RETRIEVED_ACTION == intent.action) {
            val extras = intent.extras
            val status = extras!!.get(SmsRetriever.EXTRA_STATUS) as Status

            when (status.statusCode) {
                CommonStatusCodes.SUCCESS -> {
                    // Get SMS message contents
                    var otp: String = extras.get(SmsRetriever.EXTRA_SMS_MESSAGE) as String

                    // Extract one-time code from the message and complete verification
                    // by sending the code back to your server for SMS authenticity.
                    // But here we are just passing it to MainActivity

                    // my laptop key ozQvyW7XKMd
                    // signed key Mvp+/YJeIPE
                    if (otpReceiver != null) {
                        Log.e("#SMS#", " ORignal OTP" + otp)
                        otp = otp.replace("<#> Your ExampleApp code is: ", "").split("\n".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()[0]

                        Log.e("#SMS#", " Other OTP" + otp)
                        otpReceiver!!.onOTPReceived(otp)
                    }
                }

                CommonStatusCodes.TIMEOUT ->
                    // Waiting for SMS timed out (5 minutes)
                    // Handle the error ...
                    Log.e("#SMS#", "Time out")
                //  otpReceiver!!.onOTPTimeOut()
            }
        }
    }

    interface OTPReceiveListener {

        fun onOTPReceived(otp: String)

        fun onOTPTimeOut()
    }
}