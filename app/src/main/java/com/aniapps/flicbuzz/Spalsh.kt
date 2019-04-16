package com.aniapps.flicbuzz

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.support.v7.app.AppCompatActivity
import com.aniapps.flicbuzz.activities.IntroductionScreen
import com.aniapps.flicbuzz.utils.PrefManager

class Spalsh : AppCompatActivity() {
    private var t: Thread? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        Handler().postDelayed({
            if(PrefManager.getIn().login) {
                val i = Intent(this@Spalsh, LandingPage::class.java)
                startActivity(i)
            }else{
                val i = Intent(this@Spalsh, IntroductionScreen::class.java)
                startActivity(i)
            }

            finish()
        }, 2000)

    }

}
