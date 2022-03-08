package com.pseedk.superplayer.fragments

import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.pseedk.superplayer.R
import com.pseedk.superplayer.adapter.SongAdapter
import com.pseedk.superplayer.databinding.FragmentListMusicBinding
import com.pseedk.superplayer.helper.Constants
import com.pseedk.superplayer.helper.Constants.toast
import com.pseedk.superplayer.model.Song
import java.util.ArrayList

class ListMusicFragment : Fragment(R.layout.fragment_list_music) {

    private var _binding: FragmentListMusicBinding? = null
    private val binding get() = _binding!!
    private var songList: MutableList<Song> = ArrayList()
    private lateinit var songAdapter: SongAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentListMusicBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loadSongs()
        setupRecyclerView()
        checkUserPermission()
    }

    private fun setupRecyclerView() {
        songAdapter = SongAdapter()
        binding.rvSongList.apply {
            adapter = songAdapter
            layoutManager = LinearLayoutManager(activity)
            setHasFixedSize(true)
        }
        songAdapter.differ.submitList(songList)
        songList.clear()
    }

    @SuppressLint("Range")
    private fun loadSongs() {
        val allSongUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        val selection = MediaStore.Audio.Media.IS_MUSIC + "!=0"
        val sortOrder = " ${MediaStore.Audio.Media.DISPLAY_NAME} ASC"

        val cursor = activity?.applicationContext?.contentResolver!!.query(
            allSongUri, null, selection, null, sortOrder
        )
        if (cursor != null) {
            while (cursor.moveToNext()) {
                val songUri = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA))
                val songAuthor =
                    cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST))
                val songTitle =
                    cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DISPLAY_NAME))
                val songDuration =
                    cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DURATION))

                val songDurLong = songDuration.toLong()
                songList.add(
                    Song(
                        songTitle, songAuthor, songUri, Constants.durationConverter(songDurLong)
                    )
                )
            }
            cursor.close()
        }
    }

    private fun checkUserPermission() {
        if (activity?.let {
                ActivityCompat.checkSelfPermission(
                    it,
                    android.Manifest.permission.READ_EXTERNAL_STORAGE
                )
            } != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(
                arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE),
                Constants.REQUEST_CODE_FOR_PERMISSIONS
            )
            return
        }
        loadSongs()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            Constants.REQUEST_CODE_FOR_PERMISSIONS ->
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    activity?.toast("Permission Granted")
                } else {
                    activity?.toast("Permission Denied")
                }
            else -> {
                super.onRequestPermissionsResult(requestCode, permissions, grantResults)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}