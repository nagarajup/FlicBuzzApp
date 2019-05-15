package com.aniapps.flicbuzzapp.activities

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.Button
import android.widget.TextView
import com.aniapps.flicbuzzapp.AppConstants
import com.aniapps.flicbuzzapp.R


class SignIn : AppConstants() {
    internal lateinit var btn_login: TextView
    internal lateinit var btn_signup: Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signin)
        btn_login = findViewById<View>(R.id.loginTV) as TextView
        btn_signup = findViewById<View>(R.id.btn_signup) as Button
        btn_login.setOnClickListener {
            trackEvent(this@SignIn, "SignIn", "Login")
             val i = Intent(this@SignIn, LoginActivity::class.java)
              startActivity(i)
            overridePendingTransition(R.anim.left_slide_in, R.anim.left_slide_out)

        }

        btn_signup.setOnClickListener {
            trackEvent(this@SignIn, "SignIn", "SignUp")
            val i = Intent(this@SignIn, SignUpActivity::class.java)
            startActivity(i)
            overridePendingTransition(R.anim.left_slide_in, R.anim.left_slide_out)

        }
    }











}