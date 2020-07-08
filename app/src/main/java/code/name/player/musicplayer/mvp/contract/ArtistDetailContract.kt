package code.name.player.musicplayer.mvp.contract


import code.name.player.musicplayer.model.Artist
import code.name.player.musicplayer.mvp.BasePresenter
import code.name.player.musicplayer.mvp.BaseView



interface ArtistDetailContract {
    interface ArtistsDetailsView : BaseView<Artist>

    interface Presenter : BasePresenter<ArtistsDetailsView> {
        fun loadArtistById()
    }
}
