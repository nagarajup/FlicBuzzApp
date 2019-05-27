package com.aniapps.flicbuzzapp.adapters

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.support.annotation.RequiresApi
import android.support.constraint.ConstraintLayout
import android.support.v4.content.ContextCompat
import android.support.v7.widget.CardView
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.aniapps.flicbuzzapp.AppApplication
import com.aniapps.flicbuzzapp.R
import com.aniapps.flicbuzzapp.activities.PaymentScreen_New
import com.aniapps.flicbuzzapp.db.LocalDB
import com.aniapps.flicbuzzapp.db.NotificationData
import com.aniapps.flicbuzzapp.models.MyVideos
import com.aniapps.flicbuzzapp.networkcall.APIResponse
import com.aniapps.flicbuzzapp.networkcall.RetrofitClient
import com.aniapps.flicbuzzapp.player.MyPlayer
import com.aniapps.flicbuzzapp.utils.Utility
import com.google.gson.Gson
import com.squareup.picasso.Picasso
import org.json.JSONObject

class NotificationAdapter(var context: Activity, var itemsList: List<NotificationData>) :
    RecyclerView.Adapter<NotificationAdapter.SingleItemRowHolder>() {

    internal var imageItem_height_calculation = 0

    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): SingleItemRowHolder {
        val v = LayoutInflater.from(viewGroup.context).inflate(R.layout.adapter_notification, null)
        return SingleItemRowHolder(v)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: SingleItemRowHolder, i: Int) {

        val singleItem = itemsList[i]
        holder.tv_title.setText(singleItem.push_title)
        holder.tv_message.setText(singleItem.push_msg+" Testing testing testing testing testing testing testing testing ")
        holder.tv_time.setText(singleItem.push_time)

        if(!singleItem.push_img_url.equals("")) {
            AppApplication.myImgeRes(imageItem_height_calculation, context, holder.itemImage)
            Picasso.with(context)
                .load(singleItem.push_img_url)
                .error(R.mipmap.launcher_icon)
                .into(holder.itemImage);
        }else{
            holder.cardView.visibility=View.GONE
        }

holder.img_notification_close.setOnClickListener({
    LocalDB.getInstance(context).deleteNotification(singleItem.push_time)
})
        holder.lay_card.setOnClickListener({
            if (singleItem.push_type.equals("update")) {
                val appPackageName = context.packageName
                try {
                    val notificationIntent = Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse("market://details?id=$appPackageName")
                    )
                    notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    context.startActivity(notificationIntent)
                } catch (anfe: android.content.ActivityNotFoundException) {
                    val notificationIntent = Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse("http://play.google.com/store/apps/details?id=$appPackageName")
                    )
                    notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    context.startActivity(notificationIntent)
                }

            } else if (singleItem.push_type.equals("package")) {
                try {
                    val notificationIntent = Intent(
                        context,
                        PaymentScreen_New::class.java
                    )
                    notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    context.startActivity(notificationIntent)
                } catch (e: Exception) {
                    e.printStackTrace()
                }

            } else {
                ApiCall(singleItem.push_video_id,singleItem.push_video_language)
            }
        })


    }

    fun ApiCall(videoid:String,language:String) {
        val params = java.util.HashMap<String, String>()

        params["action"] = "get_video_by_id"
        params["video_id"] = videoid
        params["language"] =language


        RetrofitClient.getInstance().doBackProcess(context, params, "online", object : APIResponse {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            override fun onSuccess(result: String) {
                try {
                    val jsonObject = JSONObject(result)
                    val status = jsonObject.getInt("status")
                    if (status == 1) {
                        val data = jsonObject.getJSONObject("data")
                        val videodata = data.getJSONObject("video")

                        val lead = Gson().fromJson<MyVideos>(videodata.toString(), MyVideos::class.java)

                        var itemsList2 =ArrayList<MyVideos>()
                        itemsList2.add(lead)
                        val player_in = Intent(context, MyPlayer::class.java)
                        player_in.putExtra("playingVideo", lead)
                        player_in.putExtra("sequence", itemsList2)
                        player_in.putExtra("from", "notification")
                        context.startActivity(player_in)
                        context.overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_up)
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    Utility.alertDialog(context, "Alert", e.message)
                }

            }

            override fun onFailure(res: String) {
                Utility.alertDialog(context, "Alert", res)
            }
        })
    }




    override fun getItemCount(): Int {
        return itemsList.size
    }

    inner class SingleItemRowHolder(view: View) : RecyclerView.ViewHolder(view) {

        var tv_title: TextView
        var tv_message: TextView
        var tv_time: TextView
        var itemImage: ImageView
        var img_notification_close: ImageView
        var lay_card: ConstraintLayout
        var cardView: CardView


        init {
            this.lay_card = view.findViewById<ConstraintLayout>(R.id.lay_card)
            this.cardView = view.findViewById<CardView>(R.id.cardView)
            this.tv_title = view.findViewById<TextView>(R.id.tv_notification_title)
            this.tv_message = view.findViewById<TextView>(R.id.tv_notification_msg)
            this.tv_time = view.findViewById<TextView>(R.id.tv_notification_time)
            this.itemImage = view.findViewById<ImageView>(R.id.imag_itemImage)
            this.img_notification_close = view.findViewById<ImageView>(R.id.img_notification_close)
        }

    }


}