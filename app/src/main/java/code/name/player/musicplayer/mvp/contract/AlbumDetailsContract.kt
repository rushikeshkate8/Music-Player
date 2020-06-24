package code.name.player.musicplayer.mvp.contract


import code.name.player.musicplayer.model.Album
import code.name.player.musicplayer.mvp.BasePresenter
import code.name.player.musicplayer.mvp.BaseView

interface AlbumDetailsContract {

    interface AlbumDetailsView : BaseView<Album>

    interface Presenter : BasePresenter<AlbumDetailsView> {

        fun loadAlbumSongs(albumId: Int)
    }
}
