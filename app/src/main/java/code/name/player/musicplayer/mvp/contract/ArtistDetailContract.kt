package code.name.player.musicplayer.mvp.contract


import code.name.player.musicplayer.model.Artist
import code.name.player.musicplayer.mvp.BasePresenter
import code.name.player.musicplayer.mvp.BaseView


/**
 * Created by hemanths on 20/08/17.
 */

interface ArtistDetailContract {
    interface ArtistsDetailsView : BaseView<Artist>

    interface Presenter : BasePresenter<ArtistsDetailsView> {
        fun loadArtistById()
    }
}
