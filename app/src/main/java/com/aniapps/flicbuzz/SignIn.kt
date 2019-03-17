package com.aniapps.flicbuzz

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.Button

class SignIn : AppCompatActivity() {
    internal lateinit var btn_login: Button
    internal lateinit var btn_signup:Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signin)
        btn_login = findViewById<View>(R.id.btn_login) as Button
        btn_signup = findViewById<View>(R.id.btn_signup) as Button
        btn_login.setOnClickListener {
            val i = Intent(this@SignIn, Login::class.java)
            startActivity(i)
        }

        btn_signup.setOnClickListener {
            val i = Intent(this@SignIn, MainActivity::class.java)
            startActivity(i)
        }
    }
}