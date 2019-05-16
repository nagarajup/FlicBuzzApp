package com.aniapps.flicbuzzapp.player

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.os.Handler
import android.support.v4.content.ContextCompat
import android.support.v4.view.GravityCompat
import android.support.v4.view.MenuItemCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.SearchView
import android.support.v7.widget.Toolbar
import android.text.*
import android.text.style.StyleSpan
import android.util.Log
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.*
import com.aniapps.flicbuzzapp.AppConstants
import com.aniapps.flicbuzzapp.R
import com.aniapps.flicbuzzapp.activities.*
import com.aniapps.flicbuzzapp.adapters.AutoSuggestAdapter
import com.aniapps.flicbuzzapp.adapters.MainAdapter
import com.aniapps.flicbuzzapp.adapters.SearchAdapter
import com.aniapps.flicbuzzapp.models.MyVideos
import com.aniapps.flicbuzzapp.models.SearchData
import com.aniapps.flicbuzzapp.networkcall.APIResponse
import com.aniapps.flicbuzzapp.networkcall.RetrofitClient
import com.aniapps.flicbuzzapp.util.IabHelper
import com.aniapps.flicbuzzapp.utils.CircleImageView
import com.aniapps.flicbuzzapp.utils.PrefManager
import com.aniapps.flicbuzzapp.utils.Utility
import com.google.gson.Gson
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_main.*
import org.json.JSONArray
import org.json.JSONObject
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap
import kotlin.collections.set
import android.widget.SearchView.OnQueryTextListener as OnQueryTextListener1

class LandingPage : AppConstants(), View.OnClickListener {


    internal lateinit var rc_list: RecyclerView
    internal lateinit var myvideos: ArrayList<MyVideos>
    internal var loading = false
    internal var scrollFlag = false
    internal var layoutManager: LinearLayoutManager? = null
    internal var total_records = ""
    private var pbr: ProgressBar? = null
    internal lateinit var imageView: CircleImageView
    internal lateinit var nav_about: TextView
    internal lateinit var tvtitle: TextView
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
    // internal lateinit var switchCompat: SwitchCompat
    internal lateinit var nav_lang: TextView
    internal var tag_id: String = "";
    var mHelper: IabHelper? = null
    private val PURCHSE_REQUEST = 3
    private val TAG = "Landing Screen"
    internal var subDate = ""

    companion object {
        lateinit var playingVideos: ArrayList<MyVideos>
        var videoCount: Int = 0
    }

    internal var pageNo = 1
    internal var my_recycler_view: RecyclerView? = null
    internal lateinit var search_list: ListView
    internal lateinit var header_title: TextView
    internal lateinit var header_hindi: TextView
    internal lateinit var header_english: TextView
    internal lateinit var nav_hindi: TextView
    internal lateinit var nav_english: TextView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val mToolbar = findViewById<View>(R.id.toolbar) as Toolbar
        header_title = findViewById<View>(R.id.title) as TextView
        header_hindi = findViewById<View>(R.id.header_lang_hindi) as TextView
        header_english = findViewById<View>(R.id.header_lang_eng) as TextView

        setSupportActionBar(mToolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        mHelper = IabHelper(this@LandingPage, Utility.sharedKey)
        header_title.text = "Hindi | English"
        setColor(header_title, 1)
        myvideos = ArrayList()
        playingVideos = ArrayList()
        // val jsonArray = intent.getStringExtra("jsonArray")
        //myData(jsonArray)

        // Toast.makeText(this, PrefManager.getIn().getPayment_data().toString(), Toast.LENGTH_SHORT).show()
        val toggle = ActionBarDrawerToggle(
            this, drawer_layout, mToolbar,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        )
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()

//        nav_view.setNavigationItemSelectedListener(this)

        my_recycler_view = findViewById<View>(R.id.my_recyclerview) as RecyclerView
        search_list = findViewById<ListView>(R.id.search_list)

        search_list.setOnItemClickListener { adapterView, view, i, l ->
            if (!searchFilters.get(0).toString().equals("No Search results found")) {
                search_list.visibility = View.GONE
                searchEditText.setText(mySearchData.get(i).display)
                val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(currentFocus.windowToken, 0)
                trackEvent(this@LandingPage, "MainPage", "Search|Selection")
                if (mySearchData.get(i).type.equals("tag")) {
                    tag_id = mySearchData.get(i).search_id
                    pageNo = 1
                    apiCall(tag_id);
                } else {
                    // Log.e("TEST", "" + mySearchData.get(i).type)
                    getVidoeById(mySearchData.get(i).search_id)
                }
            }
        }
        tv_profile_name = findViewById<TextView>(R.id.tv_profile_name);
        imageView = findViewById<CircleImageView>(R.id.imageView);
        tv_profile_email = findViewById<TextView>(R.id.tv_profile_email);
        tv_profile_plan = findViewById<TextView>(R.id.tv_profile_plan);
        tvtitle = findViewById<TextView>(R.id.tvtitle);
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
        //   switchCompat = findViewById<SwitchCompat>(R.id.nav_language)
        nav_lang = findViewById<TextView>(R.id.nav_lang)
        nav_english = findViewById<TextView>(R.id.nav_lang_eng)
        nav_hindi = findViewById<TextView>(R.id.nav_lang_hindi)
        setColor(nav_lang, 2)
        setLangSelection()
        tv_profile_name.setText(PrefManager.getIn().getName())
        tv_profile_email.setText(PrefManager.getIn().getEmail())
        if (PrefManager.getIn().getPlan().equals("3")) {
            tv_profile_plan.setText("Plan : Three Months")
        } else if (PrefManager.getIn().getPlan().equals("6")) {
            tv_profile_plan.setText("Plan : Six Months")
        } else if (PrefManager.getIn().getPlan().equals("12")) {
            tv_profile_plan.setText("Plan : One Year")
        } else if (PrefManager.getIn().getPlan().equals("expired")) {
            tv_profile_plan.setText("Plan : Expired")
        } else if (PrefManager.getIn().getPlan().equals("trail")) {
            tv_profile_plan.setText("Plan : Trail")
        }

        header_title.setOnClickListener(View.OnClickListener {
            PrefManager.getIn().language = if (PrefManager.getIn().language.equals("Hindi")) "English" else "Hindi"
            pageNo = 1
            setColor(header_title, 1)
            setColor(nav_lang, 2)
            apiCall(tag_id);
            drawer_layout.closeDrawer(GravityCompat.START)
            setLangSelection()
        })
        header_english.setOnClickListener(View.OnClickListener {
            trackEvent(this@LandingPage, "MainPage", "Language|Header|English")
            PrefManager.getIn().language = "English"
            pageNo = 1
            setLangSelection()
            apiCall(tag_id);
            drawer_layout.closeDrawer(GravityCompat.START)
        })
        header_hindi.setOnClickListener(View.OnClickListener {
            trackEvent(this@LandingPage, "MainPage", "Language|Header|Hindi")
            PrefManager.getIn().language = "Hindi"
            pageNo = 1
            setLangSelection()
            apiCall(tag_id);
            drawer_layout.closeDrawer(GravityCompat.START)
        })
        nav_lang.setOnClickListener(View.OnClickListener {
            PrefManager.getIn().language = if (PrefManager.getIn().language.equals("Hindi")) "English" else "Hindi"
            pageNo = 1
            setColor(header_title, 1)
            setColor(nav_lang, 2)
            apiCall(tag_id);
            setLangSelection()
            drawer_layout.closeDrawer(GravityCompat.START)
        })

        nav_hindi.setOnClickListener(View.OnClickListener {
            trackEvent(this@LandingPage, "MainPage", "Language|Menu|Hindi")
            PrefManager.getIn().language = "Hindi"
            pageNo = 1
            setLangSelection()
            apiCall(tag_id);
            drawer_layout.closeDrawer(GravityCompat.START)
        })
        nav_english.setOnClickListener(View.OnClickListener {
            trackEvent(this@LandingPage, "MainPage", "Language|Menu|English")
            PrefManager.getIn().language = "English"
            pageNo = 1
            setLangSelection()
            apiCall(tag_id);
            drawer_layout.closeDrawer(GravityCompat.START)
        })


        pbr = findViewById(R.id.load_progress) as ProgressBar
        pbr!!.getIndeterminateDrawable().setColorFilter(
            ContextCompat.getColor(this, R.color.colorAccent),
            android.graphics.PorterDuff.Mode.MULTIPLY
        )
        myvideos.clear()
        apiCall(tag_id)
        imageView.setOnClickListener(View.OnClickListener {
            val myintent = Intent(this@LandingPage, UpdateProfileActivity::class.java)
            startActivity(myintent)
            overridePendingTransition(R.anim.left_slide_in, R.anim.left_slide_out)
        })
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
                            apiCall(tag_id)
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }
        })

    }

    override fun onResume() {
        if (!PrefManager.getIn().getProfile_pic().equals("")) {
            Picasso.with(this@LandingPage)
                .load(PrefManager.getIn().getProfile_pic())
                .fit().centerInside()
                .error(R.mipmap.launcher_icon)
                .into(imageView);
        }
        tv_profile_name.setText(PrefManager.getIn().getName())
        tv_profile_email.setText(PrefManager.getIn().getEmail())
        if (PrefManager.getIn().getPlan().equals("3")) {
            tv_profile_plan.setText("Plan : Three Months")
        } else if (PrefManager.getIn().getPlan().equals("6")) {
            tv_profile_plan.setText("Plan : Six Months")
        } else if (PrefManager.getIn().getPlan().equals("12")) {
            tv_profile_plan.setText("Plan : One Year")
        } else if (PrefManager.getIn().getPlan().equals("expired")) {
            tv_profile_plan.setText("Plan : Expired")
        } else if (PrefManager.getIn().getPlan().equals("trail")) {
            tv_profile_plan.setText("Plan : Trail")
        }
        /* if (PrefManager.getIn().getPlan().equals("expired")) {
             alertDialog(this@LandingPage, "Alert", "Your plan is expired, Please purchase subscription.", 1)
         }*/

        if (PrefManager.getIn().getPayment_mode().equals("3")) {
            mHelper?.startSetup(IabHelper.OnIabSetupFinishedListener { result ->
                if (!result.isSuccess) {
                    Log.e("limited", "In-app Billing is not set up OK")
                } else {
                    Log.v("Limites", "YAY, in app billing set up! $result")
                    if (Utility.getMilliSeconds(PrefManager.getIn().getSubscription_end_date()) > Utility.getMilliSeconds(
                            PrefManager.getIn().getServer_date_time()
                        )
                    ) {
                        mHelper?.queryInventoryAsync(mGotInventoryListener) //Getting inventory of purchases and assigning listener
                    }
                }
            })
        }

        super.onResume()

    }

    internal var mGotInventoryListener: IabHelper.QueryInventoryFinishedListener =
        IabHelper.QueryInventoryFinishedListener { result, inventory ->
            if (result.isFailure) {
                // handle error here

                Log.v(TAG, "failure in checking if user has purchases")
            } else {
                PrefManager.getIn().setPackage(false)
                // does the user have the premium upgrade?
                val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                val calender = Calendar.getInstance()
                val calenderend = Calendar.getInstance()
                var start_date: Date? = null
                var end_date: Date? = null
                var trailFlag = false
                try {
                    start_date = sdf.parse(PrefManager.getIn().getSubscription_start_date())
                    end_date = sdf.parse(PrefManager.getIn().getSubscription_end_date())
                    calender.time = start_date!!
                    calender.add(Calendar.DATE, 7)
                    calenderend.time = end_date!!
                    if (calender.timeInMillis == calenderend.timeInMillis) {
                        trailFlag = true
                    }

                } catch (e: Exception) {

                }

                // if (PrefManager.getIn().getPlan() == "3") {
                if (inventory.hasPurchase(Utility.threemonths_threedaytrail)) run {

                    if (PrefManager.getIn().getPayment_mode() == "3") {

                        try {
                            start_date = sdf.parse(PrefManager.getIn().getSubscription_start_date())
                            subDate = sdf.format(start_date)
                            val c = Calendar.getInstance()
                            c.time = start_date
                            val c1 = Calendar.getInstance()
                            if (!trailFlag) {
                                c.add(Calendar.MONTH, 3)
                                c1.time = start_date
                                c1.add(Calendar.MONTH, 6)
                            } else {
                                c1.time = start_date
                                c1.add(Calendar.MONTH, 3)
                            }
                            val endDate1 = sdf.format(c.time)
                            val endDate2 = sdf.format(c1.time)
                            planUpdate(
                                inventory.getPurchase(Utility.threemonths_threedaytrail).sku,
                                endDate1,
                                endDate2,
                                inventory.getPurchase(Utility.threemonths_threedaytrail).toString(),
                                1
                            )

                        } catch (e: ParseException) {
                            e.printStackTrace()
                        }

                    }
                } else if (inventory.hasPurchase(Utility.threemonths)) {

                    if (PrefManager.getIn().getPayment_mode() == "3") {

                        try {
                            start_date = sdf.parse(PrefManager.getIn().getSubscription_start_date())
                            subDate = sdf.format(start_date)
                            val c = Calendar.getInstance()
                            c.time = start_date!!
                            val c1 = Calendar.getInstance()
                            if (!trailFlag) {
                                c.add(Calendar.MONTH, 3)
                                c1.time = start_date
                                c1.add(Calendar.MONTH, 6)
                            } else {
                                c1.time = start_date
                                c1.add(Calendar.MONTH, 3)
                            }
                            val endDate1 = sdf.format(c.time)
                            val endDate2 = sdf.format(c1.time)
                            planUpdate(
                                inventory.getPurchase(Utility.threemonths).sku,
                                endDate1,
                                endDate2,
                                inventory.getPurchase(Utility.threemonths).toString(),
                                1
                            )

                        } catch (e: ParseException) {
                            e.printStackTrace()
                        }

                    }
                } else if (inventory.hasPurchase(Utility.six_months)) {
                    // if (Utility.getMilliSeconds(PrefManager.getIn().getSubscription_end_date()) < calender.getTimeInMillis()) {
                    if (PrefManager.getIn().getPayment_mode() == "3") {
                        try {
                            start_date = sdf.parse(PrefManager.getIn().getSubscription_start_date())
                            subDate = sdf.format(start_date)
                            val c = Calendar.getInstance()
                            c.time = start_date!!
                            val c1 = Calendar.getInstance()
                            if (!trailFlag) {
                                c.add(Calendar.MONTH, 6)
                                c1.time = start_date
                                c1.add(Calendar.MONTH, 12)
                            } else {
                                c1.time = start_date
                                c1.add(Calendar.MONTH, 6)
                            }

                            /* c.add(Calendar.MONTH, 6);
                                 Calendar c1 = Calendar.getInstance();
                                 c1.setTime(start_date);
                                 c1.add(Calendar.MONTH, 12);*/
                            val endDate1 = sdf.format(c.time)
                            val endDate2 = sdf.format(c1.time)
                            planUpdate(
                                inventory.getPurchase(Utility.six_months).sku,
                                endDate1,
                                endDate2,
                                inventory.getPurchase(Utility.six_months).toString(),
                                1
                            )

                        } catch (e: ParseException) {
                            e.printStackTrace()
                        }

                    }
                } else if (inventory.hasPurchase(Utility.one_year)) {
                    //  if (Utility.getMilliSeconds(PrefManager.getIn().getSubscription_end_date()) < calender.getTimeInMillis()) {
                    if (PrefManager.getIn().getPayment_mode() == "3") {
                        try {
                            start_date = sdf.parse(PrefManager.getIn().getSubscription_start_date())
                            subDate = sdf.format(start_date)
                            val c = Calendar.getInstance()
                            c.time = start_date!!

                            val c1 = Calendar.getInstance()
                            if (!trailFlag) {
                                c.add(Calendar.MONTH, 12)
                                c1.time = start_date
                                c1.add(Calendar.MONTH, 24)
                            } else {
                                c1.time = start_date
                                c1.add(Calendar.MONTH, 12)
                            }


                            /*c.add(Calendar.MONTH, 12);
                                 Calendar c1 = Calendar.getInstance();
                                 c1.setTime(start_date);
                                 c1.add(Calendar.MONTH, 24);*/
                            val endDate1 = sdf.format(c.time)
                            val endDate2 = sdf.format(c1.time)
                            planUpdate(
                                inventory.getPurchase(Utility.one_year).sku,
                                endDate1,
                                endDate2,
                                inventory.getPurchase(Utility.one_year).toString(),
                                1
                            )

                        } catch (e: ParseException) {
                            e.printStackTrace()
                        }

                    }
                } else {
                    planUpdate(
                        "expired",
                        PrefManager.getIn().getSubscription_start_date(),
                        PrefManager.getIn().getSubscription_end_date(),
                        PrefManager.getIn().getPayment_data(),
                        1
                    )
                }
                //}
            }
        }

    fun planUpdate(
        package_data: String,
        sub_start_date: String,
        sub_end_date: String,
        payment_data: String,
        renewal: Int
    ) {
        val params = HashMap<String, String>()
        params["subscription_start_date"] = sub_start_date
        params["subscription_end_date"] = sub_end_date
        params["payment_data"] = payment_data
        params["action"] = "update_package"
        if (package_data == Utility.threemonths_threedaytrail) {
            params["package"] = "3"
        } else if (package_data == Utility.threemonths) {
            params["package"] = "3"
        } else if (package_data == Utility.six_months) {
            params["package"] = "6"
        } else if (package_data == Utility.one_year) {
            params["package"] = "12"
        } else if (package_data == "trail") {
            params["package"] = "trail"
        } else if (package_data == "expired") {
            params["package"] = "expired"
        }
        var jsonObject: JSONObject
        RetrofitClient.getInstance()
            .doBackProcess(this@LandingPage, params, "online", object : APIResponse {
                override fun onSuccess(result: String) {
                    try {

                        jsonObject = JSONObject(result)
                        val status = jsonObject.getInt("status")
                        if (status == 1) {

                            PrefManager.getIn().setPayment_data(params["payment_data"])
                            PrefManager.getIn().setSubscription_start_date(params["subscription_start_date"])
                            PrefManager.getIn().setSubscription_renewal_date(params["subscription_renewal_date"])
                            PrefManager.getIn().setSubscription_end_date(params["subscription_end_date"])
                            PrefManager.getIn().setPlan(params["package"])
                            if (PrefManager.getIn().getPlan() == "3") {
                                PrefManager.getIn().setPayment_mode("1")
                            } else if (PrefManager.getIn().getPlan() == "6") {
                                PrefManager.getIn().setPayment_mode("1")
                            } else if (PrefManager.getIn().getPlan() == "12") {
                                PrefManager.getIn().setPayment_mode("1")
                            } else if (PrefManager.getIn().getPlan() == "expired") {
                                PrefManager.getIn().setPayment_mode("3")
                                alertDialog(
                                    this@LandingPage,
                                    "Alert",
                                    "Your plan is expired, Please purchase subscription.",
                                    1
                                )
                            } else if (PrefManager.getIn().getPlan() == "trail") {
                                PrefManager.getIn().setPayment_mode("1")
                            }

                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }

                }

                override fun onFailure(res: String) {
                }
            })
    }


    fun alertDialog(context: Context, title: String, msg: String, from: Int) {
        val builder = AlertDialog.Builder(context)
        builder.setMessage(msg)
        builder.setTitle(title)
        builder.setCancelable(false)
        if (from == 2) {
            builder.setNegativeButton("CANCEL") { dialog, which ->
                trackEvent(this@LandingPage, "MainPage", "LogOut|Cancel")
                dialog.dismiss()
            }
        }
        builder.setPositiveButton("OK") { dialog, which ->

            if (from == 1) {
                trackEvent(this@LandingPage, "MainPage", "Plan Expired|Ok")
                val intent = Intent(this@LandingPage, PaymentScreen_New::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                startActivity(intent)
                overridePendingTransition(R.anim.left_slide_in, R.anim.left_slide_out)
            } else {
                PrefManager.getIn().clearLogins();
                trackEvent(this@LandingPage, "MainPage", "LogOut|Ok")
                // PrefManager.getIn().setLogin(false);
                val intent = Intent(this@LandingPage, SignIn::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                startActivity(intent)
                overridePendingTransition(R.anim.left_slide_in, R.anim.left_slide_out)
            }
        }
        builder.show()
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

    override fun onCreateOptionsMenu(menu: Menu): Boolean {

        this.menu = menu;
        menuInflater.inflate(R.menu.main, menu)
        val menuItem = menu.findItem(R.id.action_search)
        // Inflate the menu; this adds items to the action bar if it is present.
        autoSuggestAdapter = AutoSuggestAdapter(
            this,
            android.R.layout.simple_dropdown_item_1line
        )

        searchFilters = ArrayList();
        searchFilters.clear();
        mHighlightArrayAdapter = SearchAdapter(
            this@LandingPage,
            R.layout.serach_item,
            R.id.product_name,
            searchFilters
        )

        search_list.adapter = mHighlightArrayAdapter;
        mHighlightArrayAdapter.notifyDataSetChanged()
        //  search_list!!.adapter=autoSuggestAdapter
        searchEditText = MenuItemCompat.getActionView(menuItem).findViewById(R.id.edittext) as EditText
        menuView = MenuItemCompat.getActionView(menuItem).findViewById(R.id.menu_view) as FrameLayout
        clear = MenuItemCompat.getActionView(menuItem).findViewById(R.id.clear) as ImageView
        clear.setOnClickListener {
            searchEditText.setText("")
            search_list.visibility = View.GONE
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
                trackEvent(this@LandingPage, "MainPage", "Search")
                searchEditText.requestFocus()
                if (searchEditText.requestFocus()) {
                    val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0)
                }
                search_list.visibility = View.VISIBLE
                return true
            }

            override fun onMenuItemActionCollapse(menuItem: MenuItem): Boolean {
                trackEvent(this@LandingPage, "MainPage", "Search|Close")

                val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(currentFocus.windowToken, 0)
                searchEditText.setText("")
                search_list.visibility = View.GONE
                searchFilters.clear();
                tag_id = "";
                apiCall(tag_id)

                return true
            }
        })

        return true
    }

    private fun setLangSelection() {
        if (PrefManager.getIn().language.equals("Hindi", true)) {
            nav_hindi.setTypeface(nav_hindi.getTypeface(), Typeface.BOLD)
            nav_english.setTypeface(null, Typeface.NORMAL)
            header_hindi.setTypeface(header_hindi.getTypeface(), Typeface.BOLD)
            header_english.setTypeface(null, Typeface.NORMAL)
        } else {
            nav_hindi.setTypeface(null, Typeface.NORMAL)
            nav_english.setTypeface(nav_english.getTypeface(), Typeface.BOLD)
            header_hindi.setTypeface(null, Typeface.NORMAL)
            header_english.setTypeface(header_english.getTypeface(), Typeface.BOLD)
        }
    }

    private fun setColor(view: TextView, from: Int) {
        view.setText("Hindi | English")
        val spannable = SpannableString(view.text)
        if (PrefManager.getIn().language.equals("Hindi")) {
            spannable.setSpan(
                StyleSpan(Typeface.BOLD), 0, 5, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )
            if (from == 1) {
                spannable.setSpan(
                    StyleSpan(Typeface.NORMAL), 8, 15, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                )
            } else {
                spannable.setSpan(
                    StyleSpan(Typeface.NORMAL), 8, 15, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                )
            }
        } else {
            spannable.setSpan(
                StyleSpan(Typeface.BOLD), 8, 15, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )
            if (from == 1) {
                spannable.setSpan(
                    StyleSpan(Typeface.NORMAL), 0, 5, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                )
            } else {
                spannable.setSpan(
                    StyleSpan(Typeface.NORMAL), 0, 5, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                )
            }
        }

        view.text = spannable
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
                        //Log.e("SERACH RES", "REEE" + res)
                        val status = jobj.getInt("status")

                        val details = jobj.getString("details")
                        if (status == 1) {
                            searchFilters = ArrayList<String>()
                            mySearchData = ArrayList<SearchData>()
                            val jsonArray = jobj.getJSONArray("data")

                            if (jsonArray.length() > 0) {
                                for (i in 0 until jsonArray.length()) {
                                    val row = jsonArray.getJSONObject(i)
                                    var mysdata = Gson().fromJson(
                                        jsonArray.get(i).toString(),
                                        SearchData::class.java
                                    )
                                    mySearchData.add(mysdata)
                                    searchFilters.add(row.getString("display"))
                                }
                            } else {
                                searchFilters.add("No Search results found")
                            }


                            mHighlightArrayAdapter = SearchAdapter(
                                this@LandingPage,
                                R.layout.serach_item,
                                R.id.product_name,
                                searchFilters
                            )


                            search_list.adapter = mHighlightArrayAdapter
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


    override fun onClick(p0: View?) {
        when (p0!!.id) {
            R.id.nav_profile -> {
                    trackEvent(this@LandingPage, "MainPage", "My Profile")
                val myintent = Intent(this@LandingPage, UpdateProfileActivity::class.java)
                myintent.putExtra("title", "My Profile")
                myintent.putExtra("url", "")
                startActivity(myintent)
                overridePendingTransition(R.anim.left_slide_in, R.anim.left_slide_out)

            }

            R.id.nav_about -> {
                trackEvent(this@LandingPage, "MainPage", "About FlicBuzz")
                val myintent = Intent(this@LandingPage, AboutUs::class.java)
                // myintent.putExtra("title", "About FlicBuzz")
                myintent.putExtra("title", "About FlicBuzz")
                myintent.putExtra("url", "https://www.flicbuzz.com/about_us_text.html")
                startActivity(myintent)
                overridePendingTransition(R.anim.left_slide_in, R.anim.left_slide_out)

            }
            R.id.nav_fav -> {
                trackEvent(this@LandingPage, "MainPage", "Favorite Videos")
                val myintent = Intent(this@LandingPage, FavoriteAct::class.java)
                startActivity(myintent)
                overridePendingTransition(R.anim.left_slide_in, R.anim.left_slide_out)

            }
            R.id.nav_package -> {
                trackEvent(this@LandingPage, "MainPage", "Packages")
                val myintent = Intent(this@LandingPage, PaymentScreen_New::class.java)
                myintent.putExtra("title", "Packages")
                myintent.putExtra("url", "")
                startActivity(myintent)
                overridePendingTransition(R.anim.left_slide_in, R.anim.left_slide_out)

                // Toast.makeText(this, "Clicked item fav", Toast.LENGTH_SHORT).show()
            }
            R.id.nav_refund -> {
                trackEvent(this@LandingPage, "MainPage", "Refund and Cancellation")
                val myintent = Intent(this@LandingPage, AboutUs::class.java)
                myintent.putExtra("url", "https://www.flicbuzz.com/refund-cancellation_text.html")
                myintent.putExtra("title", "Refund and Cancellation")
                startActivity(myintent)
                overridePendingTransition(R.anim.left_slide_in, R.anim.left_slide_out)

                // Toast.makeText(this, "Clicked item settings", Toast.LENGTH_SHORT).show()
            }

            R.id.nav_privacy -> {
                trackEvent(this@LandingPage, "MainPage", "Privacy Policy")
                val myintent = Intent(this@LandingPage, AboutUs::class.java)
                myintent.putExtra("url", "https://www.flicbuzz.com/privacy-policy_text.html")
                myintent.putExtra("title", "Privacy Policy")
                startActivity(myintent)
                overridePendingTransition(R.anim.left_slide_in, R.anim.left_slide_out)

            }
            R.id.nav_terms -> {
                trackEvent(this@LandingPage, "MainPage", "Terms of Use")
                val myintent = Intent(this@LandingPage, AboutUs::class.java)
                myintent.putExtra("url", "https://www.flicbuzz.com/termsofuse_text.html")
                myintent.putExtra("title", "Terms of Use")
                startActivity(myintent)
                overridePendingTransition(R.anim.left_slide_in, R.anim.left_slide_out)

            }
            R.id.nav_contact -> {
                trackEvent(this@LandingPage, "MainPage", "Contact Us")
                MessageDialog_Feedback();
            }

            R.id.nav_settings -> {
                trackEvent(this@LandingPage, "MainPage", "Settings")
                val intent = Intent(this@LandingPage, SettingsActivity::class.java)
                startActivity(intent)
                overridePendingTransition(R.anim.left_slide_in, R.anim.left_slide_out)
            }

            R.id.nav_logout -> {
                trackEvent(this@LandingPage, "MainPage", "LogOut")
                alertDialog(this@LandingPage, "Logout", "Are you sure to logout.", 2)

            }
            R.id.nav_share -> {
                trackEvent(this@LandingPage, "MainPage", "App Share")
                val appPackageName = packageName
                val sendIntent = Intent()
                sendIntent.action = Intent.ACTION_SEND
                sendIntent.putExtra(Intent.EXTRA_SUBJECT, "FlickBuzz")
                sendIntent.putExtra(
                    Intent.EXTRA_TEXT,
                    "Hi, I Sharing FlicBuzz - A Complete Entertainment App download link from Google Play! \nadgully.com/fbapp"
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


    fun MessageDialog_Feedback() {
        val Feedback_Dialog = Dialog(this, R.style.ThemeDialogCustom)
        Feedback_Dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        Feedback_Dialog.setContentView(R.layout.dialog_dealer_helpline)
        Feedback_Dialog.setCancelable(true)
        val lay_feedback_focus = Feedback_Dialog.findViewById(R.id.lay_feedback) as LinearLayout
        lay_feedback_focus.isFocusableInTouchMode = true
        lay_feedback_focus.requestFocus()

        val et_sub = Feedback_Dialog.findViewById(R.id.et_subject) as EditText
        val et_msg = Feedback_Dialog.findViewById(R.id.et_message) as EditText

        et_msg.setLines(4)


        val tv_toll_free_number = Feedback_Dialog.findViewById(R.id.tv_toll_free_number) as TextView
        //final String toll_free = "1800 270 1249";

        //tollFreeNumber("Toll Free Number:", Pref.getIn().getToll_free_no(), tv_toll_free_number)

        val window = Feedback_Dialog.window
        val wlp = window!!.attributes
        wlp.gravity = Gravity.TOP
        wlp.y = 80
        window.attributes = wlp
        val bt_submit_feedback = Feedback_Dialog.findViewById(R.id.btn_feedback_send) as TextView
        val bt_cancel_feedback = Feedback_Dialog.findViewById(R.id.btn_feedback_cancel) as TextView
        bt_submit_feedback.setOnClickListener {

            if (validateET(et_msg)) {
                et_msg.setHint("")
                et_msg.setError("Please enter your message")
            } else {

                if (et_msg.getText().toString().length > 5) {
                    trackEvent(this@LandingPage, "MainPage", "Contact Us|Submit")
                    Feedback_Dialog.dismiss()
                    val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.hideSoftInputFromWindow(et_msg.getWindowToken(), 0)
                    feedbackApi(et_sub.text.toString(), et_msg.text.toString())
                    // Dealer_Feedback(et_dealer_feedback.getText().toString(), cb_query_option)
                } else {
                    et_msg.setError("Message length should be greater than 5 characters")
                }
            }
        }

        bt_cancel_feedback.setOnClickListener {
            trackEvent(this@LandingPage, "MainPage", "Contact Us|Cancel")
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(et_msg.getWindowToken(), 0)
            Feedback_Dialog.dismiss()
        }
        Feedback_Dialog.show()
    }


    fun validateET(et: EditText): Boolean {
        var flag = false
        flag = et.text.toString().trim { it <= ' ' }.length == 0
        return flag
    }


    internal lateinit var adapter: MainAdapter

    private fun apiCall(tag_id: String) {
        loading = true
        var from = "" as String
        val params = HashMap<String, String>()
        if (tag_id.equals("")) {
            params["action"] = "home2"
            params["plan"] = "free"
        } else {
            params["action"] = "getvideos_by_tags"
            params["tag_id"] = tag_id
        }
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
                                tvtitle.visibility = View.VISIBLE
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

    private fun feedbackApi(subject: String, msg: String) {

        val params = HashMap<String, String>()

        params["action"] = "contact_us"
        params["name"] = PrefManager.getIn().getName()
        params["email"] = PrefManager.getIn().getEmail()
        params["subject"] = subject
        params["message"] = msg


        RetrofitClient.getInstance()
            .doBackProcess(this@LandingPage, params, "", object : APIResponse {
                override fun onSuccess(res: String?) {
                    try {
                        val jobj = JSONObject(res)
                        val status = jobj.getInt("status")
                        val details = jobj.getString("details")
                        if (status == 1) {
                            Toast.makeText(this@LandingPage, "" + jobj.getString("message"), Toast.LENGTH_LONG).show()

                        } else {
                            Toast.makeText(this@LandingPage, "status" + details, Toast.LENGTH_LONG).show()
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


    private fun getVidoeById(id: String) {

        val params = HashMap<String, String>()

        params["action"] = "get_video_by_id"
        params["video_id"] = id

        RetrofitClient.getInstance()
            .doBackProcess(this@LandingPage, params, "", object : APIResponse {
                override fun onSuccess(res: String?) {
                    try {
                        val jobj = JSONObject(res)
                        val status = jobj.getInt("status")
                        val details = jobj.getString("details")
                        if (status == 1) {
                            val data = jobj.getJSONObject("data")
                            val videodata = data.getJSONObject("video")


                            var lead = Gson().fromJson(
                                videodata.toString(),
                                MyVideos::class.java
                            )

                            var mylist = ArrayList<MyVideos>()
                            mylist.add(lead)

                            val player_in = Intent(this@LandingPage, MyPlayer::class.java)
                            player_in.putExtra("playingVideo", lead)
                            player_in.putExtra("sequence", mylist)
                            player_in.putExtra("from", "main")
                            startActivity(player_in)
                            overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_up);

                        } else {
                            Toast.makeText(this@LandingPage, "status" + details, Toast.LENGTH_LONG).show()
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