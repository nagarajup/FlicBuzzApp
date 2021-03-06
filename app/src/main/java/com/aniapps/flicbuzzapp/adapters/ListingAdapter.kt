package com.aniapps.flicbuzzapp.adapters

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.aniapps.flicbuzzapp.R
import com.aniapps.flicbuzzapp.models.SectionDataModel
import java.util.*

class ListingAdapter(var mContext: Activity, var dataList: ArrayList<SectionDataModel>) :
    RecyclerView.Adapter<ListingAdapter.ItemRowHolder>() {


    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): ItemRowHolder {
        val v = LayoutInflater.from(viewGroup.context).inflate(R.layout.list_item, null)
        return ItemRowHolder(v)
    }

    override fun onBindViewHolder(itemRowHolder: ItemRowHolder, i: Int) {

        val sectionName = dataList[i].category_name
        val singleSectionItems = dataList[i].videos

        itemRowHolder.itemTitle.setText(sectionName)

        val itemListDataAdapter = MainAdapter(mContext, singleSectionItems,"")

        itemRowHolder.recycler_view_list.setHasFixedSize(true)
        itemRowHolder.recycler_view_list.setLayoutManager(
            LinearLayoutManager(
                mContext,
                LinearLayoutManager.HORIZONTAL,
                false
            )
        )
        itemRowHolder.recycler_view_list.setAdapter(itemListDataAdapter)


        itemRowHolder.btnMore.setOnClickListener(View.OnClickListener { v ->
              Toast.makeText(
                  v.context,
                  "clicked event on more, $sectionName",
                  Toast.LENGTH_SHORT
              ).show()


        })




    }

    override fun getItemCount(): Int {
        return dataList?.size ?: 0
    }

    inner class ItemRowHolder(view: View) : RecyclerView.ViewHolder(view) {
        var itemTitle: TextView
        var recycler_view_list: RecyclerView
        var btnMore: TextView

        init {
            this.itemTitle = view.findViewById<View>(R.id.itemTitle) as TextView
            this.recycler_view_list = view.findViewById<View>(R.id.recycler_view_list) as RecyclerView
            this.btnMore = view.findViewById<View>(R.id.btnMore) as TextView
        }

    }
}