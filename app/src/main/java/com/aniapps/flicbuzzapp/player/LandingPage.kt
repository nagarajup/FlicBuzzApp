package com.aniapps.flicbuzzapp.player

import android.annotation.SuppressLint
import android.app.*
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.text.*
import android.text.style.StyleSpan
import android.util.Log
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.app.NotificationCompat
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.core.view.MenuItemCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.aniapps.flicbuzzapp.AppConstants
import com.aniapps.flicbuzzapp.BuildConfig
import com.aniapps.flicbuzzapp.R
import com.aniapps.flicbuzzapp.activities.*
import com.aniapps.flicbuzzapp.adapters.AutoSuggestAdapter
import com.aniapps.flicbuzzapp.adapters.MainAdapter
import com.aniapps.flicbuzzapp.adapters.SearchAdapter
import com.aniapps.flicbuzzapp.db.LocalDB
import com.aniapps.flicbuzzapp.models.MyVideos
import com.aniapps.flicbuzzapp.models.SearchData
import com.aniapps.flicbuzzapp.networkcall.APIResponse
import com.aniapps.flicbuzzapp.networkcall.RetrofitClient
import com.aniapps.flicbuzzapp.notifications.Notification_Act
import com.aniapps.flicbuzzapp.util.IabHelper
import com.aniapps.flicbuzzapp.utils.PrefManager
import com.aniapps.flicbuzzapp.utils.Utility
import com.google.gson.Gson
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_main.*
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap
import kotlin.collections.set
import com.aniapps.flicbuzzapp.utils.CircleImageView as CircleImageView1

class LandingPage : AppConstants(), View.OnClickListener {


    internal lateinit var rc_list: RecyclerView
    internal lateinit var myvideos: ArrayList<MyVideos>
    internal var loading = false
    internal var updatePopup = false
    internal var scrollFlag = false
    internal var layoutManager: LinearLayoutManager? = null
    internal var total_records = ""
    private var pbr: ProgressBar? = null
    internal lateinit var imageView: CircleImageView1
    internal lateinit var nav_about: TextView
    internal lateinit var tvtitle: TextView
    internal lateinit var nav_profile: TextView
    internal lateinit var nav_privacy: TextView
    internal lateinit var nav_fav: TextView
    internal lateinit var nav_refund: TextView
    internal lateinit var nav_package: TextView
    internal lateinit var nav_package_razor: TextView
    internal lateinit var nav_share: TextView
    internal lateinit var nav_terms: TextView
    internal lateinit var nav_feedback: TextView
    internal lateinit var nav_settings: TextView
    internal lateinit var nav_logout: TextView
    internal lateinit var nav_version: TextView
    internal lateinit var tv_profile_name: TextView
    internal lateinit var tv_profile_email: TextView
    internal lateinit var tv_profile_plan: TextView
    internal lateinit var lay_notifications: FrameLayout
    internal lateinit var lay_notifications_count: LinearLayout
    internal lateinit var tv_notifications_count: TextView
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
    // internal lateinit var header_title: TextView
    internal lateinit var header_hindi: TextView
    internal lateinit var header_english: TextView
    internal lateinit var nav_hindi: TextView
    internal lateinit var nav_english: TextView
    private var mContext: Context? = null
    private var mNotificationManager: NotificationManager? = null
    private var mBuilder: NotificationCompat.Builder? = null
    val NOTIFICATION_CHANNEL_ID = "10001"
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        mContext = this
        val mToolbar = findViewById<View>(R.id.toolbar) as Toolbar
        // header_title = findViewById<View>(R.id.title) as TextView
        header_hindi = findViewById<View>(R.id.header_lang_hindi) as TextView
        header_english = findViewById<View>(R.id.header_lang_eng) as TextView
        setSupportActionBar(mToolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        mHelper = IabHelper(this@LandingPage, Utility.sharedKey)
        // header_title.text = "Hindi | English"
        //  setColor(header_title, 1)
        myvideos = ArrayList()
        playingVideos = ArrayList()
        // val jsonArray = intent.getStringExtra("jsonArray")
        //myData(jsonArray)

       /* val crashButton = Button(this)
        crashButton.text = "Crash!"
        crashButton.setOnClickListener {
            Crashlytics.getInstance().crash() // Force a crash
        }

        addContentView(crashButton, ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT))*/

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
        imageView = findViewById<CircleImageView1>(R.id.imageView);
        tv_profile_email = findViewById<TextView>(R.id.tv_profile_email);
        tv_profile_plan = findViewById<TextView>(R.id.tv_profile_plan);
        tvtitle = findViewById<TextView>(R.id.tvtitle);
        nav_about = findViewById<TextView>(R.id.nav_about);
        nav_profile = findViewById<TextView>(R.id.nav_profile);
        nav_privacy = findViewById<TextView>(R.id.nav_privacy);
        nav_fav = findViewById<TextView>(R.id.nav_fav);
        nav_refund = findViewById<TextView>(R.id.nav_refund);
        nav_package = findViewById<TextView>(R.id.nav_package);
        nav_package_razor = findViewById<TextView>(R.id.nav_package_razor);
        nav_share = findViewById<TextView>(R.id.nav_share);
        nav_terms = findViewById<TextView>(R.id.nav_terms);
        nav_feedback = findViewById<TextView>(R.id.nav_contact);
        nav_settings = findViewById<TextView>(R.id.nav_settings);
        nav_logout = findViewById<TextView>(R.id.nav_logout);
        nav_version = findViewById<TextView>(R.id.nav_version);
        if (resources.getString(R.string.base).contains("adgully")) {
            nav_version.text = "Version: " + BuildConfig.VERSION_CODE + " "+("Test")
        } else {
            nav_version.text = "Version: " + BuildConfig.VERSION_CODE
        }

        lay_notifications = findViewById<FrameLayout>(R.id.icon_notifications);
        lay_notifications_count = findViewById<LinearLayout>(R.id.header_count_circle);
        tv_notifications_count = findViewById<TextView>(R.id.header_notification_count);

        if (LocalDB.getInstance(this@LandingPage).getNumFiles() > 0) {
            lay_notifications_count.visibility = View.VISIBLE
            tv_notifications_count.setText("" + LocalDB.getInstance(this@LandingPage).getNumFiles())
        } else {
            lay_notifications_count.visibility = View.GONE
        }

        nav_about.setOnClickListener(this@LandingPage)
        nav_profile.setOnClickListener(this@LandingPage)
        nav_privacy.setOnClickListener(this@LandingPage)
        nav_fav.setOnClickListener(this@LandingPage)
        nav_refund.setOnClickListener(this@LandingPage)
        nav_package.setOnClickListener(this@LandingPage)
        nav_package_razor.setOnClickListener(this@LandingPage)
        nav_share.setOnClickListener(this@LandingPage)
        nav_terms.setOnClickListener(this@LandingPage)
        nav_feedback.setOnClickListener(this@LandingPage)
        nav_settings.setOnClickListener(this@LandingPage)
        nav_logout.setOnClickListener(this@LandingPage)
        lay_notifications.setOnClickListener(this@LandingPage)
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

        /* header_title.setOnClickListener(View.OnClickListener {
             PrefManager.getIn().language = if (PrefManager.getIn().language.equals("Hindi")) "English" else "Hindi"
             pageNo = 1
             setColor(header_title, 1)
             setColor(nav_lang, 2)
             apiCall(tag_id);
             drawer_layout.closeDrawer(GravityCompat.START)
             setLangSelection()
         })*/
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
            //  setColor(header_title, 1)
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
        // if (PrefManager.getIn().getPayment_mode().equals("3")) {
        mHelper?.startSetup(IabHelper.OnIabSetupFinishedListener { result ->
            if (!result.isSuccess) {
                Log.e("limited", "In-app Billing is not set up OK")
            } else {
                Log.v("Limites", "YAY, in app billing set up! $result")
                /*if (Utility.getMilliSeconds(PrefManager.getIn().getSubscription_end_date()) > Utility.getMilliSeconds(
                        PrefManager.getIn().getServer_date_time()
                    )
                ) {*/
                mHelper?.queryInventoryAsync(mGotInventoryListener) //Getting inventory of purchases and assigning listener
                //}
            }
        })
        // }
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
            tv_profile_plan.setText("Plan : Trial")
        }
        /* if (PrefManager.getIn().getPlan().equals("expired")) {
             alertDialog(this@LandingPage, "Alert", "Your plan is expired, Please purchase subscription.", 1)
         }*/

        if (!updatePopup && !PrefManager.getIn().getServer_version_mode().equals("3")) {
            updatePopup = true;
            if (PrefManager.getIn().getServer_version_mode().equals("2")) {
                updatePopup(true);
            } else {
                updatePopup(false);
            }
        }

        if (LocalDB.getInstance(this@LandingPage).getNumFiles() > 0) {
            lay_notifications_count.visibility = View.VISIBLE
            tv_notifications_count.setText("" + LocalDB.getInstance(this@LandingPage).getNumFiles())
        } else {
            lay_notifications_count.visibility = View.GONE
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
                    val purchase = inventory.getPurchase(Utility.threemonths_threedaytrail)
                    try {
                        val jsonObject = JSONObject(purchase.toString())
                        if (!PrefManager.getIn().autoRenewal) {
                            if (!jsonObject.getBoolean("autoRenewing")) {
                                PrefManager.getIn().autoRenewal = true
                                createNotification(
                                    this.getString(R.string.app_name),
                                    "Your FLicBuzz three months subscription has been cancelled, so your next subscription will not be charged"
                                )
                            }
                        }
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }

                    if (PrefManager.getIn().getPayment_mode() == "3" || PrefManager.getIn().getPlan().equals(
                            "expired",
                            ignoreCase = true
                        )
                    ) {

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

                    } else {

                    }
                } else if (inventory.hasPurchase(Utility.threemonths)) {
                    val purchase = inventory.getPurchase(Utility.threemonths)
                    try {
                        val jsonObject = JSONObject(purchase.toString())
                        if (!PrefManager.getIn().autoRenewal) {
                            if (!jsonObject.getBoolean("autoRenewing")) {
                                PrefManager.getIn().autoRenewal = true
                                createNotification(
                                    this.getString(R.string.app_name),
                                    "Your FlicBuzz three months subscription has been cancelled, so your next subscription will not be charged"
                                )
                            }
                        }
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                    if (PrefManager.getIn().getPayment_mode() == "3" || PrefManager.getIn().getPlan().equals(
                            "expired",
                            ignoreCase = true
                        )
                    ) {

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
                    val purchase = inventory.getPurchase(Utility.six_months)
                    try {
                        val jsonObject = JSONObject(purchase.toString())
                        if (!PrefManager.getIn().autoRenewal) {
                            if (!jsonObject.getBoolean("autoRenewing")) {
                                PrefManager.getIn().autoRenewal = true
                                createNotification(
                                    this.getString(R.string.app_name),
                                    "Your FlicBuzz six months subscription has been cancelled, so your next subscription will not be charged"
                                )
                            }
                        }
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                    // if (Utility.getMilliSeconds(PrefManager.getIn().getSubscription_end_date()) < calender.getTimeInMillis()) {
                    if (PrefManager.getIn().getPayment_mode() == "3" || PrefManager.getIn().getPlan().equals(
                            "expired",
                            ignoreCase = true
                        )
                    ) {
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
                    try {
                        val purchase = inventory.getPurchase(Utility.one_year)
                        val jsonObject = JSONObject(purchase.toString())
                        if (!PrefManager.getIn().autoRenewal) {
                            if (!jsonObject.getBoolean("autoRenewing")) {
                                PrefManager.getIn().autoRenewal = true
                                createNotification(
                                    this.getString(R.string.app_name),
                                    "Your FlicBuzz one year subscription has been cancelled, so your next subscription will not be charged"
                                )
                            }
                        }
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
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
                    if (PrefManager.getIn().getPayment_mode() == "3") {
                        planUpdate(
                            "expired",
                            PrefManager.getIn().getSubscription_start_date(),
                            PrefManager.getIn().getSubscription_end_date(),
                            PrefManager.getIn().getPayment_data(),
                            1
                        )
                    }
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
        PrefManager.getIn().autoRenewal = false
        val params = HashMap<String, String>()
        params["subscription_start_date"] = sub_start_date
        params["subscription_end_date"] = sub_end_date
        params["payment_data"] = payment_data
        params["action"] = "update_package"
        params["language"] = PrefManager.getIn().language.toLowerCase()
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

                        } else if (status == 14) run {
                            Utility.alertDialog(
                                this@LandingPage,
                                jsonObject.getString("message")
                            )
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

        val metrics = resources.displayMetrics
        val width = metrics.widthPixels
        val alert_dialog = Dialog(this@LandingPage)
        alert_dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        alert_dialog.setContentView(R.layout.alert_dialog)
        alert_dialog.setCanceledOnTouchOutside(false)
        alert_dialog.window!!.setLayout(6 * width / 7, LinearLayout.LayoutParams.WRAP_CONTENT)
        val txt_alert_title = alert_dialog.findViewById<View>(R.id.custom_alert_dialog_title) as TextView
        val txt_alert_description = alert_dialog.findViewById<View>(R.id.custom_alert_dialog_msg) as TextView
        val main = alert_dialog.findViewById<View>(R.id.main) as LinearLayout
        val txt_ok = alert_dialog.findViewById<View>(R.id.ok) as Button
        val txt_cancel = alert_dialog.findViewById<View>(R.id.cancel) as Button
        main.visibility = View.VISIBLE
        txt_alert_title.text = title
        txt_alert_description.text = msg
        txt_cancel.text = "OK"
        txt_ok.text = "CANCEL"
        if (from == 1) {
            txt_ok.visibility= View.GONE
        }
        txt_cancel.setOnClickListener {
            alert_dialog.dismiss()
            if (from == 1) {
                trackEvent(this@LandingPage, "MainPage", "Plan Expired|Ok")
                val intent = Intent(this@LandingPage, PaymentScreen_Razor::class.java)
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

        txt_ok.setOnClickListener {
            trackEvent(this@LandingPage, "MainPage", "LogOut|Cancel")
            alert_dialog.dismiss()
        }

        alert_dialog.show()
    }

   /* fun alertDialog(context: Context, title: String, msg: String, from: Int) {
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
                val intent = Intent(this@LandingPage, PaymentScreen_Razor::class.java)
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
    }*/

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
            tvtitle.visibility = View.VISIBLE
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
                tvtitle.visibility = View.GONE
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
                tvtitle.visibility = View.VISIBLE
                searchFilters.clear();
                tag_id = "";
                apiCall(tag_id)

                return true
            }
        })

        return true
    }

    private fun updatePopup(is_critical: Boolean) {
        try {
            val dialog = Dialog(this@LandingPage)
            dialog.setCanceledOnTouchOutside(false)
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialog.setContentView(R.layout.customalert)
            dialog.window!!.setBackgroundDrawable(
                ColorDrawable(Color.TRANSPARENT)
            )
            val title_text = dialog
                .findViewById(R.id.custom_alert_dialog_title) as TextView
            val msg_text = dialog
                .findViewById(R.id.custom_alert_dialog_msg) as TextView
            val positiveBtn = dialog
                .findViewById(R.id.custom_alert_dialog_button_ok) as TextView

            val negativeBtn = dialog
                .findViewById(R.id.custom_alert_dialog_button_cancel) as TextView
            if (is_critical) {
                negativeBtn.visibility = View.GONE
                dialog.setCancelable(false)
            }
            title_text.text = "Update Notice"
            msg_text.text =
                "It looks like your are using an older version of FlicBuzz app.Please update your app at once to the latest version"
            positiveBtn.text = "UPDATE"
            negativeBtn.text = "CANCEL"
            positiveBtn.setOnClickListener {
                // TODO Auto-generated method stub
                val appPackageName = packageName // getPackageName() from Context or Activity object
                try {
                    startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=$appPackageName")))
                } catch (anfe: android.content.ActivityNotFoundException) {
                    startActivity(
                        Intent(
                            Intent.ACTION_VIEW,
                            Uri.parse("https://play.google.com/store/apps/details?id=$appPackageName")
                        )
                    )
                }

                dialog.dismiss()
            }
            negativeBtn.setOnClickListener {
                // TODO Auto-generated method stub
                dialog.dismiss()
            }
            if (!isFinishing)
                dialog.show()
        } catch (e: Exception) {
            e.printStackTrace()
        }

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
        params["language"] = PrefManager.getIn().language.toLowerCase()

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
                        } else if (status == 14) run {
                            Utility.alertDialog(
                                this@LandingPage,
                                jobj.getString("message")
                            )
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
                val myintent = Intent(this@LandingPage, PaymentScreen_Razor::class.java)
                myintent.putExtra("title", "Packages")
                myintent.putExtra("url", "")
                startActivity(myintent)
                overridePendingTransition(R.anim.left_slide_in, R.anim.left_slide_out)

                // Toast.makeText(this, "Clicked item fav", Toast.LENGTH_SHORT).show()
            }
            R.id.nav_package_razor -> {
                trackEvent(this@LandingPage, "MainPage", "Packages Razor")
                val myintent = Intent(this@LandingPage, PaymentScreen_Razor::class.java)
                myintent.putExtra("title", "Packages Razor")
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

            R.id.icon_notifications -> {
                trackEvent(this@LandingPage, "MainPage", "Notifications")
                val intent = Intent(this@LandingPage, Notification_Act::class.java)
                startActivity(intent)
                overridePendingTransition(R.anim.left_slide_in, R.anim.left_slide_out)
            }

            R.id.nav_settings -> {
                trackEvent(this@LandingPage, "MainPage", "Settings")
                val intent = Intent(this@LandingPage, SettingsActivity::class.java)
                startActivity(intent)
                overridePendingTransition(R.anim.left_slide_in, R.anim.left_slide_out)
            }

            R.id.nav_logout -> {
                trackEvent(this@LandingPage, "MainPage", "LogOut")
                alertDialog(this@LandingPage, "Logout", "Are you sure you want to logout?", 2)

            }
            R.id.nav_share -> {
                trackEvent(this@LandingPage, "MainPage", "App Share")
                val appPackageName = packageName
                val sendIntent = Intent()
                sendIntent.action = Intent.ACTION_SEND
                sendIntent.putExtra(Intent.EXTRA_SUBJECT, "FlickBuzz")
                sendIntent.putExtra(
                    Intent.EXTRA_TEXT,
                    "Hi, I Sharing FlicBuzz - A Complete Entertainment App download link from Google Play! \nhttps://go.onelink.me/app/ef427af9"
                )
                sendIntent.type = "text/plain"
                startActivity(sendIntent)

                //flicbuzz.com/fbapp

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
        params["language"] = PrefManager.getIn().language.toLowerCase()
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

                        } else if (status == 14) run {
                            Utility.alertDialog(
                                this@LandingPage,
                                jobj.getString("message")
                            )
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
        params["language"] = PrefManager.getIn().language.toLowerCase()


        RetrofitClient.getInstance()
            .doBackProcess(this@LandingPage, params, "", object : APIResponse {
                override fun onSuccess(res: String?) {
                    try {
                        val jobj = JSONObject(res)
                        val status = jobj.getInt("status")
                        val details = jobj.getString("details")
                        if (status == 1) {
                            Toast.makeText(this@LandingPage, "" + jobj.getString("message"), Toast.LENGTH_LONG).show()

                        } else if (status == 14) run {
                            Utility.alertDialog(
                                this@LandingPage,
                                jobj.getString("message")
                            )
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
        params["language"] = PrefManager.getIn().language.toLowerCase()

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
                            player_in.putExtra("language", "")
                            startActivity(player_in)
                            overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_up);

                        } else if (status == 14) run {
                            Utility.alertDialog(
                                this@LandingPage,
                                jobj.getString("message")
                            )
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

    fun setIcon(): Int {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            return R.mipmap.ic_notification
        } else {
            return R.mipmap.ic_launcher
        }
    }

    fun createNotification(title: String, message: String) {
        /**Creates an explicit intent for an Activity in your app */
        val resultIntent = Intent(mContext, LandingPage::class.java)
        resultIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)

        val resultPendingIntent = PendingIntent.getActivity(
            mContext,
            0 /* Request code */, resultIntent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )

        mBuilder = NotificationCompat.Builder(mContext)
        mBuilder!!.setSmallIcon(setIcon())
        mBuilder!!.setLargeIcon(
            BitmapFactory.decodeResource(
                getApplicationContext().getResources(),
                R.mipmap.ic_launcher
            )
        )
        mBuilder!!.setContentTitle(title)
            .setContentText(message)
            .setAutoCancel(true)
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText(message)
            )
            .setContentIntent(resultPendingIntent)

        mNotificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_HIGH
            val notificationChannel =
                NotificationChannel(NOTIFICATION_CHANNEL_ID, "NOTIFICATION_CHANNEL_NAME", importance)
            notificationChannel.enableLights(true)
            notificationChannel.lightColor = Color.RED
            notificationChannel.enableVibration(true)
            assert(mNotificationManager != null)
            mBuilder!!.setChannelId(NOTIFICATION_CHANNEL_ID)
            mNotificationManager!!.createNotificationChannel(notificationChannel)
        }
        assert(mNotificationManager != null)
        mNotificationManager!!.notify(0 /* Request Code */, mBuilder!!.build())
    }
}