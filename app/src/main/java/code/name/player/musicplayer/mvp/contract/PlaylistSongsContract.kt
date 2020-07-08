package code.name.player.musicplayer.mvp.contract

import code.name.player.musicplayer.model.Playlist
import code.name.player.musicplayer.model.Song
import code.name.player.musicplayer.mvp.BasePresenter
import code.name.player.musicplayer.mvp.BaseView

import java.util.ArrayList



interface PlaylistSongsContract {
    interface PlaylistSongsView : BaseView<ArrayList<Song>>

    interface Presenter : BasePresenter<PlaylistSongsView> {
        fun loadSongs(playlist: Playlist)
    }
}
