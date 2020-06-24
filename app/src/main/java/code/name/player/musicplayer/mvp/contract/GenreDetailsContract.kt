package code.name.player.musicplayer.mvp.contract

import code.name.player.musicplayer.model.Song
import code.name.player.musicplayer.mvp.BasePresenter
import code.name.player.musicplayer.mvp.BaseView

import java.util.ArrayList

/**
 * @author Hemanth S (h4h13).
 */

interface GenreDetailsContract {
    interface GenreDetailsView : BaseView<ArrayList<Song>>

    interface Presenter : BasePresenter<GenreDetailsView> {
        fun loadGenre(genreId: Int)
    }
}
