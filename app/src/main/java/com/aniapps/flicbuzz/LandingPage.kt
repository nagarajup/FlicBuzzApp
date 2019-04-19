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
import android.widget.ProgressBar
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
    internal var loading = false
    internal var scrollFlag = false
    internal var layoutManager: LinearLayoutManager? = null
    internal var total_records = ""
    private var pbr: ProgressBar? = null

    internal var pageNo = 1
    internal var my_recycler_view: RecyclerView? = null
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

        my_recycler_view = findViewById<View>(R.id.my_recyclerview) as RecyclerView
        pbr = findViewById(R.id.load_progress) as ProgressBar
        pbr!!.getIndeterminateDrawable().setColorFilter(
            ContextCompat.getColor(this, R.color.colorAccent),
            android.graphics.PorterDuff.Mode.MULTIPLY
        )
        myvideos.clear()
        apiCall(pageNo, total_records)

        my_recycler_view!!.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
            }

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                Log.e("####", "scroll 1")
                if (!loading && myvideos.size > 0 && !scrollFlag) {
                    try {

                        val visibleItemCount = layoutManager!!.getChildCount()
                        val totalItemCount = layoutManager!!.getItemCount()
                        val firstVisibleItem = layoutManager!!.findFirstVisibleItemPosition()
                        Log.e("####", "scroll 2")
                        if (visibleItemCount + firstVisibleItem >= totalItemCount) {
                            /*if (totalItemCount < Integer.parseInt(total_records)) {*/
                            pageNo++
                            apiCall(pageNo, total_records)
//                            }
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }
        })

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
                    apiCall(pageNo, total_records)
                } else {
                    PrefManager.getIn().language = "hindi"
                    menu!!.getItem(0).setIcon(ContextCompat.getDrawable(this, R.drawable.icon_language_h))
                    apiCall(pageNo, total_records)
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

 /*   private fun getParams2(): Map<String, String> {
        val params = HashMap<String, String>()
        params["action"] = "home2"
        params["plan"] = "free"
        params["page_number"] = "1"

        return params
    }
*/
    internal lateinit var adapter: SectionListDataAdapter
    private fun apiCall(pageno: Int, records: String) {
        loading = true
        var from = "" as String
        val params = HashMap<String, String>()
        params["action"] = "home2"
        params["plan"] = "free"
        params["page_number"] = "" + pageno
        if (pageno == 1) {
            from = "";
        } else {
            from = "online"
            pbr!!.visibility = View.VISIBLE
        }
        RetrofitClient.getInstance()
            .doBackProcess(this@LandingPage, params, from, object : APIResponse {
                override fun onSuccess(res: String?) {
                    try {
                        val jobj = JSONObject(res)
                        val status = jobj.getInt("status")
                        val details = jobj.getString("details")

                        if (status == 1) {
                            loading = false
                            Log.e("RES", res)
                            val jsonArray = jobj.getJSONArray("data")
                            Log.e("RES my Array", "" + jsonArray.length())
                            myData(jsonArray.toString())
                            if (myvideos.size < 20) {
                                scrollFlag = true
                            }
                            if (pageNo == 1) {
                                adapter = SectionListDataAdapter(this@LandingPage, myvideos, "main")

                                layoutManager = LinearLayoutManager(applicationContext)
                                my_recycler_view!!.setLayoutManager(layoutManager)

//                                my_recycler_view!!.layoutManager =
//                                        LinearLayoutManager(this@LandingPage, LinearLayoutManager.VERTICAL, false)
                                adapter.notifyDataSetChanged()
                                my_recycler_view!!.adapter = adapter

                                /* val i = Intent(this@LandingPage, LandingPage::class.java)
                             i.putExtra("jsonArray", jsonArray.toString());
                             startActivity(i)*/

                            } else {
                                adapter.notifyDataSetChanged()
                            }

                        } else {
                            Toast.makeText(this@LandingPage, "status" + status, Toast.LENGTH_LONG).show()
                        }
                        if (pbr!!.visibility == View.VISIBLE) {
                            pbr!!.visibility = View.GONE
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                        if (pbr!!.visibility == View.VISIBLE) {
                            pbr!!.visibility = View.GONE
                        }
                    }
                }

                override fun onFailure(res: String?) {
                    Toast.makeText(this@LandingPage, "status" + res, Toast.LENGTH_LONG).show()
                    if (pbr!!.visibility == View.VISIBLE) {
                        pbr!!.visibility = View.GONE
                    }
                }
            })
    }

}