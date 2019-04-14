package com.aniapps.flicbuzz

import android.content.Intent
import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.v4.app.Fragment
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.Toast
import com.aniapps.flicbuzz.adapters.SectionListDataAdapter
import com.aniapps.flicbuzz.models.MyVideos
import com.aniapps.flicbuzz.models.SectionDataModel
import com.aniapps.flicbuzz.networkcall.APIResponse
import com.aniapps.flicbuzz.networkcall.RetrofitClient
import com.aniapps.flicbuzz.utils.PrefManager
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.app_bar_main.*
import org.json.JSONArray
import org.json.JSONObject
import java.lang.Exception
import java.util.ArrayList

class MyPlaerList : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    internal lateinit var rc_list: RecyclerView
    internal lateinit var myvideos: ArrayList<MyVideos>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
        myvideos = ArrayList()
       // val jsonArray = intent.getStringExtra("jsonArray")
        //myData(jsonArray)

        val toggle = ActionBarDrawerToggle(
            this, drawer_layout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close
        )
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()

        nav_view.setNavigationItemSelectedListener(this)
        apiCall()

    }

    fun myData(myData: String) {
        try {
            val array = JSONArray(myData)
            for (i in 0 until array.length()) {
                var lead = Gson().fromJson(
                    array.get(i).toString(),
                    MyVideos::class.java
                )
                myvideos.add(lead)
            }
        } catch (e: Exception) {
            e.printStackTrace()
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
            R.id.action_search -> return true
            else -> return super.onOptionsItemSelected(item)
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        // Handle navigation view item clicks here.
        var fragment: Fragment? = null
        val fragmentClass: Class<*>

        when (item.itemId) {
            R.id.nav_camera -> {
                val myintent = Intent(this@MyPlaerList, com.aniapps.flicbuzz.AboutUs::class.java)
                myintent.putExtra("title","About Us")
                startActivity(myintent)

                // Toast.makeText(this, "Clicked item about us", Toast.LENGTH_SHORT).show()
            }
            R.id.nav_gallery -> {
                val myintent = Intent(this@MyPlaerList, com.aniapps.flicbuzz.AboutUs::class.java)
                myintent.putExtra("title","My Favourites")
                startActivity(myintent)
                //Toast.makeText(this, "Clicked item profile", Toast.LENGTH_SHORT).show()
            }

            R.id.nav_gallery1 -> {
                val myintent = Intent(this@MyPlaerList, com.aniapps.flicbuzz.AboutUs::class.java)
                myintent.putExtra("title","My Profile")
                startActivity(myintent)
                //Toast.makeText(this, "Clicked item profile", Toast.LENGTH_SHORT).show()
            }
            R.id.nav_slideshow -> {
                val myintent = Intent(this@MyPlaerList, com.aniapps.flicbuzz.AboutUs::class.java)
                myintent.putExtra("title","Packages")
                startActivity(myintent)
                // Toast.makeText(this, "Clicked item fav", Toast.LENGTH_SHORT).show()
            }
            R.id.nav_manage1 -> {
                val myintent = Intent(this@MyPlaerList, com.aniapps.flicbuzz.AboutUs::class.java)
                myintent.putExtra("title","Privacy Policy")
                startActivity(myintent)
                // Toast.makeText(this, "Clicked item settings", Toast.LENGTH_SHORT).show()
            }

            R.id.nav_manage -> {
                val myintent = Intent(this@MyPlaerList, com.aniapps.flicbuzz.AboutUs::class.java)
                myintent.putExtra("title","Refund and Cancellation")
                startActivity(myintent)
                // Toast.makeText(this, "Clicked item settings", Toast.LENGTH_SHORT).show()
            }




            R.id.nav_share -> {

                Toast.makeText(this, "Clicked item share", Toast.LENGTH_SHORT).show()
            }
            R.id.nav_send -> {
                PrefManager.getIn().setLogin(false);
                val intent = Intent(this@MyPlaerList, SignIn::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                startActivity(intent)
            }

        }
        drawer_layout.closeDrawer(GravityCompat.START)
        return true
    }
    private fun getParams2(): Map<String, String> {
        val params = HashMap<String, String>()
        params["action"] = "home2"
        params["plan"] = "free"
        params["page_number"] = "1"
        params["device_name"] = "abcd"
        return params
    }

    private fun apiCall() {
        RetrofitClient.getInstance()
            .doBackProcess(this@MyPlaerList, getParams2(), "", object : APIResponse {
                override fun onSuccess(res: String?) {
                    try {
                        val jobj = JSONObject(res)
                        val status = jobj.getInt("status")
                        val details = jobj.getString("details")

                        if (status == 1) {
                            Log.e("RES", res)
                            val jsonArray = jobj.getJSONArray("data")
                            Log.e("RES my Array",""+jsonArray.length())
                            myData(jsonArray.toString())
                            val my_recycler_view = findViewById<View>(R.id.my_recyclerview) as RecyclerView
                            my_recycler_view.setHasFixedSize(true)
                            val adapter = SectionListDataAdapter(this@MyPlaerList, myvideos,"main")
                            my_recycler_view.layoutManager = LinearLayoutManager(this@MyPlaerList, LinearLayoutManager.VERTICAL, false)
                            my_recycler_view.adapter = adapter
                           /* val i = Intent(this@MyPlaerList, MyPlaerList::class.java)
                            i.putExtra("jsonArray", jsonArray.toString());
                            startActivity(i)*/

                        } else {
                            Toast.makeText(this@MyPlaerList, "status" + status, Toast.LENGTH_LONG).show()
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }

                override fun onFailure(res: String?) {
                    Toast.makeText(this@MyPlaerList, "status" + res, Toast.LENGTH_LONG).show()
                }
            })
    }

}