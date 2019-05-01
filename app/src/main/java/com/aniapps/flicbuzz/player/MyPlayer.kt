package com.aniapps.flicbuzz.player

import android.Manifest
import android.annotation.SuppressLint
import android.app.ActionBar
import android.app.Dialog
import android.app.PictureInPictureParams
import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.PersistableBundle
import android.support.annotation.RequiresApi
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.text.Html
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.method.LinkMovementMethod
import android.util.Log
import android.util.Rational
import android.view.*
import android.view.animation.AnimationUtils
import android.widget.*
import com.aniapps.flicbuzz.R
import com.aniapps.flicbuzz.utils.MySpannable
import com.aniapps.flicbuzz.adapters.MainAdapter
import com.aniapps.flicbuzz.models.MyVideos
import com.aniapps.flicbuzz.networkcall.APIResponse
import com.aniapps.flicbuzz.networkcall.RetrofitClient
import com.aniapps.flicbuzz.utils.PrefManager
import com.google.android.exoplayer2.*
import com.google.android.exoplayer2.source.ExtractorMediaSource
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.source.TrackGroupArray
import com.google.android.exoplayer2.source.hls.HlsMediaSource
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.trackselection.MappingTrackSelector
import com.google.android.exoplayer2.trackselection.TrackSelectionArray
import com.google.android.exoplayer2.ui.*
import com.google.android.exoplayer2.upstream.*
import com.google.android.exoplayer2.util.EventLogger
import com.google.android.exoplayer2.util.Util
import com.google.android.exoplayer2.util.Util.SDK_INT
import com.google.android.exoplayer2.util.Util.getUserAgent
import com.google.gson.Gson
import kotlinx.android.synthetic.main.list_item.*
import kotlinx.android.synthetic.main.myplayer.*
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
    private var play_fav: String = "";
    private var play_share_url: String = "";
    private var play_id: String = "";

    private var STATE_RESUME_WINDOW = "resumeWindow";
    private var STATE_RESUME_POSITION = "resumePosition";
    private var STATE_PLAYER_FULLSCREEN = "playerFullscreen";

    private lateinit var lay_playerview: FrameLayout;

    private lateinit var lay_fav: FrameLayout;
    private lateinit var img_fav: ImageView;
    private lateinit var img_fav_done: ImageView;
    private lateinit var img_share: ImageView;
    private lateinit var mVideoSource: MediaSource;
    private var mExoPlayerFullscreen: Boolean = false;
    private lateinit var mFullScreenButton: FrameLayout;
    private lateinit var mFullScreenIcon: ImageView;
    private lateinit var mFullScreenDialog: Dialog;
    private var mResumeWindow: Int = 0
    private var mResumePosition: Long = 0
    // https://github.com/GeoffLedak/ExoplayerFullscreen/blob/master/app/src/main/java/com/geoffledak/exoplayerfullscreen/MainActivity.java*/

    var mediaSource: MediaSource? = null;
    /*https://medium.com/@mayur_solanki/adaptive-streaming-with-exoplayer-c77b0032acdd*/
    private val progressBar: ProgressBar by lazy { findViewById<ProgressBar>(R.id.progress_bar) }
    private val settings: ImageButton by lazy { findViewById<ImageButton>(R.id.icon_setting) }
    private val icon_pip: ImageButton by lazy { findViewById<ImageButton>(R.id.icon_pip) }
    private val share: ImageButton by lazy { findViewById<ImageButton>(R.id.icon_share) }
    /*https://stackoverflow.com/questions/16300959/android-share-image-from-url*/
    private val fullscreen: FrameLayout by lazy { findViewById<FrameLayout>(R.id.exo_fullscreen_button) }

    internal var loading = false
    internal var scrollFlag = false
    internal var layoutManager: LinearLayoutManager? = null
    internal var total_records = ""
    private var pbr: ProgressBar? = null

    internal var pageNo = 1
    internal lateinit var my_recycler_view: RecyclerView
    var permissions = arrayOf<String>(
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.WRITE_EXTERNAL_STORAGE
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.myplayer)
        mUrl = intent.getStringExtra("url")
        play_desc = intent.getStringExtra("desc")
        play_title = intent.getStringExtra("title")
        play_id = intent.getStringExtra("id")
        play_fav = intent.getStringExtra("fav")
        play_share_url = intent.getStringExtra("play_share_url")

        if (savedInstanceState != null) {
            mResumeWindow = savedInstanceState.getInt(STATE_RESUME_WINDOW);
            mResumePosition = savedInstanceState.getLong(STATE_RESUME_POSITION);
            // mExoPlayerFullscreen = savedInstanceState.getBoolean(STATE_PLAYER_FULLSCREEN);
        }

        my_recycler_view = findViewById<View>(R.id.rc_list) as RecyclerView
        myvideos = ArrayList()

        pbr = findViewById(R.id.load_progress) as ProgressBar
        pbr!!.getIndeterminateDrawable().setColorFilter(
            ContextCompat.getColor(this, R.color.colorAccent),
            android.graphics.PorterDuff.Mode.MULTIPLY
        )
        myvideos.clear()
        LoginApi(pageNo)

        my_recycler_view.setNestedScrollingEnabled(false)

        tv_play_title.setText(play_title)
        tv_play_description.setText(play_desc)
        lay_playerview = findViewById<FrameLayout>(R.id.playerview)

        settings.setOnClickListener {
            myTracker()
        }
        icon_pip.setOnClickListener {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N && packageManager.hasSystemFeature(PackageManager.FEATURE_PICTURE_IN_PICTURE)
                && isPIPModeeEnabled
            ) {
                enterPIPMode()
            }
        }
        share.setOnClickListener {
            if (setupPermissions()) {
                DownloadTask(this@MyPlayer, play_share_url)
            } else {
                Toast.makeText(this@MyPlayer, "Please allow storage permission to share video", Toast.LENGTH_SHORT)
                    .show()
            }

        }


        makeTextViewResizable(tv_play_description, 2, "View More", true)

        lay_fav = findViewById(R.id.lay_fav)
        img_fav = findViewById(R.id.img_fav)
        img_fav_done = findViewById(R.id.img_fav_done)
        img_share = findViewById(R.id.img_share)

        if (play_fav.equals("y")) {
            img_fav_done.visibility = View.VISIBLE
            img_fav.visibility = View.GONE
        } else {
            img_fav.visibility = View.VISIBLE
            img_fav_done.visibility = View.GONE
        }

        img_share.setOnClickListener {
            if (setupPermissions()) {
                DownloadTask(this@MyPlayer, play_share_url)
            } else {
                Toast.makeText(this@MyPlayer, "Please allow storage permission to share video", Toast.LENGTH_SHORT)
                    .show()
            }
        }
        lay_fav.setOnClickListener {
            if (img_fav.getVisibility() == View.VISIBLE) {
                favAddApi()
            } else {
                favRemoveApi()
            }
        }


        if (savedInstanceState != null) {
            with(savedInstanceState) {
                playWhenReady = getBoolean(KEY_PLAY_WHEN_READY)
                currentWindow = getInt(KEY_WINDOW)
                playbackPosition = getLong(KEY_POSITION)
            }
        }

        initFullscreenButton()

        shouldAutoPlay = true
        mediaDataSourceFactory = DefaultDataSourceFactory(
            this, getUserAgent(
                this, applicationInfo.loadLabel(packageManager)
                    .toString()
            ), bandwidthMeter as TransferListener<in DataSource>
        )

        dataFactory = DefaultDataSourceFactory(this@MyPlayer, "ua")


    }


    fun favAddApi() {
        val params = HashMap<String, String>()
        params["action"] = "add_favorite"
        params["video_id"] = play_id
        // var flag = false
        RetrofitClient.getInstance()
            .doBackProcess(this@MyPlayer, params, "online", object : APIResponse {
                override fun onSuccess(res: String?) {
                    try {
                        val jobj = JSONObject(res)
                        val status = jobj.getInt("status")
                        val details = jobj.getString("details")
                        if (status == 1) {
                            Handler().postDelayed(Runnable {
                                img_fav.setVisibility(View.GONE);
                                img_fav_done.setVisibility(View.VISIBLE);
                                val animFadeIn = AnimationUtils.loadAnimation(
                                    this@MyPlayer,
                                    R.anim.fav_done
                                );
                                img_fav.startAnimation(animFadeIn);
                            }, 500)
                            // flag = true
                            Toast.makeText(this@MyPlayer, jobj.getString("message"), Toast.LENGTH_SHORT).show();
                        } else {
                            //   flag = false
                            Toast.makeText(this@MyPlayer, "status" + details, Toast.LENGTH_LONG).show()
                        }

                    } catch (e: Exception) {
                        e.printStackTrace()

                    }
                }

                override fun onFailure(res: String?) {
                    // flag = false
                    Toast.makeText(this@MyPlayer, "status" + res, Toast.LENGTH_LONG).show()

                }
            })

        //return flag
    }

    fun favRemoveApi() {
        val params = HashMap<String, String>()
        params["action"] = "remove_favorite"
        params["video_id"] = play_id
        RetrofitClient.getInstance()
            .doBackProcess(this@MyPlayer, params, "online", object : APIResponse {
                override fun onSuccess(res: String?) {
                    try {
                        val jobj = JSONObject(res)
                        val status = jobj.getInt("status")
                        val details = jobj.getString("details")
                        if (status == 1) {
                            Handler().postDelayed(Runnable {
                                img_fav_done.setVisibility(View.GONE);
                                img_fav.setVisibility(View.VISIBLE);
                            }, 500)

                            Toast.makeText(this@MyPlayer, jobj.getString("message"), Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(this@MyPlayer, "status" + details, Toast.LENGTH_LONG).show()
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

    override fun onSaveInstanceState(outState: Bundle?, outPersistentState: PersistableBundle?) {
        if (outState != null) {
            outState.putInt(STATE_RESUME_WINDOW, mResumeWindow)
            outState.putLong(STATE_RESUME_POSITION, mResumePosition);
        }
        super.onSaveInstanceState(outState, outPersistentState)
    }

    private fun initFullscreenButton() {
        mFullScreenIcon = findViewById(R.id.exo_fullscreen_icon);
        mFullScreenButton = findViewById(R.id.exo_fullscreen_button);
        mFullScreenButton.setOnClickListener {
            val orientation = this.resources.configuration.orientation
            if (orientation == Configuration.ORIENTATION_PORTRAIT) {
                //  getWindow().addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN);
                mFullScreenIcon.setImageDrawable(
                    ContextCompat.getDrawable(
                        this@MyPlayer,
                        R.drawable.ic_fullscreen_skrink
                    )
                );
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                playerView.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_FILL);

            } else {
                getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
                mFullScreenIcon.setImageDrawable(
                    ContextCompat.getDrawable(
                        this@MyPlayer,
                        R.drawable.ic_fullscreen_expand
                    )
                );
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                playerView.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_FIT);

            }

        }
    }


    override fun onConfigurationChanged(newConfig: Configuration?) {
        super.onConfigurationChanged(newConfig)
        if (newConfig!!.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
            playerView.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_FILL);
            mFullScreenIcon.setImageDrawable(
                ContextCompat.getDrawable(
                    this@MyPlayer,
                    R.drawable.ic_fullscreen_skrink
                )
            );
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {

            playerView.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_FIT);
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN);
            mFullScreenIcon.setImageDrawable(
                ContextCompat.getDrawable(
                    this@MyPlayer,
                    R.drawable.ic_fullscreen_expand
                )
            );


        }


        //  my_recycler_view.adapter!!.notifyDataSetChanged()
        // recycler_view_list.adapter!!.notifyDataSetChanged()


    }


    fun makeTextViewResizable(tv: TextView, maxLine: Int, expandText: String, viewMore: Boolean) {
        /*https://stackoverflow.com/questions/31668697/android-expandable-text-view-with-view-more-button-displaying-at-center-after*/
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


    internal lateinit var myvideos: ArrayList<MyVideos>
    internal lateinit var adapter: MainAdapter

    private fun LoginApi(pno: Int) {
        val params = HashMap<String, String>()
        params["action"] = "get_similar_by_video_id"
        params["video_id"] = play_id
        params["page_number"] = "" + pno
        params["device_name"] = "abcd"


        RetrofitClient.getInstance()
            .doBackProcess(this@MyPlayer, params, "", object : APIResponse {
                override fun onSuccess(res: String?) {
                    try {
                        val jobj = JSONObject(res)
                        val status = jobj.getInt("status")
                        val details = jobj.getString("details")

                        if (status == 1) {
                            val jsonArray = jobj.getJSONArray("data")
                            for (i in 0 until jsonArray.length()) {
                                var lead = Gson().fromJson(
                                    jsonArray.get(i).toString(),
                                    MyVideos::class.java
                                )
                                myvideos.add(lead)
                            }
                            my_recycler_view.setHasFixedSize(true)
                            adapter = MainAdapter(this@MyPlayer, myvideos, "player")
                            layoutManager = LinearLayoutManager(applicationContext)
                            my_recycler_view.setLayoutManager(layoutManager)
                            my_recycler_view.setNestedScrollingEnabled(false)
                            my_recycler_view.adapter = adapter
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
        player.addAnalyticsListener(EventLogger(trackSelector));

        playerView.player = player
        playerView.useController = true

        with(player) {
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


        //https://www.flicbuzz.com/vendor_videos/converted/video7.m3u8
        //https://bitdash-a.akamaihd.net/content/sintel/hls/playlist.m3u8
        val haveStartPosition = currentWindow != C.INDEX_UNSET
        if (haveStartPosition) {
            player.seekTo(currentWindow, playbackPosition)
        }

        player.prepare(mediaSource, !haveStartPosition, false)
        updateButtonVisibilities()

    }

    private fun releasePlayer() {
        updateStartPosition()
        shouldAutoPlay = player.playWhenReady
        player.release()
        trackSelector = null

    }

    private fun updateStartPosition() {

        with(player) {
            playbackPosition = currentPosition
            currentWindow = currentWindowIndex
            playWhenReady = playWhenReady
        }
    }

    private fun updateButtonVisibilities() {

        val mappedTrackInfo = trackSelector!!.currentMappedTrackInfo ?: return

        for (i in 0 until mappedTrackInfo.rendererCount) {
            val trackGroups = mappedTrackInfo.getTrackGroups(i)
            if (trackGroups.length != 0) {
                /*if (player.getRendererType(i) == C.TRACK_TYPE_VIDEO) {
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
                Log.e("###", mappedTrackInfo.toString())
                if (mappedTrackInfo != null) {
                    if (mappedTrackInfo.getTypeSupport(C.TRACK_TYPE_VIDEO) == MappingTrackSelector.MappedTrackInfo.RENDERER_SUPPORT_UNSUPPORTED_TRACKS) {
                        Toast.makeText(this@MyPlayer, "Error unsupported track", Toast.LENGTH_SHORT).show()
                    }
                }
                lastSeenTrackGroupArray = trackGroups
            }
        }
    }


    fun myTracker() {
        //https://medium.com/google-exoplayer/exoplayer-2-x-track-selection-2b62ff712cc9
        //https://medium.com/@mayur_solanki/adaptive-streaming-with-exoplayer-c77b0032acdd
        //https://android.jlelse.eu/exoplayer-components-explained-9937e3a5d2f5
        //https://android.jlelse.eu/exoplayer-components-explained-9937e3a5d2f5
        //https://exoplayer.dev/guide.html
        //https://medium.com/google-exoplayer/exoplayer-2-x-track-selection-2b62ff712cc9
        //https://gist.github.com/abhiint16/b473e9b1111bd8bda4833c288ae6a1b4
        //https://stackoverflow.com/questions/52112981/customizing-exoplayer-quality-dialog-in-my-app
        val mappedTrackInfo = trackSelector!!.getCurrentMappedTrackInfo();


        if (mappedTrackInfo != null) {
            MappingTrackSelector.MappedTrackInfo.RENDERER_SUPPORT_NO_TRACKS
            var dialogPair =
                TrackSelectionView.getDialog(this, "Select Video Resolution", trackSelector, 0);
            dialogPair.second.setShowDisableOption(false);
            dialogPair.second.setAllowAdaptiveSelections(false);
            dialogPair.first.setIcon(R.mipmap.ic_launcher)

            dialogPair.first.show();


        }
    }


    override fun onBackPressed() {


            val orientation = this.resources.configuration.orientation
            if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                playerView.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_FIT);
            } else {
                super.onBackPressed()
            }


    }

    private fun setupPermissions(): Boolean {
        val permission = ContextCompat.checkSelfPermission(
            this@MyPlayer,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        )

        if (permission != PackageManager.PERMISSION_GRANTED) {
            makeRequest()
            return false;
        } else {
            return true
        }

    }

    private fun makeRequest() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
            123
        )
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            123 -> {
                if (grantResults.isEmpty() || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show()
                } else {
                    DownloadTask(this@MyPlayer, play_share_url)
                }
            }
        }
    }

    //For N devices that support it, not "officially"
    //https://medium.com/s23nyc-tech/drop-in-android-video-exoplayer2-with-picture-in-picture-e2d4f8c1eb30
    @Suppress("DEPRECATION")
    override fun onPictureInPictureModeChanged(isInPictureInPictureMode: Boolean, newConfig: Configuration?) {
        if (newConfig != null) {
            playbackPosition = player.currentPosition
            isInPipMode = !isInPictureInPictureMode
        }

        if (!isInPictureInPictureMode) {
            playerView.showController()
            playerView.controllerAutoShow = true

        }
        super.onPictureInPictureModeChanged(isInPictureInPictureMode, newConfig)
    }

   /* //Called when the user touches the Home or Recents button to leave the app.
    override fun onUserLeaveHint() {
        super.onUserLeaveHint()
        enterPIPMode()
    }*/


    @Suppress("DEPRECATION")
    fun enterPIPMode() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N
            && packageManager
                .hasSystemFeature(
                    PackageManager.FEATURE_PICTURE_IN_PICTURE
                )
        ) {
            playbackPosition = player.currentPosition
            playerView.useController = false
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val params = PictureInPictureParams.Builder()
                this.enterPictureInPictureMode(params.build())
            } else {
                this.enterPictureInPictureMode()
            }
            Handler().postDelayed({checkPIPPermission()}, 30)
        }
    }

   /* @Suppress("DEPRECATION")
    fun enterPIPMode() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N
            && packageManager.hasSystemFeature(PackageManager.FEATURE_PICTURE_IN_PICTURE)
        ) {
            playbackPosition = player.currentPosition
            playerView.useController = false
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val rational = Rational(playerView.width, playerView.height)
                val params = PictureInPictureParams.Builder().setAspectRatio(rational)
                this.enterPictureInPictureMode(params.build())
            } else {
                this.enterPictureInPictureMode()
            }

            Handler().postDelayed({ checkPIPPermission() }, 30)
        }
    }*/

    @RequiresApi(Build.VERSION_CODES.N)
    fun checkPIPPermission() {
        isPIPModeeEnabled = isInPictureInPictureMode
        if (!isInPictureInPictureMode) {
            icon_pip.performClick()
        }
    }
}
