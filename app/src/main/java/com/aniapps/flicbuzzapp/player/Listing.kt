package com.aniapps.flicbuzzapp.player

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.aniapps.flicbuzzapp.R
import com.aniapps.flicbuzzapp.adapters.ListingAdapter
import com.aniapps.flicbuzzapp.models.MyVideos
import com.aniapps.flicbuzzapp.models.SectionDataModel
import java.util.ArrayList

class Listing : AppCompatActivity() {

    private var toolbar: Toolbar? = null


    internal lateinit var allSampleData: ArrayList<SectionDataModel>


    @SuppressLint("WrongConstant")
    protected override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.listingactivity)
        toolbar = findViewById<View>(R.id.toolbar11) as Toolbar
        allSampleData = ArrayList<SectionDataModel>()
        if (toolbar != null) {
            setSupportActionBar(toolbar)
            toolbar!!.title = "G PlayStore"
        }
        createDummyData()
        val my_recycler_view = findViewById<View>(R.id.my_recycler_view) as RecyclerView
        my_recycler_view.setHasFixedSize(true)
        val adapter = ListingAdapter(this, allSampleData)
        my_recycler_view.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        my_recycler_view.adapter = adapter
    }

    fun createDummyData() {
        val myCategories = resources.getStringArray(R.array.Topics)
        val myUrls = resources.getStringArray(R.array.Urls)
        for (i in myCategories.indices) {
            val singleItem = ArrayList<MyVideos>()

            for (j in 0..5) {
             //   singleItem.add(MyVideos("","","Title $j", myUrls[i], ""))
            }
            val dm = SectionDataModel(myCategories[i], singleItem)
            allSampleData.add(dm)

        }
    }


}