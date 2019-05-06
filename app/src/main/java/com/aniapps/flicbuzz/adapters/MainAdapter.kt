package com.aniapps.flicbuzz.adapters

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.support.constraint.ConstraintLayout
import android.support.v4.content.ContextCompat
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import com.aniapps.flicbuzz.AppApplication
import com.aniapps.flicbuzz.player.MyPlayer
import com.aniapps.flicbuzz.R
import com.aniapps.flicbuzz.models.MyVideos
import com.aniapps.flicbuzz.networkcall.APIResponse
import com.aniapps.flicbuzz.networkcall.RetrofitClient
import com.aniapps.flicbuzz.player.LandingPage
import com.aniapps.flicbuzz.player.MyPlayerIns
import com.aniapps.flicbuzz.utils.PrefManager
import com.google.gson.Gson
import com.squareup.picasso.Picasso
import org.json.JSONObject


class MainAdapter(var context: Activity, var itemsList: ArrayList<MyVideos>, var from: String) :
    RecyclerView.Adapter<MainAdapter.SingleItemRowHolder>() {

    internal var imageItem_height_calculation = 0

    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): SingleItemRowHolder {
        val v = LayoutInflater.from(viewGroup.context).inflate(R.layout.list_single_card_new, null)
        return SingleItemRowHolder(v)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: SingleItemRowHolder, i: Int) {

        val singleItem = itemsList[i]
        holder.tvTitle.setText(singleItem.headline)
        holder.tvDesc.setText(singleItem.description)
        holder.tvViews.setText("" + singleItem.views + " " + "Views")
        if (from.equals("main") || from.equals("fav")) {
            holder.tvDesc.setTextColor(ContextCompat.getColor(context, R.color.lightgray))

        } else {
            holder.tvDesc.setTextColor(ContextCompat.getColor(context, R.color.white))
        }
        AppApplication.myImgeRes(imageItem_height_calculation, context, holder.itemImage)
        Picasso.with(context)
            .load(singleItem.thumb)
            .error(R.mipmap.launcher_icon)
            .into(holder.itemImage);
        holder.lay_card.setOnClickListener({
            impressionTracker(from, singleItem, i)
        })


    }

    private fun impressionTracker(from: String, myVideo: MyVideos, pos: Int) {
        val params = HashMap<String, String>()
        params["action"] = "video_impression_tracker"
        params["video_id"] = myVideo.id
        RetrofitClient.getInstance()
            .doBackProcess(context, params, "", object : APIResponse {
                override fun onSuccess(res: String?) {
                    try {
                        val jobj = JSONObject(res)
                        val status = jobj.getInt("status")
                        val details = jobj.getString("details")
                        if (status == 1) {
                            LandingPage.playingVideos.add(myVideo)
                            var myurl2 = "";
                            val jsonArray = jobj.getJSONArray("next")
                            for (i in 0 until jsonArray.length()) {
                                var lead = Gson().fromJson(
                                    jsonArray.get(i).toString(),
                                    MyVideos::class.java
                                )
                                LandingPage.playingVideos.add(lead)
                            }
                            if (from.equals("main") || from.equals("fav")) {
                                val player_in = Intent(context, MyPlayer::class.java)
                                player_in.putExtra("playingVideo", myVideo)
                                player_in.putExtra("sequence", itemsList)
                                player_in.putExtra("pos", pos)
                                player_in.putExtra("from", from)
                                context.startActivity(player_in)
                                context.overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_up);
                            } else {
                               /* try {
                                    //  (context as MyPlayer).refresh(myVideo.id, myVideo.video_filename, myurl2, "adapter")
                                    Log.e("@@@@", "ins in adapter")
                                } catch (exception: ClassCastException) {
                                    // do something
                                }*/
                                val player_in = Intent(context, MyPlayer::class.java)
                                player_in.putExtra("playingVideo", myVideo)
                                player_in.putExtra("from", from)
                                player_in.putExtra("sequence", itemsList)
                                player_in.putExtra("pos", pos)
                                context.finish()
                                context.startActivity(player_in)
                                context.overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_up);
                            }
                        } else {
                            Toast.makeText(context, "status" + details, Toast.LENGTH_LONG).show()
                        }

                    } catch (e: Exception) {
                        e.printStackTrace()

                    }
                }

                override fun onFailure(res: String?) {
                    Toast.makeText(context, "status" + res, Toast.LENGTH_LONG).show()

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
        lateinit var lay_card: ConstraintLayout
        var itemImage: ImageView


        init {

            this.lay_card = view.findViewById<ConstraintLayout>(R.id.lay_card)
            this.tvTitle = view.findViewById<TextView>(R.id.tvTitle)
            this.tvDesc = view.findViewById<TextView>(R.id.tvDesc)
            this.tvViews = view.findViewById<TextView>(R.id.tvViews)
            this.itemImage = view.findViewById<ImageView>(R.id.itemImage)


            // view.setOnClickListener { v -> Toast.makeText(v.context, tvTitle.text, Toast.LENGTH_SHORT).show() }


        }

    }


}