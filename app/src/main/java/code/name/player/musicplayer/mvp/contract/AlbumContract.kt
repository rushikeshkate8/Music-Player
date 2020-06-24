package code.name.player.musicplayer.mvp.contract

import code.name.player.musicplayer.model.Album
import code.name.player.musicplayer.mvp.BasePresenter
import code.name.player.musicplayer.mvp.BaseView
import java.util.ArrayList

interface AlbumContract {

    interface AlbumView : BaseView<ArrayList<Album>>

    interface Presenter : BasePresenter<AlbumView> {

        fun loadAlbums()
    }

}
