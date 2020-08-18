package com.example.test.mc.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.ViewCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.example.test.mc.R
import com.example.test.mc.model.Video

class VideoListAdapter(private val videoList: List<Video>) :
    RecyclerView.Adapter<VideoViewHolder>() {

    interface Listener {
        fun onVideoClick(video: Video, holder: VideoViewHolder)
        fun onDetailsClick(video: Video, holder: VideoViewHolder)
    }

    var listener: Listener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VideoViewHolder {
        return LayoutInflater.from(parent.context)
            .inflate(R.layout.viewholder_video, parent, false)
            .let { VideoViewHolder(it) }
    }

    override fun getItemCount() = videoList.size

    override fun onBindViewHolder(holder: VideoViewHolder, position: Int) {
        val video = videoList[position]
        val context = holder.itemView.context

        Glide.with(context)
            .load(video.thumbUrl)
            .asBitmap()
            .transform(CenterCrop(context))
            .into(holder.imageView)

        ViewCompat.setTransitionName(holder.playerView, video.name)

        holder.imageView.visibility = View.VISIBLE
        holder.playButtonView.visibility = View.VISIBLE

        holder.detailsButton.setOnClickListener { listener?.onDetailsClick(video, holder) }
        holder.imageView.setOnClickListener { listener?.onVideoClick(video, holder) }
        holder.playButtonView.setOnClickListener { listener?.onVideoClick(video, holder) }
        holder.playerView.setOnClickListener { listener?.onVideoClick(video, holder) }
    }
}