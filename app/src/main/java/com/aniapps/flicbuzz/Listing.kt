package com.aniapps.flicbuzz

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.Toolbar
import android.view.View
import com.aniapps.flicbuzz.adapters.ListingAdapter
import com.aniapps.flicbuzz.models.SectionDataModel
import com.aniapps.flicbuzz.models.SingleItemModel
import java.util.ArrayList

class Listing : AppCompatActivity() {

    private var toolbar: Toolbar? = null


    internal lateinit var allSampleData: ArrayList<SectionDataModel>


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
            val singleItem = ArrayList<SingleItemModel>()

            for (j in 0..5) {
                singleItem.add(SingleItemModel("Title $j", myUrls[i], ""))
            }
            val dm = SectionDataModel(myCategories[i], singleItem)
            allSampleData.add(dm)

        }
    }


}