package code.name.player.musicplayer.mvp.contract

import code.name.player.musicplayer.model.Playlist
import code.name.player.musicplayer.mvp.BasePresenter
import code.name.player.musicplayer.mvp.BaseView

import java.util.ArrayList


interface PlaylistContract {
    interface PlaylistView : BaseView<ArrayList<Playlist>>

    interface Presenter : BasePresenter<PlaylistView> {
        fun loadPlaylists()
    }
}
