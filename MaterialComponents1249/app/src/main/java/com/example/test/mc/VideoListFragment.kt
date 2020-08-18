package com.example.test.mc

import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.example.test.mc.adapter.VideoListAdapter
import com.example.test.mc.adapter.VideoViewHolder
import com.example.test.mc.model.Mock
import com.example.test.mc.model.Video
import com.google.android.material.transition.Hold

class VideoListFragment : Fragment() {

    private val videosAdapter = VideoListAdapter(Mock.videoList).apply {
        listener = object : VideoListAdapter.Listener {
            override fun onVideoClick(video: Video, holder: VideoViewHolder) {
                holder.thumbnailGroup.visibility = View.GONE

                VideoPlayerController.play(
                    context = requireContext(),
                    video = video,
                    playerView = holder.playerView
                )
            }

            override fun onDetailsClick(video: Video, holder: VideoViewHolder) {
                navigateToDetailsPage(video, holder)
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_video_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val recyclerView = view.findViewById<RecyclerView>(R.id.recycler_view)

        recyclerView.adapter = videosAdapter
    }

    fun navigateToDetailsPage(video: Video, holder: VideoViewHolder) {
        val keepPlayingVideo = true
        val directions = VideoListFragmentDirections.toVideoFragment(video, keepPlayingVideo)

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            findNavController().navigate(directions)
            return
        }

        exitTransition = Hold()

        val extras = FragmentNavigatorExtras(
            holder.playerView to holder.playerView.transitionName
        )

        findNavController().navigate(directions, extras)
    }

}