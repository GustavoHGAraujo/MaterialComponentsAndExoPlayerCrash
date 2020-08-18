package com.example.test.mc

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.ViewCompat
import androidx.navigation.fragment.navArgs
import com.google.android.exoplayer2.ui.PlayerView
import com.google.android.material.transition.MaterialContainerTransform

class VideoDetailsFragment : Fragment() {

    private val args: VideoDetailsFragmentArgs by navArgs()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedElementEnterTransition = MaterialContainerTransform()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_video_details, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val video = args.video
        val playerView = view.findViewById<PlayerView>(R.id.player_view)

        ViewCompat.setTransitionName(playerView, video.name)

        if (args.keepPlayingVideo) {
            VideoPlayerController.play(
                context = requireContext(),
                playerView = playerView,
                video = video,
                resetPosition = false
            )
        }
    }

}