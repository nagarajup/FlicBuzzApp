package com.aniapps.flicbuzz.adapters

import android.app.Activity
import android.content.Intent
import android.os.Build
import android.support.v7.widget.RecyclerView
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.aniapps.flicbuzz.player.MyPlayer
import com.aniapps.flicbuzz.R
import com.aniapps.flicbuzz.models.MyVideos
import com.squareup.picasso.Picasso

class PlayerAdapter (var context: Activity, var itemsList: ArrayList<MyVideos>, var from:String) :
    RecyclerView.Adapter<PlayerAdapter.PlayerRowHolder>() {
    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): PlayerRowHolder {
        val v = LayoutInflater.from(viewGroup.context).inflate(R.layout.playerlistingcard, null)
        return PlayerRowHolder(v)
    }

    override fun onBindViewHolder(holder: PlayerRowHolder, i: Int) {

        val singleItem = itemsList[i]

        holder.tvTitle.setText(singleItem.headline)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            holder.tvDesc.setText(
                Html.fromHtml(
                    "Adgully Network &nbsp; &nbsp; 1K Views &nbsp;&nbsp; - 1 day ago",
                    Html.FROM_HTML_MODE_COMPACT
                )
            );
        } else {
            holder.tvDesc.setText(Html.fromHtml("Adgully Network &nbsp; &nbsp; 1K Views &nbsp;&nbsp; - 1 day ago"));
        }
        holder.tvDesc.setText(singleItem.description)



        Picasso.with(context)
            .load(singleItem.thumb)
            .centerInside()
            .error(R.mipmap.launcher_icon)
            .into(holder.itemImage);


        holder.itemImage.setOnClickListener({

            if (from.equals("main")) {
                val player_in = Intent(it.context, MyPlayer::class.java)
                player_in.putExtra("url", singleItem.video_filename)
                player_in.putExtra("title", singleItem.headline)
                player_in.putExtra("desc", singleItem.description)
                player_in.putExtra("id", singleItem.id)

                it.context.startActivity(player_in)
                context.overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_up);
            } else {
                Toast.makeText(it.context, "Clicked on " + singleItem.headline, Toast.LENGTH_SHORT).show()
            }
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

    inner class PlayerRowHolder(view: View) : RecyclerView.ViewHolder(view) {

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