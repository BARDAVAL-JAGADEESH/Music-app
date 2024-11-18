package com.bardaval.musicapp

data class Song(
    val title: String,
    val artist: String,
    val path: String,
    val albumArtUri: String,
    var isPlaying: Boolean = false
)


