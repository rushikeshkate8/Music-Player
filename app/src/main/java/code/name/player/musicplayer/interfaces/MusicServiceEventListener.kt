package code.name.player.musicplayer.interfaces


interface MusicServiceEventListener {
    fun onServiceConnected()

    fun onServiceDisconnected()

    fun onQueueChanged()

    fun onPlayingMetaChanged()

    fun onPlayStateChanged()

    fun onRepeatModeChanged()

    fun onShuffleModeChanged()

    fun onMediaStoreChanged()
}
