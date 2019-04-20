package com.aniapps.flicbuzz.player

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.support.design.widget.NavigationView
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.support.v4.view.GravityCompat
import android.support.v4.view.MenuItemCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.SearchView
import android.support.v7.widget.SwitchCompat
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.*
import com.aniapps.flicbuzz.activities.AboutUs
import com.aniapps.flicbuzz.R
import com.aniapps.flicbuzz.activities.PaymentScreen_New
import com.aniapps.flicbuzz.activities.SignIn
import com.aniapps.flicbuzz.adapters.AutoSuggestAdapter
import com.aniapps.flicbuzz.adapters.MainAdapter
import com.aniapps.flicbuzz.adapters.SearchAdapter
import com.aniapps.flicbuzz.models.MyVideos
import com.aniapps.flicbuzz.models.SearchData
import com.aniapps.flicbuzz.networkcall.APIResponse
import com.aniapps.flicbuzz.networkcall.RetrofitClient
import com.aniapps.flicbuzz.utils.PrefManager
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.app_bar_main.*
import org.json.JSONArray
import org.json.JSONObject
import org.w3c.dom.Text
import java.lang.Exception
import java.util.ArrayList
import android.widget.SearchView.OnQueryTextListener as OnQueryTextListener1

class LandingPage : AppCompatActivity(), View.OnClickListener {


    internal lateinit var rc_list: RecyclerView
    internal lateinit var myvideos: ArrayList<MyVideos>
    internal var loading = false
    internal var scrollFlag = false
    internal var layoutManager: LinearLayoutManager? = null
    internal var total_records = ""
    private var pbr: ProgressBar? = null
    internal lateinit var nav_about: TextView
    internal lateinit var nav_profile: TextView
    internal lateinit var nav_privacy: TextView
    internal lateinit var nav_fav: TextView
    internal lateinit var nav_refund: TextView
    internal lateinit var nav_package: TextView
    internal lateinit var nav_share: TextView
    internal lateinit var nav_terms: TextView
    internal lateinit var nav_feedback: TextView
    internal lateinit var nav_settings: TextView
    internal lateinit var nav_logout: TextView
    internal lateinit var tv_profile_name: TextView
    internal lateinit var tv_profile_email: TextView
    internal lateinit var tv_profile_plan: TextView
    internal lateinit var switchCompat: SwitchCompat

    internal var pageNo = 1
    internal var my_recycler_view: RecyclerView? = null
    internal lateinit var search_list: ListView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
        myvideos = ArrayList()
        // val jsonArray = intent.getStringExtra("jsonArray")
        //myData(jsonArray)

       // Toast.makeText(this, PrefManager.getIn().getPayment_data().toString(), Toast.LENGTH_SHORT).show()
        val toggle = ActionBarDrawerToggle(
            this, drawer_layout, toolbar,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        )
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()

//        nav_view.setNavigationItemSelectedListener(this)

        my_recycler_view = findViewById<View>(R.id.my_recyclerview) as RecyclerView
        search_list = findViewById<ListView>(R.id.search_list)

       search_list.setOnItemClickListener { adapterView, view, i, l ->
           search_list.visibility = View.GONE
           searchEditText.setText(mySearchData.get(i).name)
           Toast.makeText(this@LandingPage,"ID: "+mySearchData.get(i).search_id,Toast.LENGTH_SHORT).show()
       }
        tv_profile_name = findViewById<TextView>(R.id.tv_profile_name);
        tv_profile_email = findViewById<TextView>(R.id.tv_profile_email);
        tv_profile_plan = findViewById<TextView>(R.id.tv_profile_plan);
        nav_about = findViewById<TextView>(R.id.nav_about);
        nav_profile = findViewById<TextView>(R.id.nav_profile);
        nav_privacy = findViewById<TextView>(R.id.nav_privacy);
        nav_fav = findViewById<TextView>(R.id.nav_fav);
        nav_refund = findViewById<TextView>(R.id.nav_refund);
        nav_package = findViewById<TextView>(R.id.nav_package);
        nav_share = findViewById<TextView>(R.id.nav_share);
        nav_terms = findViewById<TextView>(R.id.nav_terms);
        nav_feedback = findViewById<TextView>(R.id.nav_contact);
        nav_settings = findViewById<TextView>(R.id.nav_settings);
        nav_logout = findViewById<TextView>(R.id.nav_logout);
        nav_about.setOnClickListener(this@LandingPage)
        nav_profile.setOnClickListener(this@LandingPage)
        nav_privacy.setOnClickListener(this@LandingPage)
        nav_fav.setOnClickListener(this@LandingPage)
        nav_refund.setOnClickListener(this@LandingPage)
        nav_package.setOnClickListener(this@LandingPage)
        nav_share.setOnClickListener(this@LandingPage)
        nav_terms.setOnClickListener(this@LandingPage)
        nav_feedback.setOnClickListener(this@LandingPage)
        nav_settings.setOnClickListener(this@LandingPage)
        nav_logout.setOnClickListener(this@LandingPage)
        switchCompat = findViewById<SwitchCompat>(R.id.nav_language)
        tv_profile_name.setText(PrefManager.getIn().getName())
        tv_profile_email.setText(PrefManager.getIn().getEmail())
        if(PrefManager.getIn().getPlan().equals("3")) {
            tv_profile_plan.setText("Plan : Three Months")
        }else   if(PrefManager.getIn().getPlan().equals("6")) {
            tv_profile_plan.setText("Plan : Six Months")
        }else   if(PrefManager.getIn().getPlan().equals("12")) {
            tv_profile_plan.setText("Plan : One Year")
        }
        if(PrefManager.getIn().language.equals("Hindi")){
            switchCompat.isChecked=false
        }else {
            switchCompat.isChecked=true
        }
        switchCompat.text = "Language: " + PrefManager.getIn().language
        switchCompat.setOnCheckedChangeListener({ _, isChecked ->
            PrefManager.getIn().language = if (isChecked) "English" else "Hindi"
            if (isChecked) {
                menu!!.getItem(0).setIcon(ContextCompat.getDrawable(this, R.mipmap.icon_language_e))
            } else {
                menu!!.getItem(0).setIcon(ContextCompat.getDrawable(this, R.mipmap.icon_language_h))
            }
            pageNo = 1
            apiCall()
            switchCompat.text = "Language: " + PrefManager.getIn().language
            drawer_layout.closeDrawer(GravityCompat.START)
        })

        pbr = findViewById(R.id.load_progress) as ProgressBar
        pbr!!.getIndeterminateDrawable().setColorFilter(
            ContextCompat.getColor(this, R.color.colorAccent),
            android.graphics.PorterDuff.Mode.MULTIPLY
        )
        myvideos.clear()
        apiCall()

        my_recycler_view!!.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
            }

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (!loading && myvideos.size > 0 && !scrollFlag) {
                    try {
                        val visibleItemCount = layoutManager!!.getChildCount()
                        val totalItemCount = layoutManager!!.getItemCount()
                        val firstVisibleItem = layoutManager!!.findFirstVisibleItemPosition()
                        if (visibleItemCount + firstVisibleItem >= totalItemCount) {
                            pageNo++
                            apiCall()
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }
        })

    }

    internal lateinit var search: android.widget.SearchView

    override fun onResume() {
        super.onResume()

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
    internal lateinit var searchEditText: EditText
    internal lateinit var menuView: FrameLayout
    internal lateinit var clear: ImageView
    internal lateinit var menuItem: MenuItem
    private val searchView: SearchView? = null
    private val mSearchAutoComplete: SearchView.SearchAutoComplete? = null
    internal lateinit var autoSuggestAdapter: AutoSuggestAdapter
    internal lateinit var mHighlightArrayAdapter: SearchAdapter
    internal lateinit var handler: Handler
    private val TRIGGER_AUTO_COMPLETE = 100
    private val AUTO_COMPLETE_DELAY: Long = 300
    lateinit var searchFilters: ArrayList<String>
    lateinit var mySearchData: ArrayList<SearchData>
    @SuppressLint("RestrictedApi")

    /*override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        this.menu = menu;
        menuInflater.inflate(R.menu.main, menu)
        val item = menu!!.findItem(R.id.action_search)
        val searchView = MenuItemCompat.getActionView(item) as SearchView
        val mSearchAutoComplete =
            searchView.findViewById(android.support.v7.appcompat.R.id.search_src_text) as SearchView.SearchAutoComplete
        autoSuggestAdapter = AutoSuggestAdapter(
            this,
            android.R.layout.simple_dropdown_item_1line
        )

        mSearchAutoComplete.setDropDownBackgroundResource(R.color.white);
        // mSearchAutoComplete.setDropDownAnchor(R.id.action_search);
        mSearchAutoComplete.setThreshold(0)
        mSearchAutoComplete.setAdapter(autoSuggestAdapter)
        mSearchAutoComplete.setOnItemClickListener(
            AdapterView.OnItemClickListener { parent, view, position, id ->
                Log.e(
                    "@@@", "" +
                            autoSuggestAdapter.getObject(
                                position
                            )
                )
                mSearchAutoComplete.setText(autoSuggestAdapter.getObject(position))
            })

        mSearchAutoComplete.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                handler.removeMessages(TRIGGER_AUTO_COMPLETE)
                handler.sendEmptyMessageDelayed(
                    TRIGGER_AUTO_COMPLETE,
                    AUTO_COMPLETE_DELAY
                )
            }

            override fun afterTextChanged(s: Editable) {

            }
        })
        handler = Handler(Handler.Callback { msg ->
            if (msg.what == TRIGGER_AUTO_COMPLETE) {
                if (!TextUtils.isEmpty(mSearchAutoComplete.getText())) {
                    makeApiCall(mSearchAutoComplete.getText().toString())
                }
            }
            false
        })


        *//* val searchItems=ArrayList<String>()
         searchItems.add("asdfsdf")
         searchItems.add("werw")
         searchItems.add("fsd")
         searchItems.add("erer")
         searchItems.add("asdf")
         searchItems.add("thyr")
        val adapter = ArrayAdapter<String>(this@LandingPage, android.R.layout.simple_list_item_1, searchItems);
         mSearchAutoComplete.setAdapter(adapter);*//*

        return true


    }*/


    override fun onCreateOptionsMenu(menu: Menu): Boolean {


        this.menu = menu;
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main, menu)

        val menuItem = menu.findItem(R.id.action_search)
        autoSuggestAdapter = AutoSuggestAdapter(
            this,
            android.R.layout.simple_dropdown_item_1line
        )
        searchFilters = ArrayList();
        mHighlightArrayAdapter = SearchAdapter(
            this@LandingPage,
            R.layout.serach_item,
            R.id.product_name,
            searchFilters
        )

        search_list!!.adapter = mHighlightArrayAdapter;
        //  search_list!!.adapter=autoSuggestAdapter
        searchEditText = MenuItemCompat.getActionView(menuItem).findViewById(R.id.edittext) as EditText
        menuView = MenuItemCompat.getActionView(menuItem).findViewById(R.id.menu_view) as FrameLayout
        clear = MenuItemCompat.getActionView(menuItem).findViewById(R.id.clear) as ImageView
        clear.setOnClickListener {
            searchEditText.setText("")
            search_list!!.visibility = View.GONE
            mHighlightArrayAdapter.notifyDataSetChanged()
            MenuItemCompat.collapseActionView(menuItem)
        }

        searchEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                handler.removeMessages(TRIGGER_AUTO_COMPLETE)
                handler.sendEmptyMessageDelayed(
                    TRIGGER_AUTO_COMPLETE,
                    AUTO_COMPLETE_DELAY
                )
                if (s.length != 0) {
                    searchEditText.setCompoundDrawables(null, null, null, null)
                    clear.visibility = View.VISIBLE

                } else {
                    searchEditText.setCompoundDrawablesWithIntrinsicBounds(R.mipmap.icon_search_edit, 0, 0, 0)
                    clear.visibility = View.GONE
                    mHighlightArrayAdapter.notifyDataSetChanged()
                }
            }

            override fun afterTextChanged(s: Editable) {

            }
        })

        handler = Handler(Handler.Callback { msg ->
            if (msg.what == TRIGGER_AUTO_COMPLETE) {
                if (!TextUtils.isEmpty(searchEditText.getText())) {
                    makeApiCall(searchEditText.getText().toString())
                }
            }
            false
        })
        menuItem.setOnActionExpandListener(object : MenuItem.OnActionExpandListener {
            override fun onMenuItemActionExpand(menuItem: MenuItem): Boolean {
                searchEditText.requestFocus()
                if (searchEditText.requestFocus()) {
                    val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0)
                }
                menu.findItem(R.id.action_language).setVisible(false)
                search_list!!.visibility = View.VISIBLE
                return true
            }

            override fun onMenuItemActionCollapse(menuItem: MenuItem): Boolean {
                val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(currentFocus.windowToken, 0)
                searchEditText.setText("")
                search_list!!.visibility = View.GONE

                menu.findItem(R.id.action_language).setVisible(true)

                return true
            }
        })

        return true
    }

    private fun makeApiCall(mytag: String) {

        val params = HashMap<String, String>()
        params["keyword"] = mytag
        params["action"] = "autosuggest"

        RetrofitClient.getInstance()
            .doBackProcess(this@LandingPage, params, "online", object : APIResponse {
                override fun onSuccess(res: String?) {
                    try {
                        val jobj = JSONObject(res)
                        Log.e("SERACH RES", "REEE" + res)
                        val status = jobj.getInt("status")

                        val details = jobj.getString("details")
                        if (status == 1) {
                            searchFilters = ArrayList<String>()
                            mySearchData = ArrayList<SearchData>()
                            val jsonArray = jobj.getJSONArray("data")

                            for (i in 0 until jsonArray.length()) {
                                val row = jsonArray.getJSONObject(i)
                                var mysdata = Gson().fromJson(
                                    jsonArray.get(i).toString(),
                                    SearchData::class.java
                                )
                                mySearchData.add(mysdata)
                                searchFilters.add(row.getString("display"))
                            }

                            mHighlightArrayAdapter = SearchAdapter(
                                this@LandingPage,
                                R.layout.serach_item,
                                R.id.product_name,
                                searchFilters
                            )

                            search_list!!.adapter = mHighlightArrayAdapter
                            mHighlightArrayAdapter.notifyDataSetChanged()
                            //  autoSuggestAdapter.setData(searchFilters)
                            // autoSuggestAdapter.notifyDataSetChanged()
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

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        when (item.itemId) {
            R.id.action_search -> {
                return true
            }
            R.id.action_language ->
                if (PrefManager.getIn().language == "Hindi") {
                    PrefManager.getIn().language = "English"
                    pageNo = 1
                    menu!!.getItem(0).setIcon(
                        ContextCompat.getDrawable(
                            this,
                            R.mipmap.icon_language_e
                        )
                    )
                    apiCall()
                } else {
                    PrefManager.getIn().language = "Hindi"
                    pageNo = 1
                    menu!!.getItem(0).setIcon(
                        ContextCompat.getDrawable(
                            this,
                            R.mipmap.icon_language_h
                        )
                    )
                    apiCall()
                }

            else -> return super.onOptionsItemSelected(item)
        }

        if (PrefManager.getIn().language.equals("Hindi")) {
            menu!!.getItem(0).setIcon(ContextCompat.getDrawable(this, R.mipmap.icon_language_h))
        } else {
            menu!!.getItem(0).setIcon(ContextCompat.getDrawable(this, R.mipmap.icon_language_e))
        }
        return true
    }


    override fun onClick(p0: View?) {
        when (p0!!.id) {
            R.id.nav_profile -> {
                val myintent = Intent(this@LandingPage, AboutUs::class.java)
                myintent.putExtra("title", "My Profile")
                myintent.putExtra("url", "")
                startActivity(myintent)
            }

            R.id.nav_about -> {
                val myintent = Intent(this@LandingPage, AboutUs::class.java)
                myintent.putExtra("title", "About FlicBuzz")
                myintent.putExtra("url", "")
                startActivity(myintent)
            }
            R.id.nav_fav -> {
                val myintent = Intent(this@LandingPage, AboutUs::class.java)
                myintent.putExtra("title", "My Favourites")
                myintent.putExtra("url", "")
                startActivity(myintent)
            }
            R.id.nav_package-> {
                val myintent = Intent(this@LandingPage, PaymentScreen_New::class.java)
                myintent.putExtra("title", "Packages")
                myintent.putExtra("url", "")
                startActivity(myintent)
                // Toast.makeText(this, "Clicked item fav", Toast.LENGTH_SHORT).show()
            }
            R.id.nav_refund -> {
                val myintent = Intent(this@LandingPage, AboutUs::class.java)
                myintent.putExtra("url", "https://www.flicbuzz.com/refund-cancellation_text.html")
                myintent.putExtra("title", "Refund and Cancellation")
                startActivity(myintent)
                // Toast.makeText(this, "Clicked item settings", Toast.LENGTH_SHORT).show()
            }

            R.id.nav_privacy -> {
                val myintent = Intent(this@LandingPage, AboutUs::class.java)
                myintent.putExtra("url", "https://www.flicbuzz.com/privacy-policy_text.html")
                myintent.putExtra("title", "Privacy Policy")
                startActivity(myintent)
            }
            R.id.nav_terms -> {
                val myintent = Intent(this@LandingPage, AboutUs::class.java)
                myintent.putExtra("url", "https://www.flicbuzz.com/termsofuse_text.html")
                myintent.putExtra("title", "Terms of Use")
                startActivity(myintent)
            }
            R.id.nav_contact -> {
                val myintent = Intent(this@LandingPage, AboutUs::class.java)
                myintent.putExtra("url", "")
                myintent.putExtra("title", "Privacy Policy")
                startActivity(myintent)
            }
            /*https://www.flicbuzz.com/termsofuse_text.html*/

            R.id.nav_logout -> {
                PrefManager.getIn().setLogin(false);
                val intent = Intent(this@LandingPage, SignIn::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                startActivity(intent)
            }
            R.id.nav_share -> {
                val appPackageName = packageName
                val sendIntent = Intent()
                sendIntent.action = Intent.ACTION_SEND
                sendIntent.putExtra(Intent.EXTRA_SUBJECT, "Carneeds")
                sendIntent.putExtra(
                    Intent.EXTRA_TEXT,
                    "Hi,I would like to share this FlicBuzz application, Please download from Google Play! \nhttps://play.google.com/store/apps/details?id=$appPackageName"
                )
                sendIntent.type = "text/plain"
                startActivity(sendIntent)
            }

        }
        drawer_layout.closeDrawer(GravityCompat.START)
        /*if (intent != null) {
             drawer_layout.closeDrawer(GravityCompat.START)
             intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
             startActivity(intent)
         }*/
    }

    internal lateinit var adapter: MainAdapter

    private fun apiCall() {
        loading = true
        var from = "" as String
        val params = HashMap<String, String>()
        params["action"] = "home2"
        params["plan"] = "free"
        params["page_number"] = "" + pageNo
        if (pageNo == 1) {
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
                            val jsonArray = jobj.getJSONArray("data")
                            if (pageNo == 1) {
                                myvideos.clear()
                            }
                            myData(jsonArray.toString())
                            if (myvideos.size < 20) {
                                scrollFlag = true
                            }
                            if (pageNo == 1) {
                                adapter = MainAdapter(this@LandingPage, myvideos, "main")
                                layoutManager = LinearLayoutManager(applicationContext)
                                my_recycler_view!!.setLayoutManager(layoutManager)
                                adapter.notifyDataSetChanged()
                                my_recycler_view!!.adapter = adapter
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