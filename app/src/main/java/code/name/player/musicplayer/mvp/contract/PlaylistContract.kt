package code.name.player.musicplayer.mvp.contract

import code.name.player.musicplayer.model.Playlist
import code.name.player.musicplayer.mvp.BasePresenter
import code.name.player.musicplayer.mvp.BaseView

import java.util.ArrayList

/**
 * Created by hemanths on 19/08/17.
 */

interface PlaylistContract {
    interface PlaylistView : BaseView<ArrayList<Playlist>>

    interface Presenter : BasePresenter<PlaylistView> {
        fun loadPlaylists()
    }
}
