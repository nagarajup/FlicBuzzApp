package com.aniapps.flicbuzz

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import android.widget.TextView

class AboutUs : AppCompatActivity() {
    lateinit var tv_title:TextView;
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.frgment_about)
        setSupportActionBar(findViewById(R.id.mytoolbar));
        getSupportActionBar()!!.setDisplayHomeAsUpEnabled(true);
        title = intent.getStringExtra("title")

        getSupportActionBar()!!.setTitle(title)
        tv_title=findViewById(R.id.tv_frgament_title)
        tv_title.setText(title)

    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        finish();
        return super.onOptionsItemSelected(item)
    }
}

