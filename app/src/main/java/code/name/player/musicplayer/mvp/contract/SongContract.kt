package code.name.player.musicplayer.mvp.contract


import code.name.player.musicplayer.model.Song
import code.name.player.musicplayer.mvp.BasePresenter
import code.name.player.musicplayer.mvp.BaseView

import java.util.ArrayList



interface SongContract {

    interface SongView : BaseView<ArrayList<Song>>

    interface Presenter : BasePresenter<SongView> {
        fun loadSongs()
    }
}
