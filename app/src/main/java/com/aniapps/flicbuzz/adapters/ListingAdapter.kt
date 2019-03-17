package com.aniapps.flicbuzz.adapters

import android.content.Context
import android.content.Intent
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.aniapps.flicbuzz.Listing
import com.aniapps.flicbuzz.MyPlayer
import com.aniapps.flicbuzz.R
import com.aniapps.flicbuzz.models.SectionDataModel
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import java.util.*

class ListingAdapter(var mContext: Context, var dataList: ArrayList<SectionDataModel>) :
    RecyclerView.Adapter<ListingAdapter.ItemRowHolder>() {


    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): ItemRowHolder {
        val v = LayoutInflater.from(viewGroup.context).inflate(R.layout.list_item, null)
        return ItemRowHolder(v)
    }

    override fun onBindViewHolder(itemRowHolder: ItemRowHolder, i: Int) {

        val sectionName = dataList[i].headerTitle

        val singleSectionItems = dataList[i].allItemsInSection

        itemRowHolder.itemTitle.setText(sectionName)

        val itemListDataAdapter = SectionListDataAdapter(mContext, singleSectionItems)

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
            /*  Toast.makeText(
                  v.context,
                  "click event on more, $sectionName",
                  Toast.LENGTH_SHORT
              ).show()*/

            val i = Intent(v.context, MyPlayer::class.java)
            v.context.startActivity(i)
        })

       /* GlideApp.with(context)
            .asBitmap()
            .load(FILE_URL)
            .diskCacheStrategy(DiskCacheStrategy.DATA)
            .into(iv_picture);*/


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