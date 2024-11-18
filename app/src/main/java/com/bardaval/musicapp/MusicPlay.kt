package com.bardaval.musicapp

import android.Manifest
import android.content.ContentUris
import android.content.Context
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.PowerManager
import android.provider.MediaStore
import android.widget.Button
import android.widget.SeekBar
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class MusicPlay : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var songsAdapter: SongsAdapter
    private lateinit var mediaPlayer: MediaPlayer
    private lateinit var seekBar: SeekBar
    private lateinit var nextButton: Button
    private lateinit var previousButton: Button
    private val handler = Handler(Looper.getMainLooper())
    private lateinit var wakeLock: PowerManager.WakeLock
    private var currentSongIndex = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_music_play)

        recyclerView = findViewById(R.id.recyclerView)
        seekBar = findViewById(R.id.seekBar)
        nextButton = findViewById(R.id.nextButton)
        previousButton = findViewById(R.id.previousButton)

        val powerManager = getSystemService(Context.POWER_SERVICE) as PowerManager
        wakeLock = powerManager.newWakeLock(
            PowerManager.SCREEN_DIM_WAKE_LOCK or PowerManager.ON_AFTER_RELEASE,
            "MusicPlayer::WakeLock"
        )
        wakeLock.acquire()

        // Initialize MediaPlayer
        mediaPlayer = MediaPlayer().apply {
            setWakeMode(applicationContext, PowerManager.PARTIAL_WAKE_LOCK)
            setAudioAttributes(
                AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .build()
            )
            setOnCompletionListener {
                playNextSong()
            }
        }

        recyclerView.layoutManager = LinearLayoutManager(this)

        // Request permissions
        requestPermissions()

        nextButton.setOnClickListener { playNextSong() }
        previousButton.setOnClickListener { playPreviousSong() }
    }

    private fun requestPermissions() {
        val permissions = arrayOf(
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.RECORD_AUDIO
        )
        val permissionResultLauncher =
            registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
                if (permissions[Manifest.permission.READ_EXTERNAL_STORAGE] == true &&
                    permissions[Manifest.permission.RECORD_AUDIO] == true
                ) {
                    loadSongs()
                } else {
                    // Handle permissions denied
                }
            }

        permissionResultLauncher.launch(permissions)
    }

    private fun loadSongs() {
        val songList = ArrayList<Song>()

        val projection = arrayOf(
            MediaStore.Audio.Media._ID,
            MediaStore.Audio.Media.TITLE,
            MediaStore.Audio.Media.ARTIST,
            MediaStore.Audio.Media.DATA,
            MediaStore.Audio.Media.ALBUM_ID
        )

        val selection = "${MediaStore.Audio.Media.IS_MUSIC} != 0"

        val cursor = contentResolver.query(
            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
            projection,
            selection,
            null,
            null
        )

        cursor?.use { cursor ->
            val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID)
            val titleColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE)
            val artistColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST)
            val pathColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA)
            val albumIdColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM_ID)

            while (cursor.moveToNext()) {
                val id = cursor.getLong(idColumn)
                val title = cursor.getString(titleColumn)
                val artist = cursor.getString(artistColumn)
                val path = cursor.getString(pathColumn)
                val albumId = cursor.getLong(albumIdColumn)
                val albumArtUri = getAlbumArtUri(albumId)

                val song = Song(title, artist, path, albumArtUri)
                songList.add(song)
            }
        }

        songsAdapter = SongsAdapter(songList) { song ->
            // Handle song selection and playback
            if (mediaPlayer.isPlaying) {
                mediaPlayer.stop()
            }
            currentSongIndex = songList.indexOf(song)
            playSong(song)
        }
        recyclerView.adapter = songsAdapter
    }

    private fun playSong(song: Song) {
        try {
            mediaPlayer.reset()
            mediaPlayer.setDataSource(song.path)
            mediaPlayer.prepare()
            mediaPlayer.start()
            // Set isPlaying property to true for the selected song
            songsAdapter.updatePlayingSong(currentSongIndex)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun playNextSong() {
        if (currentSongIndex != -1 && currentSongIndex < songsAdapter.itemCount - 1) {
            currentSongIndex++
            val nextSong = songsAdapter.getItem(currentSongIndex)
            playSong(nextSong)
        }
    }

    private fun playPreviousSong() {
        if (currentSongIndex != -1 && currentSongIndex > 0) {
            currentSongIndex--
            val previousSong = songsAdapter.getItem(currentSongIndex)
            playSong(previousSong)
        }
    }

    private fun getAlbumArtUri(albumId: Long): String {
        val albumArtUri = ContentUris.withAppendedId(
            Uri.parse("content://media/external/audio/albumart"),
            albumId
        )
        return albumArtUri.toString()
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer.release() // Release MediaPlayer resources
        if (wakeLock.isHeld) {
            wakeLock.release()
        }
    }
}
