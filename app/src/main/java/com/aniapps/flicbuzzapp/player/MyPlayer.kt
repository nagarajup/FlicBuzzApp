package com.aniapps.flicbuzzapp.player

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.PictureInPictureParams
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.net.Uri
import android.os.*
import android.text.Html
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.method.LinkMovementMethod
import android.util.Log
import android.util.Rational
import android.view.*
import android.view.animation.AnimationUtils
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.aniapps.flicbuzzapp.AppConstants
import com.aniapps.flicbuzzapp.R
import com.aniapps.flicbuzzapp.utils.MySpannable
import com.aniapps.flicbuzzapp.adapters.MainAdapter
import com.aniapps.flicbuzzapp.models.MyVideos
import com.aniapps.flicbuzzapp.networkcall.APIResponse
import com.aniapps.flicbuzzapp.networkcall.RetrofitClient
import com.aniapps.flicbuzzapp.utils.PrefManager
import com.aniapps.flicbuzzapp.utils.Utility
import com.google.android.exoplayer2.*
import com.google.android.exoplayer2.source.*
import com.google.android.exoplayer2.source.hls.HlsMediaSource
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.trackselection.MappingTrackSelector
import com.google.android.exoplayer2.trackselection.TrackSelectionArray
import com.google.android.exoplayer2.ui.*
import com.google.android.exoplayer2.upstream.*
import com.google.android.exoplayer2.util.EventLogger
import com.google.android.exoplayer2.util.Util.SDK_INT
import com.google.gson.Gson
import kotlinx.android.synthetic.main.myplayer.*
import org.json.JSONObject
import java.util.ArrayList

class MyPlayer : AppConstants()/*, MyPlayerIns*/ {

    companion object {
        private const val KEY_PLAY_WHEN_READY = "play_when_ready"
        private const val KEY_WINDOW = "window"
        private const val KEY_POSITION = "position"
    }

    //https://techdai.info/making-dynamic-video-player-from-exoplayer-android/
    private lateinit var player: SimpleExoPlayer
    private val playerView: PlayerView by lazy { findViewById<PlayerView>(R.id.video_view) }
    lateinit var dynamicConcatenatingMediaSource: DynamicConcatenatingMediaSource;
    private var shouldAutoPlay: Boolean = true
    private var trackSelector: DefaultTrackSelector? = null
    private var lastSeenTrackGroupArray: TrackGroupArray? = null
    private lateinit var mediaDataSourceFactory: DataSource.Factory
    private lateinit var dataFactory: DefaultDataSourceFactory
    var isInPipMode: Boolean = false
    var isPIPModeeEnabled: Boolean = true
    internal lateinit var myvideos: ArrayList<MyVideos>
    internal lateinit var adapter: MainAdapter
    lateinit var language: String
    lateinit var playing_video: MyVideos
    lateinit var mySequence: ArrayList<MyVideos>

    private var bandwidthMeter: BandwidthMeter = DefaultBandwidthMeter()

    private var playWhenReady: Boolean = false
    private var currentWindow: Int = 0


    private var playbackPosition: Long = 0

    private var STATE_RESUME_WINDOW = "resumeWindow";
    private var STATE_RESUME_POSITION = "resumePosition"

    private lateinit var lay_fav: FrameLayout;
    private lateinit var img_fav: ImageView;
    private lateinit var img_fav_done: ImageView;
    private lateinit var img_share: ImageView;
    private lateinit var mFullScreenButton: FrameLayout;
    private lateinit var mFullScreenIcon: ImageView;
    private var mResumeWindow: Int = 0
    private var mResumePosition: Long = 0
    // https://github.com/GeoffLedak/ExoplayerFullscreen/blob/master/app/src/main/java/com/geoffledak/exoplayerfullscreen/MainActivity.java*/
    /*https://medium.com/@mayur_solanki/adaptive-streaming-with-exoplayer-c77b0032acdd*/
    private val progressBar: ProgressBar by lazy { findViewById<ProgressBar>(R.id.progress_bar) }
    private val settings: ImageButton by lazy { findViewById<ImageButton>(R.id.icon_setting) }
    private val icon_pip: ImageButton by lazy { findViewById<ImageButton>(R.id.icon_pip) }
    private val share: ImageButton by lazy { findViewById<ImageButton>(R.id.icon_share) }
    // private val previous_video: ImageButton by lazy { findViewById<ImageButton>(R.id.exo_prev_video) }
    // private val next_video: ImageButton by lazy { findViewById<ImageButton>(R.id.exo_next_video) }
    /*https://stackoverflow.com/questions/16300959/android-share-image-from-url*/
    var share_flag: Boolean = false
    private val fullscreen: FrameLayout by lazy { findViewById<FrameLayout>(R.id.exo_fullscreen_button) }
    var window = Timeline.Window();
    internal var layoutManager: LinearLayoutManager? = null

    internal lateinit var my_recycler_view: RecyclerView
    var permissions = arrayOf<String>(
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.WRITE_EXTERNAL_STORAGE
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.myplayer)
        playing_video = intent.getParcelableExtra("playingVideo")
        mySequence = intent.getParcelableArrayListExtra("sequence")
        language = intent.getStringExtra("language")
        currentWindow = intent.getIntExtra("pos", 0)

        //Log.e("####myPlayingvideo", playing_video.headline);
        //Log.e("####myPlayingvideo", playing_video.description);

        if (savedInstanceState != null) {
            mResumeWindow = savedInstanceState.getInt(STATE_RESUME_WINDOW);
            mResumePosition = savedInstanceState.getLong(STATE_RESUME_POSITION);
        }

        if (savedInstanceState != null) {
            with(savedInstanceState) {
                playWhenReady = getBoolean(KEY_PLAY_WHEN_READY)
                currentWindow = getInt(KEY_WINDOW)
                playbackPosition = getLong(KEY_POSITION)
            }
        }

        dynamicConcatenatingMediaSource = DynamicConcatenatingMediaSource()
        myPlayerApi(mySequence.get(currentWindow))
    }


    /* override fun refresh(id: String, urs1: String, url2: String, from: String) {
         Handler().postDelayed(Runnable {
         //    preparePlayer(urs1)
             //  myPlayerApi(id, from)
         }, 500)

         Log.e("@@@@", "ins in Class")
     }*/

    private fun myPlayerApi(myVideo: MyVideos) {
        val params = HashMap<String, String>()
        params["action"] = "get_similar_by_video_id"
        params["video_id"] = myVideo.id
        params["page_number"] = "1"
        params["page_number"] = "1"
        if (language.equals("")) {
            params["language"] = PrefManager.getIn().language.toLowerCase()
        } else {
            params["language"] = language;
        }

        // Log.e("&&&&&&", " video id 4" +mySequence.get(currentWindow).id)
        // Log.e("&&&&&&", " from" +myVideo.id)

        /* for (i in 0 until LandingPage.playingVideos.size) {
             Log.e("###", "MYIDS" + LandingPage.playingVideos.get(i).id)
         }*/
        RetrofitClient.getInstance()
            .doBackProcess(this@MyPlayer, params, "", object : APIResponse {
                override fun onSuccess(res: String?) {
                    try {
                        val jobj = JSONObject(res)
                        val status = jobj.getInt("status")
                        val details = jobj.getString("details")
                        myvideos = ArrayList()
                        myvideos.clear()
                        if (status == 1) {
                            val jsonArray = jobj.getJSONArray("data")
                            for (i in 0 until jsonArray.length()) {
                                val lead = Gson().fromJson(
                                    jsonArray.get(i).toString(),
                                    MyVideos::class.java
                                )
                                myvideos.add(lead)
                            }

                            initUi(myVideo)
                            my_recycler_view.adapter = null;
                            my_recycler_view.setHasFixedSize(true)
                            my_recycler_view.setItemViewCacheSize(20);
                            my_recycler_view.setDrawingCacheEnabled(true);
                            adapter = MainAdapter(this@MyPlayer, myvideos, "player")
                            layoutManager = LinearLayoutManager(applicationContext)
                            my_recycler_view.setLayoutManager(layoutManager)
                            my_recycler_view.setNestedScrollingEnabled(false)
                            my_recycler_view.adapter = adapter
                            adapter.notifyDataSetChanged()
                        } else if (status == 14) run {
                            Utility.alertDialog(
                                this@MyPlayer,
                                jobj.getString("message")
                            )
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


    private fun impressionTracker(myVideo: MyVideos) {

        val params = HashMap<String, String>()
        params["action"] = "video_impression_tracker"
        params["video_id"] = myVideo.id
        if (language.equals("")) {
            params["language"] = PrefManager.getIn().language.toLowerCase()
        } else {
            params["language"] = language;
        }
        //Log.e("&&&&&&", " video id 3" +mySequence.get(currentWindow).id)
        //Log.e("&&&&&&", " video id 3A" +myVideo.id)
        //Log.e("###", "Impression Vidoe ID" + myVideo.id)
        // Log.e("###", "Impression title" + myVideo.headline)
        // Log.e("###", "Impression window ID" + currentWindow)

        RetrofitClient.getInstance()
            .doBackProcess(this@MyPlayer, params, "", object : APIResponse {
                override fun onSuccess(res: String?) {
                    try {
                        val jobj = JSONObject(res)
                        val status = jobj.getInt("status")
                        val details = jobj.getString("details")
                        if (status == 1) {
                            /*val jsonArray = jobj.getJSONArray("next")
                            var lead = Gson().fromJson(
                                jsonArray.get(0).toString(),
                                MyVideos::class.java
                            )
                            mySequence.add(lead)*/
                            /* if (!from.equals("previous")) {
                                 val jsonArray = jobj.getJSONArray("next")
                                 for (i in 0 until jsonArray.length()) {
                                     var lead = Gson().fromJson(
                                         jsonArray.get(i).toString(),
                                         MyVideos::class.java
                                     )
                                     *//*if (!LandingPage.playingVideos.contains(lead)) {
                                        LandingPage.playingVideos.add(lead)
                                    }*//*
                                }
                            }*/
                            myPlayerApi(myVideo)
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


    @SuppressLint("SwitchIntDef")
    private fun initializePlayer() {
        shouldAutoPlay = true
        playerView.requestFocus()
        window = Timeline.Window();
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
        // preparePlayer(playing_video.video_filename)
        myPlayerSource()
        updateButtonVisibilities()
    }

    fun myPlayerSource() {
        val uriList = mutableListOf<MediaSource>()
        dataFactory = DefaultDataSourceFactory(this@MyPlayer, "ua")
        for (i in 0 until mySequence.size) {
            // Log.e("@@@@", "Player List" + i + mySequence.get(i).id);
            uriList.add(
                HlsMediaSource.Factory(dataFactory)
                    .setAllowChunklessPreparation(true)
                    .createMediaSource(Uri.parse(mySequence.get(i).video_filename))
            )
        }
        val mediaSource = ConcatenatingMediaSource(*uriList.toTypedArray())
        val haveStartPosition = currentWindow != C.INDEX_UNSET
        if (haveStartPosition) {
            player.seekTo(currentWindow, playbackPosition)
        }
        player.setPlayWhenReady(true);
        player.prepare(mediaSource, !haveStartPosition, false)
    }
    /* fun preparePlayer(url: String) {
         Log.e("@@@@", "media  LIST" + dynamicConcatenatingMediaSource.size)
         bandwidthMeter = DefaultBandwidthMeter()
         dataFactory = DefaultDataSourceFactory(this@MyPlayer, "ua")
         val uriList = mutableListOf<MediaSource>()
         uriList.clear();
         Log.e("@@@@", "Player Size" + LandingPage.playingVideos.size);
         var mymediasourc = HlsMediaSource.Factory(dataFactory)
             .setAllowChunklessPreparation(true)
             .createMediaSource(Uri.parse(url))
         if (dynamicConcatenatingMediaSource.getSize() == 0) {
             dynamicConcatenatingMediaSource.addMediaSource(mymediasourc)
             player.prepare(dynamicConcatenatingMediaSource);
             player.setPlayWhenReady(true);
         } else {
             dynamicConcatenatingMediaSource.addMediaSource(mymediasourc);
         }


         *//* for (i in 0 until LandingPage.playingVideos.size) {
              Log.e("@@@@", "Player List" + i + LandingPage.playingVideos.get(i).id);
              uriList.add(
                  HlsMediaSource.Factory(dataFactory)
                      .setAllowChunklessPreparation(true)
                      .createMediaSource(Uri.parse(LandingPage.playingVideos.get(i).video_filename))
              )
          }
          val mediaSource = ConcatenatingMediaSource(*uriList.toTypedArray())
          val haveStartPosition = currentWindow != C.INDEX_UNSET
          if (haveStartPosition) {
              player.seekTo(currentWindow, playbackPosition)
          }
          player.setPlayWhenReady(true);
          player.prepare(mediaSource, !haveStartPosition, false)*//*


//https://medium.com/androiddevelopers/building-a-video-player-app-in-android-part-3-5-19543ea9d416
        //  https://stackoverflow.com/questions/40284772/exoplayer-2-playlist-listener
        //https://stackoverflow.com/questions/tagged/exoplayer
        //https://stackoverflow.com/questions/40555405/how-to-pause-exoplayer-2-playback-and-resume-playercontrol-was-removed
        // Log.e("MyList", "@@@" + LandingPage.playingVideos);


        *//*val hashset = HashSet<MyVideos>();
        hashset.addAll(LandingPage.playingVideos)
        LandingPage.playingVideos.clear()
        LandingPage.playingVideos.addAll(hashset)*//*

    }*/


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
                if (player.getRendererType(i) == C.TRACK_TYPE_VIDEO) {
                    settings.visibility = View.VISIBLE
                    settings.setOnClickListener {
                        myTracker()
                        trackEvent(this@MyPlayer, "Player", "Player|Settings")
                    }
                    settings.tag = i
                }
            }
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            icon_pip.visibility = View.VISIBLE
            icon_pip.setOnClickListener {

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N && packageManager.hasSystemFeature(PackageManager.FEATURE_PICTURE_IN_PICTURE)
                    && isPIPModeeEnabled
                ) {
                    trackEvent(this@MyPlayer, "Player", "Player|PIP")
                    enterPIPMode()
                }
            }
        } else {
            icon_pip.visibility = View.GONE
        }

        if (mySequence.get(currentWindow).share_url.equals("")) {
            share.visibility = View.GONE
        } else {
            share.visibility = View.VISIBLE
        }
        share.setOnClickListener {
            trackEvent(this@MyPlayer, "Player", "Player|Share")
            /*f (setupPermissions()) {*/
            share_flag = true;
            pausePlayer()
            DownloadTask(this@MyPlayer, mySequence.get(currentWindow))
            /*} else {
                Toast.makeText(this@MyPlayer, "Please allow storage permission to share video", Toast.LENGTH_SHORT)
                    .show()
            }*/

        }

        /*previous_video.setOnClickListener {
            // LandingPage.videoCount--
            //https://github.com/google/ExoPlayer/blob/3ada4e178dc320abb73c01ec7bdf4535334e0a3d/library/ui/src/main/java/com/google/android/exoplayer2/ui/PlaybackControlView.java#L944


            impressionTracker("previous", myvideos.get(currentWindow - 1))


            *//* val timeline = player.currentTimeline
             if (timeline.isEmpty) {
                 return@setOnClickListener
             }
             currentWindow = player.currentWindowIndex
             timeline.getWindow(currentWindow, window)
             if (currentWindow > 0 && (player.getCurrentPosition() <= 3000
                         || (window.isDynamic && !window.isSeekable))
             ) {
                 player.seekToDefaultPosition(currentWindow - 1);
                 myPlayerApi(LandingPage.playingVideos.get(currentWindow).id, "previous")
                 Log.e("@@@@", "prev1111111" + currentWindow)
             } else {
                 Log.e("@@@@", "prev222222" + currentWindow)
                 player.seekTo(0);
             }*//*
        }*/


        /* next_video.setOnClickListener {

             LandingPage.videoCount++
             val timeline = player.currentTimeline
             if (timeline.isEmpty) {
                 return@setOnClickListener
             }
             currentWindow = player.currentWindowIndex

             Log.e("###", "next id ");
             Log.e("###", "playervidoes" + dynamicConcatenatingMediaSource.getMediaSource(LandingPage.videoCount));
             Log.e("###", "next vidoe " + LandingPage.playingVideos.get(LandingPage.videoCount).headline);

             impressionTracker("next", LandingPage.playingVideos.get(currentWindow + 1))
             if (currentWindow < timeline.getWindowCount() - 1) {
                 player.seekToDefaultPosition(currentWindow + 1);
             }

             *//* val timeline = player.currentTimeline
             if (timeline.isEmpty) {
                 return@setOnClickListener
             }
             Log.e("@@@@", "media  LIST" + dynamicConcatenatingMediaSource.size)
             currentWindow = player.currentWindowIndex
             if (currentWindow < timeline.getWindowCount() - 1) {
                 player.seekToDefaultPosition(currentWindow + 1);
                 myPlayerApi(LandingPage.playingVideos.get(currentWindow).id, "next")
                 Log.e("@@@@", "next1111" + currentWindow)
             } else if (timeline.getWindow(currentWindow, window, false).isDynamic) {
                 player.seekToDefaultPosition();
                 Log.e("@@@@", "next2222" + currentWindow)
                 myPlayerApi(LandingPage.playingVideos.get(currentWindow).id, "next")
             }*//*

        }*/
    }

    fun DownloadTask(context: Activity, myVideos: MyVideos) {
        // val firstLine = myVideos.description.split('.');
        val i = Intent(Intent.ACTION_SEND)
        i.type = "text/plain"
        i.putExtra(Intent.EXTRA_TEXT, "text")
        i.putExtra(Intent.EXTRA_TEXT, myVideos.share_url)
        /*  i.putExtra(
              Intent.EXTRA_TEXT, myVideos.headline + "\n\n" + myVideos.short_desc + "\n\n" +
                      myVideos.share_url
          )*/
        context.startActivity(Intent.createChooser(i, "Share to"))
        /* val activities = context.packageManager.queryIntentActivities(i, 0)
         val appNames = ArrayList<String>()
         for (info in activities) {
             appNames.add(info.loadLabel(context.packageManager).toString())
         }

         val builder = AlertDialog.Builder(context)
         builder.setTitle("Complete Action using...")
         builder.setItems(appNames.toTypedArray<CharSequence>()) { dialog, item ->
             val info = activities[item]
             if (info.activityInfo.packageName == "com.twitter.android") {
                 i.putExtra(
                     Intent.EXTRA_TEXT,
                     myVideos.headline + "\n" + myVideos.short_video_filename
                 )
                 //Twitter was chosen
             } else {
                 i.putExtra(Intent.EXTRA_TEXT, myVideos.headline  + "\n" + myVideos.description+ "\n" + myVideos.short_video_filename)
             }
        */     // start the selected activity

        // i.setPackage(info.activityInfo.packageName)
        //  context.startActivity(i)
        /* }
         val alert = builder.create()
         alert.show()*/
    }


    /*fun sharePop() {
        val share = Intent(Intent.ACTION_SEND);
        share.setType("text/plain");
        share.putExtra(Intent.EXTRA_SUBJECT, "FlickBuzz App");
        val activities = getPackageManager().queryIntentActivities(share, 0);
        val appNames = ArrayList<String>()
        for (i in activities) {
            appNames.add(i.loadLabel(packageManager).toString())
        }

        var builder = AlertDialog.Builder(this);
        builder.setTitle("Complete Action using...");
        var charsequence: CharSequence=CharSequence[0]

        builder.setItems(appNames.toArray(charsequence[appNames.size]), object : DialogInterface.OnClickListener {
            override fun onClick(dialog: DialogInterface?, which: Int) {
                var info = activities.get(which);
                if (info.activityInfo.packageName.equals("com.twitter.android")) {
                    share.putExtra(
                        Intent.EXTRA_TEXT,
                        mySequence.get(currentWindow).headline +
                                "\n" + mySequence.get(currentWindow).short_video_filename
                    );
                } else {
                    share.putExtra(
                        Intent.EXTRA_TEXT,
                        mySequence.get(currentWindow).headline + "\n" + mySequence.get(currentWindow).description + "\n" + mySequence.get(
                            currentWindow
                        ).short_video_filename
                    );
                }
                share.setPackage(info.activityInfo.packageName);
                startActivity(share);
            }


        })
        val alert = builder.create();
        alert.show();

    }*/

    fun initUi(myVideo: MyVideos) {
        initFullscreenButton()
        my_recycler_view = findViewById<RecyclerView>(R.id.rc_list)
        my_recycler_view.setNestedScrollingEnabled(false)
        //  Log.e("###", "ui title" + myVideo.headline)
        tv_play_title.setText(myVideo.headline)
        tv_play_description.setText(myVideo.description)

        makeTextViewResizable(tv_play_description, 2, "View More", true)

        lay_fav = findViewById(R.id.lay_fav)
        img_fav = findViewById(R.id.img_fav)
        img_fav_done = findViewById(R.id.img_fav_done)
        img_share = findViewById(R.id.img_share)

        if (myVideo.fav_video.equals("y")) {
            img_fav_done.visibility = View.VISIBLE
            img_fav.visibility = View.GONE
        } else {
            img_fav.visibility = View.VISIBLE
            img_fav_done.visibility = View.GONE
        }

        if (mySequence.get(currentWindow).share_url.equals("")) {
            img_share.visibility = View.GONE
        } else {
            img_share.visibility = View.VISIBLE
        }

        img_share.setOnClickListener {
            /* if (setupPermissions()) {*/
            trackEvent(this@MyPlayer, "Player", "Share")
            share_flag = true;
            pausePlayer()
            DownloadTask(this@MyPlayer, mySequence.get(currentWindow))
            /*} else {
                Toast.makeText(this@MyPlayer, "Please allow storage permission to share video", Toast.LENGTH_SHORT)
                    .show()
            }*/
        }
        lay_fav.setOnClickListener {
            if (img_fav.getVisibility() == View.VISIBLE) {
                trackEvent(this@MyPlayer, "Player", "Favorite Add")
                favAddApi()
            } else {
                trackEvent(this@MyPlayer, "Player", "Favorite Remove")
                favRemoveApi()
            }
        }
    }

    fun favAddApi() {
        val params = HashMap<String, String>()
        params["action"] = "add_favorite"
        params["video_id"] = mySequence.get(currentWindow).id
        params["language"] = PrefManager.getIn().language.toLowerCase()
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
                        } else if (status == 14) run {
                            Utility.alertDialog(
                                this@MyPlayer,
                                jobj.getString("message")
                            )
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
        params["video_id"] = mySequence.get(currentWindow).id
        params["language"] = PrefManager.getIn().language.toLowerCase()
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
                        } else if (status == 14) run {
                            Utility.alertDialog(
                                this@MyPlayer,
                                jobj.getString("message")
                            )
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
                trackEvent(this@MyPlayer, "Player", "Player|Full Screen")
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
                trackEvent(this@MyPlayer, "Player", "Player|Shrink")
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
            //  myPlayerApi(mySequence.get(currentWindow))
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
            myPlayerApi(mySequence.get(currentWindow))
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN);
            mFullScreenIcon.setImageDrawable(
                ContextCompat.getDrawable(
                    this@MyPlayer,
                    R.drawable.ic_fullscreen_expand
                )
            );
        }
    }


    fun makeTextViewResizable(tv: TextView, maxLine: Int, expandText: String, viewMore: Boolean) {
        /*https://stackoverflow.com/questions/31668697/android-expandable-text-view-with-view-more-button-displaying-at-center-after*/
        //if (tv.tag == null) {
        tv.tag = tv.text
        //}

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
                        trackEvent(this@MyPlayer, "Player", "View Less")
                        makeTextViewResizable(tv, -1, "View Less", false)
                    } else {
                        trackEvent(this@MyPlayer, "Player", "View More")
                        makeTextViewResizable(tv, 2, "View More", true)
                    }
                }
            }, str.indexOf(spanableText), str.indexOf(spanableText) + spanableText.length, 0)

        }
        return ssb

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
                -> {
                    progressBar.visibility = View.GONE
                    //Toast.makeText(this@MyPlayer, "My Player Ending" + currentWindow, Toast.LENGTH_SHORT).show()
                }

            }
            updateButtonVisibilities()
        }


        override fun onPositionDiscontinuity(reason: Int) {
            super.onPositionDiscontinuity(reason)

            val newcurrentWindow = player.currentWindowIndex;

            // Log.e("@@@@@", " Window id" + currentWindow)
            // Log.e("@@@@@", " New Window id" + newcurrentWindow)

            // Log.e("&&&&&&", " video id 1" +mySequence.get(currentWindow).id)

            if (currentWindow != newcurrentWindow) {
                currentWindow = player.currentWindowIndex
                impressionTracker(mySequence.get(currentWindow))
                mySequence.get(currentWindow).id
                // myPlayerApi(LandingPage.playingVideos.get(currentWindow).id, "next")
                // Log.e("&&&&&&", " video id 2" +mySequence.get(currentWindow).id)
                /* val timeline = player.currentTimeline
                 if (timeline.isEmpty) {
                     return
                 }
                 currentWindow = player.currentWindowIndex
                 if (currentWindow < timeline.getWindowCount() - 1) {
                     myPlayerApi(LandingPage.playingVideos.get(currentWindow + 1).id)
                     Log.e("@@@@", "next3333" + currentWindow)
                 } else if (timeline.getWindow(currentWindow, window, false).isDynamic) {
                     Log.e("@@@@", "next4444" + currentWindow)
                     myPlayerApi(LandingPage.playingVideos.get(currentWindow).id)
                 }*/
            }
            //   Log.e("@@@@", "next4444" + currentWindow)
            /*if (currentWindow != 0)
                myPlayerApi(LandingPage.playingVideos.get(currentWindow).id)*//*
            Toast.makeText(this@MyPlayer,"Next or Previos", Toast.LENGTH_SHORT).show()
            Log.e("#####", "#$#$#" + currentWindow)*/
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

        /* override fun onPlaybackParametersChanged(playbackParameters: PlaybackParameters?) {
             super.onPlaybackParametersChanged(playbackParameters)
         }*/
    }


    fun myTracker() {
        //https://medium.com/google-exoplayer/exoplayer-2-x-track-selection-2b62ff712cc9
        //https://medium.com/@mayur_solanki/adaptive-streaming-with-exoplayer-c77b0032acdd
        //https://android.jlelse.eu/exoplayer-components-explained-9937e3a5d2f5
        //https://exoplayer.dev/guide.html
        //https://medium.com/google-exoplayer/exoplayer-2-x-track-selection-2b62ff712cc9
        //https://gist.github.com/abhiint16/b473e9b1111bd8bda4833c288ae6a1b4
        //https://stackoverflow.com/questions/52112981/customizing-exoplayer-quality-dialog-in-my-app
        val mappedTrackInfo = trackSelector!!.getCurrentMappedTrackInfo();
        if (mappedTrackInfo != null) {
            MappingTrackSelector.MappedTrackInfo.RENDERER_SUPPORT_NO_TRACKS
            val dialogPair =
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

    /* override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
         when (requestCode) {
             123 -> {
                 if (grantResults.isEmpty() || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                     Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show()
                 } else {
                     share_flag = true;
                     pausePlayer()
                     DownloadTask(this@MyPlayer, mySequence.get(currentWindow).short_video_filename)
                 }
             }
         }
     }*/

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
            playerView.useController = true

        }
        super.onPictureInPictureModeChanged(isInPictureInPictureMode, newConfig)
    }

//Called when the user touches the Home or Recents button to leave the app.
/* override fun onUserLeaveHint() {
     super.onUserLeaveHint()
     enterPIPMode()
     playerView.useController=true
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
                val rational = Rational(playerView.width, playerView.height)
                val params = PictureInPictureParams.Builder().setAspectRatio(rational)
                this.enterPictureInPictureMode(params.build())
            } else {
                this.enterPictureInPictureMode()
            }
            Handler().postDelayed({ checkPIPPermission() }, 30)
        }
    }


    @RequiresApi(Build.VERSION_CODES.N)
    fun checkPIPPermission() {
        isPIPModeeEnabled = isInPictureInPictureMode
        if (!isInPictureInPictureMode) {
            icon_pip.performClick()

            // onBackPressed()
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

    public override fun onStart() {
        super.onStart()

        if (SDK_INT > 23) initializePlayer()
    }

    public override fun onResume() {
        super.onResume()

        if (SDK_INT <= 23 || player == null) initializePlayer()
        //resumePlayer()
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
            if (!share_flag) {
                finishAndRemoveTask()
            } else {
                share_flag = false
                /* Handler().postDelayed(Runnable {
                     resumePlayer()
                 },300)
 */

            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (SDK_INT > 23) releasePlayer()
    }

    fun pausePlayer() {
        player.playWhenReady = false
        player.playbackState

    }

    /* fun resumePlayer(){
         if (SDK_INT <= 23 || player == null) initializePlayer()
         player.playWhenReady=true
         player.playbackState
         updateButtonVisibilities()
     }*/
}
