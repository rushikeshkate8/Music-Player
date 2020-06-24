package code.name.player.musicplayer.mvp.contract


import code.name.player.musicplayer.model.Artist
import code.name.player.musicplayer.mvp.BasePresenter
import code.name.player.musicplayer.mvp.BaseView

import java.util.ArrayList


/**
 * Created by hemanths on 16/08/17.
 */

interface ArtistContract {
    interface ArtistView : BaseView<ArrayList<Artist>>

    interface Presenter : BasePresenter<ArtistView> {
        fun loadArtists()
    }
}
