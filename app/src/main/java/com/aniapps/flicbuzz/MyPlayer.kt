package com.aniapps.flicbuzz

import android.annotation.SuppressLint
import android.app.PictureInPictureParams
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.support.annotation.RequiresApi
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.text.Html
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.method.LinkMovementMethod
import android.util.Log
import android.util.Rational
import android.view.View
import android.view.ViewTreeObserver
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import com.aniapps.flicbuzz.adapters.MySpannable
import com.aniapps.flicbuzz.adapters.PlayerAdapter
import com.aniapps.flicbuzz.adapters.SectionListDataAdapter
import com.aniapps.flicbuzz.models.MyVideos
import com.aniapps.flicbuzz.networkcall.APIResponse
import com.aniapps.flicbuzz.networkcall.RetrofitClient
import com.google.android.exoplayer2.C
import com.google.android.exoplayer2.ExoPlayerFactory
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.source.ExtractorMediaSource
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.source.TrackGroupArray
import com.google.android.exoplayer2.source.hls.HlsMediaSource
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.trackselection.MappingTrackSelector
import com.google.android.exoplayer2.trackselection.TrackSelectionArray
import com.google.android.exoplayer2.ui.PlayerView
import com.google.android.exoplayer2.upstream.*
import com.google.android.exoplayer2.util.Util
import com.google.android.exoplayer2.util.Util.SDK_INT
import com.google.android.exoplayer2.util.Util.getUserAgent
import com.google.gson.Gson
import kotlinx.android.synthetic.main.myplayer.*
import org.json.JSONArray
import org.json.JSONObject
import java.util.ArrayList

class MyPlayer : AppCompatActivity() {

    companion object {
        private const val KEY_PLAY_WHEN_READY = "play_when_ready"
        private const val KEY_WINDOW = "window"
        private const val KEY_POSITION = "position"
    }

    private lateinit var player: SimpleExoPlayer
    private val playerView: PlayerView by lazy { findViewById<PlayerView>(R.id.video_view) }


    private var shouldAutoPlay: Boolean = true
    private var trackSelector: DefaultTrackSelector? = null
    private var lastSeenTrackGroupArray: TrackGroupArray? = null
    private lateinit var mediaDataSourceFactory: DataSource.Factory
    private lateinit var dataFactory: DefaultDataSourceFactory
    var isInPipMode: Boolean = false
    var isPIPModeeEnabled: Boolean = true

    private val bandwidthMeter: BandwidthMeter = DefaultBandwidthMeter()

    private var playWhenReady: Boolean = false
    private var currentWindow: Int = 0
    private var playbackPosition: Long = 0
    private var mUrl: String = "";
    private var play_title: String = "";
    private var play_desc: String = "";
    private var play_id: String = "";

    var mediaSource: MediaSource? = null;
    private val progressBar: ProgressBar by lazy { findViewById<ProgressBar>(R.id.progress_bar) }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.myplayer)
        mUrl = intent.getStringExtra("url")
        play_desc = intent.getStringExtra("desc")
        play_title = intent.getStringExtra("title")
        play_id = intent.getStringExtra("id")


        Log.e("player title", play_title)
        Log.e("player desc", play_desc)


        tv_play_title.setText(play_title)
        tv_play_description.setText(play_desc)

        makeTextViewResizable(tv_play_description,3,"View More",true)

        LoginApi()

        if (savedInstanceState != null) {

            with(savedInstanceState) {
                playWhenReady = getBoolean(KEY_PLAY_WHEN_READY)
                currentWindow = getInt(KEY_WINDOW)
                playbackPosition = getLong(KEY_POSITION)
            }
        }


        shouldAutoPlay = true
        mediaDataSourceFactory = DefaultDataSourceFactory(
            this, getUserAgent(
                this, applicationInfo.loadLabel(packageManager)
                    .toString()
            ), bandwidthMeter as TransferListener<in DataSource>
        )

        dataFactory = DefaultDataSourceFactory(this@MyPlayer, "ua")


    }



    fun makeTextViewResizable(tv: TextView, maxLine: Int, expandText: String, viewMore: Boolean) {

        if (tv.tag == null) {
            tv.tag = tv.text
        }
        val vto = tv.viewTreeObserver
        vto.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {

            override fun onGlobalLayout() {
                val text: String
                val lineEndIndex: Int
                val obs = tv.viewTreeObserver
                obs.removeGlobalOnLayoutListener(this)
                if (maxLine == 0) {
                    lineEndIndex = tv.layout.getLineEnd(0)
                    text = tv.text.subSequence(0, lineEndIndex - expandText.length + 1).toString() + " " + expandText
                } else if (maxLine > 0 && tv.lineCount >= maxLine) {
                    lineEndIndex = tv.layout.getLineEnd(maxLine - 1)
                    text = tv.text.subSequence(0, lineEndIndex - expandText.length + 1).toString() + " " + expandText
                } else {
                    lineEndIndex = tv.layout.getLineEnd(tv.layout.lineCount - 1)
                    text = tv.text.subSequence(0, lineEndIndex).toString() + " " + expandText
                }
                tv.text = text
                tv.movementMethod = LinkMovementMethod.getInstance()
                tv.setText(
                    addClickablePartTextViewResizable(
                        Html.fromHtml(tv.text.toString()), tv, lineEndIndex, expandText,
                        viewMore
                    ), TextView.BufferType.SPANNABLE
                )
            }
        })

    }

    private fun addClickablePartTextViewResizable(
        strSpanned: Spanned, tv: TextView,
        maxLine: Int, spanableText: String, viewMore: Boolean
    ): SpannableStringBuilder {
        val str = strSpanned.toString()
        val ssb = SpannableStringBuilder(strSpanned)

        if (str.contains(spanableText)) {


            ssb.setSpan(object : MySpannable(false) {
                override fun onClick(widget: View) {
                    tv.layoutParams = tv.layoutParams
                    tv.setText(tv.tag.toString(), TextView.BufferType.SPANNABLE)
                    tv.invalidate()
                    if (viewMore) {
                        makeTextViewResizable(tv, -1, "View Less", false)
                    } else {
                        makeTextViewResizable(tv, 3, "View More", true)
                    }
                }
            }, str.indexOf(spanableText), str.indexOf(spanableText) + spanableText.length, 0)

        }
        return ssb

    }
    private fun getParams2(): Map<String, String> {
        val params = HashMap<String, String>()
        params["action"] = "get_similar_by_video_id"
        params["video_id"] = play_id
        params["page_number"] = "1"
        params["device_name"] = "abcd"
        return params
    }

    internal lateinit var myvideos: ArrayList<MyVideos>

    private fun LoginApi() {
        RetrofitClient.getInstance()
            .doBackProcess(this@MyPlayer, getParams2(), "", object : APIResponse {
                override fun onSuccess(res: String?) {
                    try {
                        val jobj = JSONObject(res)
                        val status = jobj.getInt("status")
                        val details = jobj.getString("details")

                        if (status == 1) {
                            Log.e("RES", res)
                            val jsonArray = jobj.getJSONArray("data")
                            Log.e("RES my Array", "" + jsonArray.length())
                            myvideos = ArrayList()
                            for (i in 0 until jsonArray.length()) {
                                var lead = Gson().fromJson(
                                    jsonArray.get(i).toString(),
                                    MyVideos::class.java
                                )
                                myvideos.add(lead)
                            }
                            val my_recycler_view = findViewById<View>(R.id.rc_list) as RecyclerView
                            my_recycler_view.setHasFixedSize(true)
                            val adapter = PlayerAdapter(this@MyPlayer, myvideos, "player")
                            my_recycler_view.layoutManager =
                                LinearLayoutManager(this@MyPlayer, LinearLayoutManager.VERTICAL, false)
                            my_recycler_view.adapter = adapter
                        } else {
                            Toast.makeText(this@MyPlayer, "status" + status, Toast.LENGTH_LONG).show()
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }

                override fun onFailure(res: String?) {
                    Toast.makeText(this@MyPlayer, "status" + res, Toast.LENGTH_LONG).show()
                }
            })
    }

    public override fun onStart() {
        super.onStart()

        if (SDK_INT > 23) initializePlayer()
    }

    public override fun onResume() {
        super.onResume()

        if (SDK_INT <= 23 || player == null) initializePlayer()
    }

    public override fun onPause() {
        super.onPause()

        if (SDK_INT <= 23) releasePlayer()
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    public override fun onStop() {
        super.onStop()

        if (SDK_INT > 23) releasePlayer()

        if ((Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
            && packageManager.hasSystemFeature(PackageManager.FEATURE_PICTURE_IN_PICTURE)
        ) {
            finishAndRemoveTask()
        }
    }


    override fun onSaveInstanceState(outState: Bundle) {
        updateStartPosition()

        with(outState) {
            putBoolean(KEY_PLAY_WHEN_READY, playWhenReady)
            putInt(KEY_WINDOW, currentWindow)
            putLong(KEY_POSITION, playbackPosition)
        }

        super.onSaveInstanceState(outState)
    }


    @SuppressLint("SwitchIntDef")
    private fun initializePlayer() {

        playerView.requestFocus()

        val videoTrackSelectionFactory = AdaptiveTrackSelection.Factory(bandwidthMeter)

        trackSelector = DefaultTrackSelector(videoTrackSelectionFactory)
        lastSeenTrackGroupArray = null

        player = ExoPlayerFactory.newSimpleInstance(this, trackSelector)

        playerView.player = player

        with(player!!) {
            addListener(PlayerEventListener())
            playWhenReady = shouldAutoPlay
        }

        // Use Hls, Dash or other smooth streaming media source if you want to test the track quality selection.
        /* val mediaSource: MediaSource = HlsMediaSource(Uri.parse("ttps://www.flicbuzz.com/vendor_videos/converted/video7.m3u8"),
                 mediaDataSourceFactory, mainHandler, null)*/
        when (Util.inferContentType(Uri.parse(mUrl))) {
            C.TYPE_HLS -> {
                mediaSource = HlsMediaSource.Factory(dataFactory)
                    .setAllowChunklessPreparation(true)
                    .createMediaSource(Uri.parse(mUrl))
                Log.e("#Video Type#", "Playing HSL")
            }
            C.TYPE_DASH -> {
                mediaSource = ExtractorMediaSource
                    .Factory(mediaDataSourceFactory)
                    .createMediaSource(Uri.parse(mUrl))
                Log.e("#Video Type#", "Playing DASH")
            }
            else -> {
                Log.e("#Video Type#", "in Other ")
                finish()
            }

        }


        /* val mediaSource = ExtractorMediaSource.Factory(mediaDataSourceFactory)
                 .createMediaSource(Uri.parse("http://clips.vorwaerts-gmbh.de/big_buck_bunny.mp4"))*/


        /*  when (Util.inferContentType(Uri.parse(mUrl))) {
            C.TYPE_HLS -> {
                val mediaSource = HlsMediaSource
                        .Factory(dataFactory)
                        .createMediaSource(Uri.parse(mUrl))
            }

            C.TYPE_OTHER -> {
                val mediaSource = ExtractorMediaSource
                        .Factory(mediaDataSourceFactory)
                        .createMediaSource(Uri.parse(mUrl))
            }

            else -> {
                //This is to catch SmoothStreaming and
                //DASH types which we won't support currently, exit
                finish()
            }
        }*/

        //https://www.flicbuzz.com/vendor_videos/converted/video7.m3u8
        //https://bitdash-a.akamaihd.net/content/sintel/hls/playlist.m3u8
        val haveStartPosition = currentWindow != C.INDEX_UNSET
        if (haveStartPosition) {
            player!!.seekTo(currentWindow, playbackPosition)
        }

        player!!.prepare(mediaSource, !haveStartPosition, false)
        updateButtonVisibilities()

    }

    private fun releasePlayer() {
        if (player != null) {
            updateStartPosition()
            shouldAutoPlay = player!!.playWhenReady
            player.release()
            trackSelector = null
        }
    }

    private fun updateStartPosition() {

        with(player!!) {
            playbackPosition = currentPosition
            currentWindow = currentWindowIndex
            playWhenReady = playWhenReady
        }
    }

    private fun updateButtonVisibilities() {
        if (player == null) {
            return
        }

        val mappedTrackInfo = trackSelector!!.currentMappedTrackInfo ?: return

        for (i in 0 until mappedTrackInfo.rendererCount) {
            val trackGroups = mappedTrackInfo.getTrackGroups(i)
            if (trackGroups.length != 0) {
                /*   if (player!!.getRendererType(i) == C.TRACK_TYPE_VIDEO) {
                       ivSettings.visibility = View.VISIBLE
                       ivSettings.setOnClickListener(this)
                       ivSettings.tag = i
                   }*/
            }
        }
    }


    private inner class PlayerEventListener : Player.DefaultEventListener() {

        override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
            when (playbackState) {
                Player.STATE_IDLE       // The player does not have any media to play yet.
                -> progressBar.visibility = View.VISIBLE
                Player.STATE_BUFFERING  // The player is buffering (loading the content)
                -> progressBar.visibility = View.VISIBLE
                Player.STATE_READY      // The player is able to immediately play
                -> progressBar.visibility = View.GONE
                Player.STATE_ENDED      // The player has finished playing the media
                -> progressBar.visibility = View.GONE
            }
            updateButtonVisibilities()
        }

        override fun onTracksChanged(trackGroups: TrackGroupArray?, trackSelections: TrackSelectionArray?) {
            updateButtonVisibilities()
            // The video tracks are no supported in this device.
            if (trackGroups !== lastSeenTrackGroupArray) {
                val mappedTrackInfo = trackSelector!!.currentMappedTrackInfo
                if (mappedTrackInfo != null) {
                    if (mappedTrackInfo.getTypeSupport(C.TRACK_TYPE_VIDEO) == MappingTrackSelector.MappedTrackInfo.RENDERER_SUPPORT_UNSUPPORTED_TRACKS) {
                        Toast.makeText(this@MyPlayer, "Error unsupported track", Toast.LENGTH_SHORT).show()
                    }
                }
                lastSeenTrackGroupArray = trackGroups
            }
        }
    }


    override fun onPictureInPictureModeChanged(isInPictureInPictureMode: Boolean, newConfig: Configuration?) {
        if (newConfig != null) {
            playbackPosition = player!!.currentPosition
            isInPipMode = !isInPictureInPictureMode
        }
        super.onPictureInPictureModeChanged(isInPictureInPictureMode, newConfig)
    }

    //Called when the user touches the Home or Recents button to leave the app.
    override fun onUserLeaveHint() {
        super.onUserLeaveHint()
        enterPIPMode()
    }


    fun pipmode(){
        if (android.os.Build.VERSION.SDK_INT >= 26) {
            try {
                val rational = Rational(playerView.width, playerView.height)
                val mParams=PictureInPictureParams.Builder().setAspectRatio(rational)
                    .build()
                enterPictureInPictureMode(mParams);
            }catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
    @Suppress("DEPRECATION")
    fun enterPIPMode() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N
            && packageManager.hasSystemFeature(PackageManager.FEATURE_PICTURE_IN_PICTURE)
        ) {
            playbackPosition = player!!.currentPosition
            playerView.useController = false
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val rational = Rational(playerView.width, playerView.height)
                val params = PictureInPictureParams.Builder().setAspectRatio(rational)
                this.enterPictureInPictureMode(params.build())
            } else {
                this.enterPictureInPictureMode()
            }
            /* We need to check this because the system permission check is publically hidden for integers for non-manufacturer-built apps
               https://github.com/aosp-mirror/platform_frameworks_base/blob/studio-3.1.2/core/java/android/app/AppOpsManager.java#L1640

               ********* If we didn't have that problem *********
                val appOpsManager = getSystemService(Context.APP_OPS_SERVICE) as AppOpsManager
                if(appOpsManager.checkOpNoThrow(AppOpManager.OP_PICTURE_IN_PICTURE, packageManager.getApplicationInfo(packageName, PackageManager.GET_META_DATA).uid, packageName) == AppOpsManager.MODE_ALLOWED)

                30MS window in even a restricted memory device (756mb+) is more than enough time to check, but also not have the system complain about holding an action hostage.
             */
            Handler().postDelayed({ checkPIPPermission() }, 30)
        }
    }

    @RequiresApi(Build.VERSION_CODES.N)
    fun checkPIPPermission() {
        isPIPModeeEnabled = isInPictureInPictureMode
        if (!isInPictureInPictureMode) {
            onBackPressed()
        }
    }

    override fun onBackPressed() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N
            && packageManager.hasSystemFeature(PackageManager.FEATURE_PICTURE_IN_PICTURE)
            && isPIPModeeEnabled
        ) {
            enterPIPMode()
        } else {
            super.onBackPressed()
        }
    }




}
