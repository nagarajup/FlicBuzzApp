package com.aniapps.flicbuzzapp.player

import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.Toolbar
import android.view.MenuItem
import android.view.View
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import com.aniapps.flicbuzzapp.R
import com.aniapps.flicbuzzapp.adapters.MainAdapter
import com.aniapps.flicbuzzapp.models.MyVideos
import com.aniapps.flicbuzzapp.networkcall.APIResponse
import com.aniapps.flicbuzzapp.networkcall.RetrofitClient
import com.aniapps.flicbuzzapp.utils.PrefManager
import com.aniapps.flicbuzzapp.utils.Utility
import com.google.gson.Gson
import org.json.JSONArray
import org.json.JSONObject
import java.util.ArrayList

class FavoriteAct : AppCompatActivity() {
    internal lateinit var rc_list: RecyclerView
    internal lateinit var myvideos: ArrayList<MyVideos>
    internal var loading = false
    internal var scrollFlag = false
    internal var layoutManager: LinearLayoutManager? = null
    private var pbr: ProgressBar? = null
    internal var pageNo = 1
    internal lateinit var laynofav: LinearLayout
    internal var my_recycler_view: RecyclerView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.favourite_list)
        val mToolbar = findViewById<View>(R.id.toolbar) as Toolbar
        val header_title = findViewById<View>(R.id.tvheader) as TextView

        setSupportActionBar(mToolbar)
        supportActionBar!!.setDisplayShowTitleEnabled(false)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        header_title.text = "My Favourites"
        myvideos = ArrayList()

        my_recycler_view = findViewById<RecyclerView>(R.id.fmy_recyclerview)
        laynofav = findViewById<LinearLayout>(R.id.lay_no_fav)
        pbr = findViewById(R.id.fload_progress) as ProgressBar
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

    internal lateinit var adapter: MainAdapter
    private fun apiCall() {
        loading = true
        var from = "" as String
        val params = HashMap<String, String>()
        params["action"] = "get_favorite_list"
        params["language"] = PrefManager.getIn().language.toLowerCase()
        params["page_number"] = "" + pageNo
        if (pageNo == 1) {
            from = "";
        } else {
            from = "online"
            pbr!!.visibility = View.VISIBLE
        }
        RetrofitClient.getInstance()
            .doBackProcess(this@FavoriteAct, params, from, object : APIResponse {
                override fun onSuccess(res: String?) {
                    try {
                        val jobj = JSONObject(res)
                        val status = jobj.getInt("status")
                        val details = jobj.getString("details")
                        if (status == 1) {
                            loading = false
                            val jsonArray = jobj.getJSONArray("data")
                            if (jsonArray.length() > 0) {
                                my_recycler_view!!.visibility = View.VISIBLE
                                if (pageNo == 1) {
                                    myvideos.clear()
                                }
                                myData(jsonArray.toString())
                                if (myvideos.size < 20) {
                                    scrollFlag = true
                                }
                                if (pageNo == 1) {
                                    adapter = MainAdapter(this@FavoriteAct, myvideos, "fav")
                                    layoutManager = LinearLayoutManager(applicationContext)
                                    my_recycler_view!!.setLayoutManager(layoutManager)
                                    adapter.notifyDataSetChanged()
                                    my_recycler_view!!.adapter = adapter
                                } else {
                                    adapter.notifyDataSetChanged()
                                }
                            } else {
                                laynofav.visibility = View.VISIBLE
                            }
                        } else if (status == 14) run {
                            Utility.alertDialog(
                                this@FavoriteAct,
                                jobj.getString("message")
                            )
                        } else {
                            Toast.makeText(this@FavoriteAct, "status" + status, Toast.LENGTH_LONG).show()
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
                    Toast.makeText(this@FavoriteAct, "status" + res, Toast.LENGTH_LONG).show()
                    if (pbr!!.visibility == View.VISIBLE) {
                        pbr!!.visibility = View.GONE
                    }
                }
            })
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        finish();
        overridePendingTransition(R.anim.right_slide_in, R.anim.right_slide_out)
        return super.onOptionsItemSelected(item)
    }
}