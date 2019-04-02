package com.aniapps.flicbuzz

import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.design.widget.NavigationView
import android.support.v4.app.Fragment
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import com.aniapps.flicbuzz.adapters.ListingAdapter
import com.aniapps.flicbuzz.fragments.AboutUs
import com.aniapps.flicbuzz.models.SectionDataModel
import com.aniapps.flicbuzz.models.SingleItemModel
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.app_bar_main.*
import java.util.ArrayList
import android.R.attr.fragment
import android.content.Intent
import com.mad.kotlin_navigation_drawer.Fragment1
import com.mad.kotlin_navigation_drawer.replaceFragmenty


class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    internal lateinit var allSampleData: ArrayList<SectionDataModel>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
        allSampleData = ArrayList<SectionDataModel>()
/*
        fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
        }*/


        createDummyData()
        val my_recycler_view = findViewById<View>(R.id.my_recyclerview) as RecyclerView
        my_recycler_view.setHasFixedSize(true)
        val adapter = ListingAdapter(this, allSampleData)
        my_recycler_view.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        my_recycler_view.adapter = adapter
        val toggle = ActionBarDrawerToggle(
            this, drawer_layout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close
        )
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()

        nav_view.setNavigationItemSelectedListener(this)
    }

    fun createDummyData() {
        val myCategories = resources.getStringArray(R.array.Topics)
        val myUrls = resources.getStringArray(R.array.Urls)
        for (i in myCategories.indices) {
            val singleItem = ArrayList<SingleItemModel>()

            for (j in 0..5) {
                singleItem.add(SingleItemModel("Title $j", myUrls[i], ""))
            }
            val dm = SectionDataModel(myCategories[i], singleItem)
            allSampleData.add(dm)

        }
    }

    override fun onBackPressed() {
        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        when (item.itemId) {
            R.id.action_settings -> return true
            else -> return super.onOptionsItemSelected(item)
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        // Handle navigation view item clicks here.
        var fragment: Fragment? = null
        val fragmentClass: Class<*>

        when (item.itemId) {
            R.id.nav_camera -> {
                val myintent = Intent(this@MainActivity, com.aniapps.flicbuzz.AboutUs::class.java)
                myintent.putExtra("title","About Us")
                startActivity(myintent)

               // Toast.makeText(this, "Clicked item about us", Toast.LENGTH_SHORT).show()
            }
            R.id.nav_gallery -> {
                val myintent = Intent(this@MainActivity, com.aniapps.flicbuzz.AboutUs::class.java)
                myintent.putExtra("title","My Favourites")
                startActivity(myintent)
               //Toast.makeText(this, "Clicked item profile", Toast.LENGTH_SHORT).show()
            }

            R.id.nav_gallery1 -> {
                val myintent = Intent(this@MainActivity, com.aniapps.flicbuzz.AboutUs::class.java)
                myintent.putExtra("title","My Profile")
                startActivity(myintent)
                //Toast.makeText(this, "Clicked item profile", Toast.LENGTH_SHORT).show()
            }
            R.id.nav_slideshow -> {
                val myintent = Intent(this@MainActivity, com.aniapps.flicbuzz.AboutUs::class.java)
                myintent.putExtra("title","Packages")
                startActivity(myintent)
               // Toast.makeText(this, "Clicked item fav", Toast.LENGTH_SHORT).show()
            }
            R.id.nav_manage1 -> {
                val myintent = Intent(this@MainActivity, com.aniapps.flicbuzz.AboutUs::class.java)
                myintent.putExtra("title","Privacy Policy")
                startActivity(myintent)
               // Toast.makeText(this, "Clicked item settings", Toast.LENGTH_SHORT).show()
            }

            R.id.nav_manage -> {
                val myintent = Intent(this@MainActivity, com.aniapps.flicbuzz.AboutUs::class.java)
                myintent.putExtra("title","Refund and Cancellation")
                startActivity(myintent)
                // Toast.makeText(this, "Clicked item settings", Toast.LENGTH_SHORT).show()
            }




            R.id.nav_share -> {

              Toast.makeText(this, "Clicked item share", Toast.LENGTH_SHORT).show()
            }
            R.id.nav_send -> {
                Toast.makeText(this, "Clicked item send", Toast.LENGTH_SHORT).show()
            }

        }
        drawer_layout.closeDrawer(GravityCompat.START)
        return true
    }
}
