package com.aniapps.flicbuzzapp.player;

import android.content.Context;
import android.graphics.Point;
import android.net.Uri;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.aniapps.flicbuzzapp.R;
import com.aniapps.flicbuzzapp.adapters.VideoRecyclerViewAdapter;
import com.aniapps.flicbuzzapp.models.MyVideos;
import com.google.android.exoplayer2.*;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.source.hls.HlsMediaSource;
import com.google.android.exoplayer2.trackselection.*;
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.*;
import com.google.android.exoplayer2.util.Util;

import java.util.ArrayList;
import java.util.List;

public class ExoPlayerRecyclerView extends RecyclerView {
    private static final String TAG = "ExoPlayerRecyclerView";

    private List<MyVideos> videoInfoList = new ArrayList<>();
    private int videoSurfaceDefaultHeight = 0;
    private int screenDefaultHeight = 0;
    SimpleExoPlayer player;
    //surface view for playing video
    private PlayerView videoSurfaceView;
    private ImageView mCoverImage;

    private ProgressBar mProgressBar;
    private Context appContext;


    /**
     * the position of playing video
     */
    private int playPosition = -1;

    private boolean addedVideo = false;
    private View rowParent;

    /**
     * {@inheritDoc}
     *
     * @param context
     */
    public ExoPlayerRecyclerView(Context context) {
        super(context);
        initialize(context);
    }

    /**
     * {@inheritDoc}
     *
     * @param context
     * @param attrs
     */
    public ExoPlayerRecyclerView(Context context,
                                 AttributeSet attrs) {
        super(context, attrs);
        initialize(context);
    }

    /**
     * {@inheritDoc}
     *
     * @param context
     * @param attrs
     * @param defStyleAttr
     */
    public ExoPlayerRecyclerView(Context context,
                                 AttributeSet attrs,
                                 int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initialize(context);
    }

    public void setVideoInfoList(List<MyVideos> videoInfoList) {
        this.videoInfoList = videoInfoList;

    }


    /**
     * prepare for video play
     */
    //remove the player from the row
    private void removeVideoView(PlayerView videoView) {

        ViewGroup parent = (ViewGroup) videoView.getParent();
        if (parent == null) {
            return;
        }

        int index = parent.indexOfChild(videoView);
        if (index >= 0) {
            parent.removeViewAt(index);
            addedVideo = false;
        }

    }

    //play the video in the row
    public void playVideo() {
        int startPosition = ((LinearLayoutManager) getLayoutManager()).findFirstVisibleItemPosition();
        int endPosition = ((LinearLayoutManager) getLayoutManager()).findLastVisibleItemPosition();

        if (endPosition - startPosition > 1) {
            endPosition = startPosition + 1;
        }

        if (startPosition < 0 || endPosition < 0) {
            return;
        }

        int targetPosition;
        if (startPosition != endPosition) {
            int startPositionVideoHeight = getVisibleVideoSurfaceHeight(startPosition);
            int endPositionVideoHeight = getVisibleVideoSurfaceHeight(endPosition);
            targetPosition = startPositionVideoHeight > endPositionVideoHeight ? startPosition : endPosition;
        } else {
            targetPosition = startPosition;
        }

        if (targetPosition < 0 || targetPosition == playPosition) {
            return;
        }
        playPosition = targetPosition;
        if (videoSurfaceView == null) {
            return;
        }
        videoSurfaceView.setVisibility(INVISIBLE);
        removeVideoView(videoSurfaceView);

        // get target View targetPosition in RecyclerView
        int at = targetPosition - ((LinearLayoutManager) getLayoutManager()).findFirstVisibleItemPosition();

        View child = getChildAt(at);
        if (child == null) {
            return;
        }

        VideoRecyclerViewAdapter.ViewHolder holder
                = (VideoRecyclerViewAdapter.ViewHolder) child.getTag();
        if (holder == null) {
            playPosition = -1;
            return;
        }
        mCoverImage = holder.mCover;
        mProgressBar = holder.mProgressBar;
        FrameLayout frameLayout = holder.itemView.findViewById(R.id.video_layout);
        frameLayout.addView(videoSurfaceView);
        addedVideo = true;
        rowParent = holder.itemView;
        videoSurfaceView.requestFocus();
        // Bind the player to the view.
        videoSurfaceView.setPlayer(player);

        // Measures bandwidth during playback. Can be null if not required.
        DefaultBandwidthMeter defaultBandwidthMeter = new DefaultBandwidthMeter();
        // Produces DataSource instances through which media data is loaded.

       /* DataSource.Factory dataSourceFactory = new DefaultDataSourceFactory(appContext,
                Util.getUserAgent(appContext, "android_wave_list"), defaultBandwidthMeter);*/
        // This is the MediaSource representing the media to be played.
        DataSource.Factory dataSourceFactory=new DefaultDataSourceFactory(appContext,"ua");
        String uriString = videoInfoList.get(targetPosition).getVideo_filename();
        if (uriString != null) {
           /* MediaSource videoSource = new ExtractorMediaSource.Factory(dataSourceFactory)
                    .createMediaSource(Uri.parse(uriString));*/
            MediaSource videoSourc= new HlsMediaSource.Factory(dataSourceFactory).setAllowChunklessPreparation(true).createMediaSource(Uri.parse(uriString));
            // Prepare the player with the source.
            player.prepare(videoSourc);
            player.setPlayWhenReady(true);
        }


    }

    private int getVisibleVideoSurfaceHeight(int playPosition) {
        int at = playPosition - ((LinearLayoutManager) getLayoutManager()).findFirstVisibleItemPosition();

        View child = getChildAt(at);
        if (child == null) {
            return 0;
        }

        int[] location01 = new int[2];
        child.getLocationInWindow(location01);

        if (location01[1] < 0) {
            return location01[1] + videoSurfaceDefaultHeight;
        } else {
            return screenDefaultHeight - location01[1];
        }
    }


    private void initialize(Context context) {

        appContext = context.getApplicationContext();
        Display display = ((WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        Point point = new Point();
        display.getSize(point);
        videoSurfaceDefaultHeight = point.x;

        screenDefaultHeight = point.y;
        videoSurfaceView = new PlayerView(appContext);
        videoSurfaceView.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_ZOOM);

        BandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
        TrackSelection.Factory videoTrackSelectionFactory =
                new AdaptiveTrackSelection.Factory(bandwidthMeter);
        TrackSelector trackSelector =
                new DefaultTrackSelector(videoTrackSelectionFactory);
       /* LoadControl loadControl = new DefaultLoadControl(
                new DefaultAllocator(true, 16),
                VideoPlayerConfig.MIN_BUFFER_DURATION,
                VideoPlayerConfig.MAX_BUFFER_DURATION,
                VideoPlayerConfig.MIN_PLAYBACK_START_BUFFER,
                VideoPlayerConfig.MIN_PLAYBACK_RESUME_BUFFER, -1, true);*/

        // 2. Create the player
        player = ExoPlayerFactory.newSimpleInstance(appContext, trackSelector);
        // Bind the player to the view.
        videoSurfaceView.setUseController(false);
        videoSurfaceView.setPlayer(player);

        addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE) {
                    playVideo();
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
            }
        });

        addOnChildAttachStateChangeListener(new OnChildAttachStateChangeListener() {
            @Override
            public void onChildViewAttachedToWindow(View view) {

            }

            @Override
            public void onChildViewDetachedFromWindow(View view) {
                if (addedVideo && rowParent != null && rowParent.equals(view)) {
                    removeVideoView(videoSurfaceView);
                    playPosition = -1;
                    videoSurfaceView.setVisibility(INVISIBLE);
                }

            }
        });
        player.addListener(new Player.EventListener() {
            @Override
            public void onTimelineChanged(Timeline timeline, Object manifest, int reason) {

            }

            @Override
            public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {

            }

            @Override
            public void onLoadingChanged(boolean isLoading) {

            }

            @Override
            public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
                switch (playbackState) {

                    case Player.STATE_BUFFERING:
                        videoSurfaceView.setAlpha(0.5f);
                        Log.e(TAG, "onPlayerStateChanged: Buffering ");
                        if (mProgressBar != null) {
                            mProgressBar.setVisibility(VISIBLE);
                        }

                        break;
                    case Player.STATE_ENDED:
                        player.seekTo(0);
                        break;
                    case Player.STATE_IDLE:
                       if (mProgressBar != null) {
                            mProgressBar.setVisibility(GONE);
                        }
                        break;
                    case Player.STATE_READY:
                        Log.e(TAG, "onPlayerStateChanged: Ready ");
                        if (mProgressBar != null) {
                            mProgressBar.setVisibility(GONE);
                        }
                        videoSurfaceView.setVisibility(VISIBLE);
                        videoSurfaceView.setAlpha(1);
                        mCoverImage.setVisibility(GONE);

                        break;
                    default:
                        break;
                }
            }

            @Override
            public void onRepeatModeChanged(int repeatMode) {

            }

            @Override
            public void onShuffleModeEnabledChanged(boolean shuffleModeEnabled) {

            }

            @Override
            public void onPlayerError(ExoPlaybackException error) {

            }

            @Override
            public void onPositionDiscontinuity(int reason) {

            }

            @Override
            public void onPlaybackParametersChanged(PlaybackParameters playbackParameters) {

            }

            @Override
            public void onSeekProcessed() {

            }
        });
    }

    public void onPausePlayer() {
        if (videoSurfaceView != null) {
            removeVideoView(videoSurfaceView);
            player.release();
            videoSurfaceView = null;
        }
    }

    public void onRestartPlayer() {
        if (videoSurfaceView == null) {
            playPosition = -1;
            playVideo();
        }
    }

    /**
     * release memory
     */
    public void onRelease() {

        if (player != null) {
            player.release();
            player = null;
        }

        rowParent = null;
    }


}

