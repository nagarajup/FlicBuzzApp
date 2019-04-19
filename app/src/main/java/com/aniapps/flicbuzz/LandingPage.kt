package com.aniapps.flicbuzz

import android.app.PendingIntent.getActivity
import android.app.SearchManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.SearchView
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import com.aniapps.flicbuzz.adapters.SectionListDataAdapter
import com.aniapps.flicbuzz.models.MyVideos
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
import android.widget.SearchView.OnQueryTextListener as OnQueryTextListener1

class LandingPage : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    internal lateinit var rc_list: RecyclerView
    internal lateinit var myvideos: ArrayList<MyVideos>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
        myvideos = ArrayList()


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
            myvideos.clear()
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

    private var menu: Menu? = null
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        this.menu = menu;
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    private var searchView: SearchView? = null
    private var queryTextListener: OnQueryTextListener1? = null
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        when (item.itemId) {
            R.id.action_search -> {
                menu!!.findItem(R.id.action_language).setVisible(false)
                val searchItem = menu!!.findItem(R.id.action_search) as MenuItem
                val searchManager = getSystemService(Context.SEARCH_SERVICE) as SearchManager

                val manager = getSystemService(Context.SEARCH_SERVICE) as SearchManager

                if (searchItem != null) {
                    searchView = searchItem.getActionView() as SearchView
                }
                if (searchView != null) {
                    searchView!!.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));

                    queryTextListener = object : SearchView.OnQueryTextListener,
                        android.widget.SearchView.OnQueryTextListener {
                        override fun onQueryTextChange(newText: String): Boolean {
                            Log.e("####onQueryTextChange", newText)
                            menu!!.findItem(R.id.action_language).setVisible(false)
                            return true
                        }

                        override fun onQueryTextSubmit(query: String): Boolean {
                            Log.e("####onQueryTextSubmit", query)
                            menu!!.findItem(R.id.action_language).setVisible(true)
                            return true
                        }
                    }
                    searchView!!.setOnCloseListener {
                        menu!!.findItem(R.id.action_language).setVisible(true);

                        if (PrefManager.getIn().language == "hindi") {
                            menu!!.getItem(0).setIcon(ContextCompat.getDrawable(this, R.drawable.icon_language_e))
                        }else{
                            menu!!.getItem(0).setIcon(ContextCompat.getDrawable(this, R.drawable.icon_language_h))
                        }
                        return@setOnCloseListener true }

                }


                /*search.setOnQueryTextListener(object : OnQueryTextListener1 {
                    override fun onQueryTextSubmit(query: String?): Boolean {
                        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.

                        menu!!.findItem(R.id.action_language).setVisible(true)
                    }

                    override fun onQueryTextChange(newText: String?): Boolean {
                        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                    }
                })
                return true*/
            }


            R.id.action_language ->
                if (PrefManager.getIn().language == "hindi") {
                    PrefManager.getIn().language = "english"
                    menu!!.getItem(0).setIcon(ContextCompat.getDrawable(this, R.drawable.icon_language_e))
                    apiCall()
                } else {
                    PrefManager.getIn().language = "hindi"
                    menu!!.getItem(0).setIcon(ContextCompat.getDrawable(this, R.drawable.icon_language_h))
                    apiCall()
                }

            else -> return super.onOptionsItemSelected(item)
        }

        if (PrefManager.getIn().language.equals("hindi")) {
            menu!!.getItem(0).setIcon(ContextCompat.getDrawable(this, R.drawable.icon_language_h))
        } else {
            menu!!.getItem(0).setIcon(ContextCompat.getDrawable(this, R.drawable.icon_language_e))
        }
        return true
    }



    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        // Handle navigation view item clicks here.
        var fragment: Fragment? = null
        val fragmentClass: Class<*>

        when (item.itemId) {
            R.id.nav_camera -> {
                val myintent = Intent(this@LandingPage, com.aniapps.flicbuzz.AboutUs::class.java)
                myintent.putExtra("title", "About Us")
                startActivity(myintent)

                // Toast.makeText(this, "Clicked item about us", Toast.LENGTH_SHORT).show()
            }
            R.id.nav_gallery -> {
                val myintent = Intent(this@LandingPage, com.aniapps.flicbuzz.AboutUs::class.java)
                myintent.putExtra("title", "My Favourites")
                startActivity(myintent)
                //Toast.makeText(this, "Clicked item profile", Toast.LENGTH_SHORT).show()
            }

            R.id.nav_gallery1 -> {
                val myintent = Intent(this@LandingPage, com.aniapps.flicbuzz.AboutUs::class.java)
                myintent.putExtra("title", "My Profile")
                startActivity(myintent)
                //Toast.makeText(this, "Clicked item profile", Toast.LENGTH_SHORT).show()
            }
            R.id.nav_slideshow -> {
                val myintent = Intent(this@LandingPage, com.aniapps.flicbuzz.AboutUs::class.java)
                myintent.putExtra("title", "Packages")
                startActivity(myintent)
                // Toast.makeText(this, "Clicked item fav", Toast.LENGTH_SHORT).show()
            }
            R.id.nav_manage1 -> {
                val myintent = Intent(this@LandingPage, com.aniapps.flicbuzz.AboutUs::class.java)
                myintent.putExtra("title", "Privacy Policy")
                startActivity(myintent)
                // Toast.makeText(this, "Clicked item settings", Toast.LENGTH_SHORT).show()
            }

            R.id.nav_manage -> {
                val myintent = Intent(this@LandingPage, com.aniapps.flicbuzz.AboutUs::class.java)
                myintent.putExtra("title", "Refund and Cancellation")
                startActivity(myintent)
                // Toast.makeText(this, "Clicked item settings", Toast.LENGTH_SHORT).show()
            }


            R.id.nav_share -> {

                Toast.makeText(this, "Clicked item share", Toast.LENGTH_SHORT).show()
            }
            R.id.nav_send -> {
                PrefManager.getIn().setLogin(false);
                val intent = Intent(this@LandingPage, SignIn::class.java)
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

        return params
    }

    private fun apiCall() {
        RetrofitClient.getInstance()
            .doBackProcess(this@LandingPage, getParams2(), "", object : APIResponse {
                override fun onSuccess(res: String?) {
                    try {
                        val jobj = JSONObject(res)
                        val status = jobj.getInt("status")
                        val details = jobj.getString("details")

                        if (status == 1) {
                            Log.e("RES", res)
                            val jsonArray = jobj.getJSONArray("data")
                            Log.e("RES my Array", "" + jsonArray.length())
                            myData(jsonArray.toString())

                            val my_recycler_view = findViewById<View>(R.id.my_recyclerview) as RecyclerView

                            my_recycler_view.setHasFixedSize(true)

                            val adapter = SectionListDataAdapter(this@LandingPage, myvideos, "main")
                            my_recycler_view.layoutManager =
                                LinearLayoutManager(this@LandingPage, LinearLayoutManager.VERTICAL, false)
                            my_recycler_view.adapter = adapter
                            /* val i = Intent(this@LandingPage, LandingPage::class.java)
                             i.putExtra("jsonArray", jsonArray.toString());
                             startActivity(i)*/

                        } else {
                            Toast.makeText(this@LandingPage, "status" + status, Toast.LENGTH_LONG).show()
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }

                override fun onFailure(res: String?) {
                    Toast.makeText(this@LandingPage, "status" + res, Toast.LENGTH_LONG).show()
                }
            })
    }

}