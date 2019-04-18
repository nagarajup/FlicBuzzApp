package com.aniapps.flicbuzz.adapters

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Build
import android.support.v4.content.ContextCompat
import android.support.v7.widget.RecyclerView
import android.text.Html
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
import android.widget.TextView.BufferType
import android.text.method.LinkMovementMethod
import android.view.ViewTreeObserver.OnGlobalLayoutListener
import android.text.SpannableStringBuilder
import android.text.Spanned





class SectionListDataAdapter(var context: Activity, var itemsList: ArrayList<MyVideos>, var from:String) :
    RecyclerView.Adapter<SectionListDataAdapter.SingleItemRowHolder>() {
    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): SingleItemRowHolder {
        val v = LayoutInflater.from(viewGroup.context).inflate(R.layout.list_single_card, null)
        return SingleItemRowHolder(v)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: SingleItemRowHolder, i: Int) {

        val singleItem = itemsList[i]

        holder.tvTitle.setText(singleItem.headline)
        holder.tvDesc.setText(singleItem.description)
        holder.tvViews.setText(""+singleItem.views+" "+"Views")

       /* if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            holder.tvDesc.setText(Html.fromHtml("<b>Adgully Network</b> &nbsp; &nbsp;&nbsp; 1K Views &nbsp;&nbsp;&nbsp; - 1 day ago", Html.FROM_HTML_MODE_COMPACT));
        } else {
            holder.tvDesc.setText(Html.fromHtml("<b>Adgully Network</b> &nbsp; &nbsp;&nbsp; 1K Views &nbsp;&nbsp;&nbsp; - 1 day ago"));
        }*/

        if(!from.equals("main")) {
            holder.tvDesc.setTextColor(ContextCompat.getColor(context, R.color.white))
        }else{
            holder.tvDesc.setTextColor(ContextCompat.getColor(context, R.color.lightgray))
        }

       Glide.with(context)
            .load(singleItem.thumb)
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .centerCrop()
            .error(R.mipmap.launcher_icon)
            .into(holder.itemImage);

        holder.itemImage.setOnClickListener({
            if(from.equals("main")) {
                val player_in = Intent(it.context, MyPlayer::class.java)
                player_in.putExtra("url", singleItem.video_filename)
                player_in.putExtra("title", singleItem.headline)
                player_in.putExtra("desc", singleItem.description)
                player_in.putExtra("id", singleItem.id)
                it.context.startActivity(player_in)
                context.overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_up);
            }else{
                val player_in = Intent(it.context, MyPlayer::class.java)
                player_in.putExtra("url", singleItem.video_filename)
                player_in.putExtra("title", singleItem.headline)
                player_in.putExtra("desc", singleItem.description)
                player_in.putExtra("id", singleItem.id)
                context.finish()
                it.context.startActivity(player_in)
                context.overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_up);

               // Toast.makeText(it.context,"Clicked on "+singleItem.headline,Toast.LENGTH_SHORT).show()
            }
        })
    }

    override fun getItemCount(): Int {
        return itemsList.size
    }

    inner class SingleItemRowHolder(view: View) : RecyclerView.ViewHolder(view) {

        var tvTitle: TextView
        var tvDesc: TextView
        var tvViews: TextView

         var itemImage: ImageView


        init {

            this.tvTitle = view.findViewById<View>(R.id.tvTitle) as TextView
            this.tvDesc = view.findViewById<View>(R.id.tvDesc) as TextView
            this.tvViews = view.findViewById<View>(R.id.tvViews) as TextView
            this.itemImage = view.findViewById<View>(R.id.itemImage) as ImageView


            view.setOnClickListener { v -> Toast.makeText(v.context, tvTitle.text, Toast.LENGTH_SHORT).show() }


        }

    }



}