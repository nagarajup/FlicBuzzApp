package com.aniapps.flicbuzz.adapters

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.support.v4.content.ContextCompat
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.aniapps.flicbuzz.player.MyPlayer
import com.aniapps.flicbuzz.R
import com.aniapps.flicbuzz.models.MyVideos
import com.squareup.picasso.Picasso


class MainAdapter(var context: Activity, var itemsList: ArrayList<MyVideos>, var from: String) :
    RecyclerView.Adapter<MainAdapter.SingleItemRowHolder>() {

    internal var imageheight = 0f
   internal var imageItem_height_calculation=0
    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): SingleItemRowHolder {
        val v = LayoutInflater.from(viewGroup.context).inflate(R.layout.list_single_card, null)
        return SingleItemRowHolder(v)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: SingleItemRowHolder, i: Int) {

        val singleItem = itemsList[i]

        holder.tvTitle.setText(singleItem.headline)
        holder.tvDesc.setText(singleItem.description)
        holder.tvViews.setText("" + singleItem.views + " " + "Views")

        /* if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
             holder.tvDesc.setText(Html.fromHtml("<b>Adgully Network</b> &nbsp; &nbsp;&nbsp; 1K Views &nbsp;&nbsp;&nbsp; - 1 day ago", Html.FROM_HTML_MODE_COMPACT));
         } else {
             holder.tvDesc.setText(Html.fromHtml("<b>Adgully Network</b> &nbsp; &nbsp;&nbsp; 1K Views &nbsp;&nbsp;&nbsp; - 1 day ago"));
         }*/


        if (!from.equals("main")) {
            holder.tvDesc.setTextColor(ContextCompat.getColor(context, R.color.white))
        } else {
            holder.tvDesc.setTextColor(ContextCompat.getColor(context, R.color.lightgray))
        }
       /* holder.itemImage.requestLayout()
        holder.itemImage.layoutParams.height=200;*/

       /* if (imageItem_height_calculation == 0) {
            imageItem_height_calculation = 1
            holder.itemImage.getLayoutParams().height = (((context
                .resources.displayMetrics.widthPixels - dpToPx(10, context)) / 2)  / 1.33).toInt()
            imageheight = (((context
                .resources.displayMetrics.widthPixels - dpToPx(10, context)) / 2)  / 1.33).toInt().toFloat()
        } else {
            holder.itemImage.getLayoutParams().height = imageheight.toInt()
        }*/


        Picasso.with(context)
            .load(singleItem.thumb)
            .fit()
            .error(R.mipmap.launcher_icon)
            .into(holder.itemImage);

        holder.lay_card.setOnClickListener({
            if (from.equals("main")) {
                val player_in = Intent(it.context, MyPlayer::class.java)
                player_in.putExtra("url", singleItem.video_filename)
                player_in.putExtra("title", singleItem.headline)
                player_in.putExtra("desc", singleItem.description)
                player_in.putExtra("id", singleItem.id)
                it.context.startActivity(player_in)
                context.overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_up);
            } else {
                val player_in = Intent(it.context, MyPlayer::class.java)
                player_in.putExtra("url", singleItem.video_filename)
                player_in.putExtra("title", singleItem.headline)
                player_in.putExtra("desc", singleItem.description)
                player_in.putExtra("id", singleItem.id)
                context.finish()
                it.context.startActivity(player_in)
                context.overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_up);
            }
        })


    }

    fun dpToPx(dp: Int, context: Context): Int {
        val scale = context.resources.displayMetrics.density
        return (dp * scale + 0.5f).toInt()
    }

    override fun getItemCount(): Int {
        return itemsList.size
    }

    inner class SingleItemRowHolder(view: View) : RecyclerView.ViewHolder(view) {

        var tvTitle: TextView
        var tvDesc: TextView
        var tvViews: TextView
        lateinit var lay_card: LinearLayout
        var itemImage: ImageView


        init {

            this.lay_card = view.findViewById<LinearLayout>(R.id.lay_card)
            this.tvTitle = view.findViewById<TextView>(R.id.tvTitle)
            this.tvDesc = view.findViewById<TextView>(R.id.tvDesc)
            this.tvViews = view.findViewById<TextView>(R.id.tvViews)
            this.itemImage = view.findViewById<ImageView>(R.id.itemImage)


            // view.setOnClickListener { v -> Toast.makeText(v.context, tvTitle.text, Toast.LENGTH_SHORT).show() }


        }

    }


}