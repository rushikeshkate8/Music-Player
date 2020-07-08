package code.name.player.musicplayer.mvp.contract


import code.name.player.musicplayer.model.Artist
import code.name.player.musicplayer.mvp.BasePresenter
import code.name.player.musicplayer.mvp.BaseView

import java.util.ArrayList


interface ArtistContract {
    interface ArtistView : BaseView<ArrayList<Artist>>

    interface Presenter : BasePresenter<ArtistView> {
        fun loadArtists()
    }
}
