package com.aniapps.flicbuzz

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.Button
import android.widget.TextView

class Login : AppCompatActivity() {
    internal lateinit var btn_login: Button
    internal lateinit var btn_signup: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login_new)
        btn_login = findViewById<View>(R.id.login_btn) as Button
        btn_signup = findViewById<View>(R.id.signUpTV) as TextView
        btn_signup.setOnClickListener {
            val i = Intent(this@Login, SignUp::class.java)
            startActivity(i)
        }

    }
}