package com.aniapps.flicbuzzapp.activities

import android.graphics.Bitmap
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.MenuItem
import android.view.View
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.TextView
import com.aniapps.flicbuzzapp.R
import com.aniapps.flicbuzzapp.utils.FlickLoading
import com.aniapps.flicbuzzapp.utils.Utility
import com.aniapps.flicbuzzapp.utils.Utility.isConnectingToInternet

class AboutUs : AppCompatActivity() {
    private lateinit var webview: WebView
    private lateinit var tv_note: TextView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.frgment_about)
        val mToolbar = findViewById<View>(R.id.toolbar) as Toolbar
        val header_title = findViewById<View>(R.id.title) as TextView

        setSupportActionBar(mToolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        title = intent.getStringExtra("title")

        header_title.text = title
        webview = findViewById<WebView>(R.id.web_page)
        tv_note = findViewById<TextView>(R.id.tv_note)

        if (intent.getStringExtra("url").equals(""))
        {
            tv_note.visibility = View.VISIBLE
            webview.visibility = View.GONE
            if(title.equals("About FlicBuzz")){
                tv_note.setText("     The Flicbuzz app for android, source content for celebrity gossip, fitness tips,yoga and lifestyle, food recipes and other interesting content that we may keep adding. Flicbuzz is owned and operated by Tencom Ventures Pvt Ltd.\n" +
                        "\n" +
                        "    Our team works round the clock to bring readers the latest stories, photos and nes from the popular celebrities in India and also if possible international celebrities. We publishes Filmy news, gossip, rumors and speculation.")
            }
        } else {
            tv_note.visibility = View.GONE
            webview.visibility = View.VISIBLE
            loadWeb()
        }


    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        finish();
        overridePendingTransition(R.anim.right_slide_in, R.anim.right_slide_out)
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

