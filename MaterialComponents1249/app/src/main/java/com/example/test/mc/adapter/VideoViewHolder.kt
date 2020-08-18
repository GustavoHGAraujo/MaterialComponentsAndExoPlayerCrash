package com.example.test.mc.adapter

import android.view.View
import androidx.appcompat.widget.AppCompatImageView
import androidx.constraintlayout.widget.Group
import androidx.recyclerview.widget.RecyclerView
import com.example.test.mc.R
import com.google.android.exoplayer2.ui.PlayerView
import com.google.android.material.button.MaterialButton

class VideoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val imageView: AppCompatImageView = itemView.findViewById(R.id.image_view)
    val playerView: PlayerView = itemView.findViewById(R.id.player_view)
    val playButtonView: AppCompatImageView = itemView.findViewById(R.id.play_button)
    val detailsButton: MaterialButton = itemView.findViewById(R.id.details_button)
    val thumbnailGroup: Group = itemView.findViewById(R.id.thumb_group)
}