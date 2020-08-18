package com.example.test.mc

import android.content.Context
import android.net.Uri
import android.os.Handler
import android.os.Looper
import android.util.Log
import com.example.test.mc.model.Video
import com.google.android.exoplayer2.ExoPlayerFactory
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.analytics.AnalyticsListener
import com.google.android.exoplayer2.source.ExtractorMediaSource
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.source.MediaSourceEventListener
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.ui.PlayerView
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory
import com.google.android.exoplayer2.video.VideoListener
import java.io.IOException
import java.lang.Exception

object VideoPlayerController {

    enum class PlaybackState {
        Idle,
        Loading,
        Ready,
        Playing,
        Paused,
        Ended
    }

    interface PlaybackStateChangeListener {
        fun onPlaybackStateChanged(state: PlaybackState?)
    }

    private val uiHandler = Handler(Looper.getMainLooper())
    private val playbackStateLock = Any()
    private val playbackStateChangeListeners = mutableListOf<PlaybackStateChangeListener>()
    private val playerEventListener = object : Player.EventListener {
        override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
            when (playbackState) {
                Player.STATE_IDLE -> onPlayerStateChangedTo(PlaybackState.Idle, playWhenReady)
                Player.STATE_BUFFERING -> onPlayerStateChangedTo(
                    PlaybackState.Loading,
                    playWhenReady
                )
                Player.STATE_READY -> onPlayerStateChangedTo(PlaybackState.Ready, playWhenReady)
                Player.STATE_ENDED -> onPlayerStateChangedTo(PlaybackState.Ended, playWhenReady)
            }
        }
    }

    private val isInitialized: Boolean
        get() = this::videoPlayer.isInitialized

    private lateinit var videoPlayer: SimpleExoPlayer
    private var lastVideo: Video? = null
    private var lastPlayerView: PlayerView? = null

    var currentPlaybackState: PlaybackState? = null
        private set(value) {
            if (value == field) {
                return
            }

            field = value
            notifyPlaybackStateChanged()
        }

    private fun buildMediaSource(uri: Uri): MediaSource? {
        log("buildMediaSource(uri: $uri)")
        return ExtractorMediaSource.Factory(DefaultHttpDataSourceFactory("http.agent"))
            .createMediaSource(uri)
    }

    private fun log(msg: String, exception: Exception? = null) {
        val tag = this::class.simpleName

        if (exception == null) {
            Log.d(tag, msg)
        } else {
            Log.d(tag, msg, exception)
        }
    }

    private fun notifyPlaybackStateChanged() {
        synchronized(playbackStateChangeListeners) {
            playbackStateChangeListeners.forEach { listener ->
                onUiThread { listener.onPlaybackStateChanged(currentPlaybackState) }
            }
        }
    }

    private fun onPlayerStateChangedTo(state: PlaybackState, playWhenReady: Boolean) {
        log("onPlayerStateChangedTo(state: $state, playWhenReady: $playWhenReady)")
        synchronized(playbackStateLock) {
            currentPlaybackState = state

            if (currentPlaybackState == PlaybackState.Ready && playWhenReady) {
                currentPlaybackState = PlaybackState.Playing
            }
        }
    }

    private fun play(
        video: Video,
        playerView: PlayerView,
        playWhenReady: Boolean,
        resetPosition: Boolean
    ) {
        log("play(video: $video, playWhenReady: $playWhenReady)")

        synchronized(playbackStateLock) {
            require(isInitialized)

            currentPlaybackState = PlaybackState.Loading
            lastVideo = video

            val uri = Uri.parse(lastVideo!!.url)
            val mediaSource = buildMediaSource(uri)

            videoPlayer.playWhenReady = playWhenReady
            videoPlayer.prepare(mediaSource, resetPosition, true)


            if (lastPlayerView != playerView) {
                lastPlayerView?.player = null
            }

            lastPlayerView = playerView
            lastPlayerView!!.player = videoPlayer
        }
    }

    @JvmStatic
    fun registerPlaybackStateChangeListener(playbackStateChangeListener: PlaybackStateChangeListener) {
        synchronized(playbackStateChangeListeners) {
            if (playbackStateChangeListeners.contains(playbackStateChangeListener)) {
                return
            }

            playbackStateChangeListeners.add(playbackStateChangeListener)
        }
    }

    @JvmStatic
    fun unregisterPlaybackStateChangeListener(playbackStateChangeListener: PlaybackStateChangeListener) {
        synchronized(playbackStateChangeListeners) {
            playbackStateChangeListeners.remove(playbackStateChangeListener)
        }
    }

    @JvmStatic
    private fun init(context: Context) {
        log("init()")

        val videoTrackSelectionFactory = AdaptiveTrackSelection.Factory()
        val trackSelector = DefaultTrackSelector(videoTrackSelectionFactory)

        videoPlayer = ExoPlayerFactory.newSimpleInstance(context, trackSelector)
        videoPlayer.addListener(playerEventListener)
        videoPlayer.addAnalyticsListener(object : AnalyticsListener {
            override fun onLoadError(
                eventTime: AnalyticsListener.EventTime?,
                loadEventInfo: MediaSourceEventListener.LoadEventInfo?,
                mediaLoadData: MediaSourceEventListener.MediaLoadData?,
                error: IOException?,
                wasCanceled: Boolean
            ) {
                """onLoadError(
                |   eventTime: $eventTime,
                |   loadEventInfo: $loadEventInfo,
                |   mediaLoadData: $mediaLoadData,
                |   wasCanceled: $wasCanceled
                )""".trimMargin().let {
                    log(it, error)
                }
            }
        })
    }

    @JvmStatic
    fun play(
        context: Context,
        video: Video,
        playerView: PlayerView,
        playWhenReady: Boolean = true,
        resetPosition: Boolean = true
    ) {
        if (!isInitialized) {
            init(context)
        }

        play(video, playerView, playWhenReady, resetPosition)
    }

    @JvmStatic
    fun pause() {
        log("pause()")
        videoPlayer.playWhenReady = false
        currentPlaybackState = PlaybackState.Paused
    }

    @JvmStatic
    fun resume() {
        log("resume()")
        videoPlayer.playWhenReady = true
        currentPlaybackState = PlaybackState.Playing
    }

    @JvmStatic
    fun resumeInto(playerView: PlayerView) {
        log("resumeInto()")
        lastPlayerView?.player = null
        lastPlayerView = playerView
        lastPlayerView!!.player = videoPlayer

        videoPlayer.playWhenReady = true
        currentPlaybackState = PlaybackState.Playing
    }

    private fun onUiThread(action: () -> Unit) = uiHandler.post(action)

}