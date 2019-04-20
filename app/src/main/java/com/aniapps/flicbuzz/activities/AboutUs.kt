package com.aniapps.flicbuzz.activities

import android.app.Activity
import android.graphics.Bitmap
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import android.view.View
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.TextView
import com.aniapps.flicbuzz.R
import com.aniapps.flicbuzz.utils.FlickLoading
import com.aniapps.flicbuzz.utils.Utility
import com.aniapps.flicbuzz.utils.Utility.isConnectingToInternet

class AboutUs : AppCompatActivity() {
    private lateinit var webview: WebView
    private lateinit var tv_note: TextView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.frgment_about)
        setSupportActionBar(findViewById(R.id.mytoolbar));
        getSupportActionBar()!!.setDisplayHomeAsUpEnabled(true);
        title = intent.getStringExtra("title")

        getSupportActionBar()!!.setTitle(title)
        webview = findViewById<WebView>(R.id.web_page)
        tv_note = findViewById<TextView>(R.id.tv_note)

        if (intent.getStringExtra("url").equals("")) {
            tv_note.visibility = View.VISIBLE
            webview.visibility = View.GONE
        } else {
            tv_note.visibility = View.GONE
            webview.visibility = View.VISIBLE
            loadWeb()
        }


    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        finish();
        return super.onOptionsItemSelected(item)
    }

    fun loadWeb() {
        val settings = webview.getSettings()
        settings.javaScriptEnabled = true
        webview.getSettings().javaScriptEnabled = true
        webview.getSettings().pluginState = WebSettings.PluginState.ON
        webview.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY)
        webview.webViewClient = object : WebViewClient() {

            override fun onPageStarted(view: WebView, url: String, favicon: Bitmap?) {

                try {
                    runOnUiThread { FlickLoading.getInstance().show(this@AboutUs) }
                } catch (e: Exception) {
                    e.printStackTrace()
                }

                super.onPageStarted(view, url, favicon)
            }

            override fun onPageFinished(view: WebView, url: String) {
                try {
                    runOnUiThread { FlickLoading.getInstance().dismiss(this@AboutUs) }
                } catch (e: Exception) {
                    e.printStackTrace()
                }



                super.onPageFinished(view, url)
            }

            override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
                return true
            }


        }

        try {
            if (!isConnectingToInternet(this@AboutUs)) {
                if (!isFinishing) {
                    Utility.alertDialog(
                        this@AboutUs,
                        "No Internet Connection",
                        "Please check your internet connectivity and try again!"
                    )
                }
            } else {
                webview.loadUrl(intent.getStringExtra("url"))
            }

        } catch (e: IllegalArgumentException) {
            e.printStackTrace()
        }
    }
}

