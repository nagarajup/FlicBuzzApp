package com.aniapps.flicbuzz.adapters

import android.content.Context
import android.content.Intent
import android.support.v4.content.ContextCompat
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.aniapps.flicbuzz.MyPlayer
import com.aniapps.flicbuzz.R
import com.aniapps.flicbuzz.models.MyVideos
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy

class SectionListDataAdapter(var context: Context, var itemsList: ArrayList<MyVideos>) :
    RecyclerView.Adapter<SectionListDataAdapter.SingleItemRowHolder>() {
    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): SingleItemRowHolder {
        val v = LayoutInflater.from(viewGroup.context).inflate(R.layout.list_single_card, null)
        return SingleItemRowHolder(v)
    }

    override fun onBindViewHolder(holder: SingleItemRowHolder, i: Int) {

        val singleItem = itemsList[i]

        holder.tvTitle.setText(singleItem.headline)
        holder.tvDesc.setText(singleItem.description)

       Glide.with(context)
            .load(singleItem.thumb)
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .centerCrop()
            .error(R.mipmap.launcher_icon)
            .into(holder.itemImage);


        holder.itemImage.setOnClickListener({

            val player_in = Intent(it.context, MyPlayer::class.java)
            player_in.putExtra("url",singleItem.video_filename)
            it.context.startActivity(player_in)
        })

        /* Glide.with(mContext)
                .load(feedItem.getImageURL())
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .centerCrop()
                .error(R.drawable.bg)
                .into(feedListRowHolder.thumbView);*/
    }

    override fun getItemCount(): Int {
        return itemsList.size
    }

    inner class SingleItemRowHolder(view: View) : RecyclerView.ViewHolder(view) {

        var tvTitle: TextView
        var tvDesc: TextView

         var itemImage: ImageView


        init {

            this.tvTitle = view.findViewById<View>(R.id.tvTitle) as TextView
            this.tvDesc = view.findViewById<View>(R.id.tvDesc) as TextView
            this.itemImage = view.findViewById<View>(R.id.itemImage) as ImageView


            view.setOnClickListener { v -> Toast.makeText(v.context, tvTitle.text, Toast.LENGTH_SHORT).show() }


        }

    }
}