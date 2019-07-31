package com.aniapps.flicbuzzapp.adapters

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.aniapps.flicbuzzapp.AppApplication
import com.aniapps.flicbuzzapp.AppConstants
import com.aniapps.flicbuzzapp.player.MyPlayer
import com.aniapps.flicbuzzapp.R
import com.aniapps.flicbuzzapp.activities.PaymentScreen_Razor
import com.aniapps.flicbuzzapp.activities.SignIn
import com.aniapps.flicbuzzapp.models.MyVideos
import com.aniapps.flicbuzzapp.networkcall.APIResponse
import com.aniapps.flicbuzzapp.networkcall.RetrofitClient
import com.aniapps.flicbuzzapp.utils.PrefManager
import com.aniapps.flicbuzzapp.utils.Utility
import com.google.gson.Gson
import com.squareup.picasso.Picasso
import io.branch.referral.util.BRANCH_STANDARD_EVENT
import io.branch.referral.util.BranchEvent
import io.branch.referral.util.CurrencyType
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
        holder.tvViews.setText(singleItem.video_date2 + " | " + singleItem.views + " " + "Views")
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
        params["language"] = PrefManager.getIn().language.toLowerCase()
        RetrofitClient.getInstance()
            .doBackProcess(context, params, "", object : APIResponse {
                override fun onSuccess(res: String?) {
                    try {
                        val jobj = JSONObject(res)
                        val status = jobj.getInt("status")
                        val details = jobj.getString("details")
                        if (status == 1) {
                            // LandingPage.playingVideos.add(myVideo)
                            var myurl2 = "";
                            val jsonArray = jobj.getJSONArray("next")
                            for (i in 0 until jsonArray.length()) {
                                var lead = Gson().fromJson(
                                    jsonArray.get(i).toString(),
                                    MyVideos::class.java
                                )
                                //  LandingPage.playingVideos.add(lead)
                            }

                            BranchEvent(BRANCH_STANDARD_EVENT.VIEW_ITEM)
                                .setDescription(myVideo.headline)
                                .logEvent(context)

                            if (from.equals("main") || from.equals("fav")) {
                                val player_in = Intent(context, MyPlayer::class.java)
                                player_in.putExtra("playingVideo", myVideo)
                                player_in.putExtra("sequence", itemsList)
                                player_in.putExtra("pos", pos)
                                player_in.putExtra("from", from)
                                player_in.putExtra("language", "")
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
                                player_in.putExtra("language", "")
                                context.finish()
                                context.startActivity(player_in)
                                context.overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_up);
                            }
                        } else if (status == 14) run {
                            Utility.alertDialog(
                                context,
                                jobj.getString("message")
                            )
                        }  else if (status == 99) {
                            if (jobj.getString("next_screen").equals("require_registration")) {
                                val intent = Intent(context, SignIn::class.java)
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                                context.startActivity(intent)
                                context.overridePendingTransition(R.anim.left_slide_in, R.anim.left_slide_out)
                            } else {
                                val intent = Intent(context, PaymentScreen_Razor::class.java)
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                                context.startActivity(intent)
                                context.overridePendingTransition(R.anim.left_slide_in, R.anim.left_slide_out)
                            }
                        }else {
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