package com.pseedk.superplayer.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.pseedk.superplayer.R
import com.pseedk.superplayer.databinding.SongBinding
import com.pseedk.superplayer.fragments.ListMusicFragment
import com.pseedk.superplayer.fragments.ListMusicFragmentDirections
import com.pseedk.superplayer.model.Song

class SongAdapter : RecyclerView.Adapter<SongAdapter.SongViewHolder>() {

    inner class SongViewHolder(val binding: SongBinding) : RecyclerView.ViewHolder(binding.root)

    private val differCallback = object : DiffUtil.ItemCallback<Song>() {
        override fun areItemsTheSame(oldItem: Song, newItem: Song): Boolean {
            return oldItem.songUri == newItem.songUri
        }

        override fun areContentsTheSame(oldItem: Song, newItem: Song): Boolean {
            return oldItem == newItem
        }
    }

    val differ = AsyncListDiffer(this, differCallback)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SongViewHolder {
        return SongViewHolder(
            SongBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: SongViewHolder, position: Int) {
        val currSong = differ.currentList[position]
        holder.binding.apply {
            tvDuration.text = currSong.songDuration
            songTitle.text = currSong.songTitle
            songArtist.text = currSong.songArtist
            tvOrder.text = "${position + 1}"
        }
        holder.itemView.setOnClickListener { mView ->
           val direction = ListMusicFragmentDirections
               .actionMusicListFragmentToPlayMusicFragment(currSong)
            mView.findNavController().navigate(direction)
        }
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }
}