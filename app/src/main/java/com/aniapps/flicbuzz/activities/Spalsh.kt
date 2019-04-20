package com.aniapps.flicbuzz.activities

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.support.v7.app.AppCompatActivity
import com.aniapps.flicbuzz.player.LandingPage
import com.aniapps.flicbuzz.R
import com.aniapps.flicbuzz.utils.PrefManager

class Spalsh : AppCompatActivity() {
    private var t: Thread? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        Handler().postDelayed({
            if(PrefManager.getIn().login ) {
                if (PrefManager.getIn().getPlan() == "3" || PrefManager.getIn().getPlan() == "6" || PrefManager.getIn().getPlan() == "12") {
                    val intent = Intent(this@Spalsh, LandingPage::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                    startActivity(intent)
                } else {
                    val intent = Intent(this@Spalsh, PaymentScreen_New::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                    startActivity(intent)
                }
            }else{
                val i = Intent(this@Spalsh, SignIn::class.java)
                startActivity(i)
            }

            finish()
        }, 2000)

    }

}
