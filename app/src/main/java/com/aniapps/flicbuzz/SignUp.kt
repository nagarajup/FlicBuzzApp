package com.aniapps.flicbuzz

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.Button

class SignUp : AppCompatActivity() {
    internal lateinit var signup_btn_main: Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)
        signup_btn_main=findViewById<View>(R.id.signup_btn) as Button
        signup_btn_main.setOnClickListener {
            val i = Intent(this@SignUp, Login::class.java)
            startActivity(i)
        }
    }
}