package code.name.player.musicplayer.mvp.contract

import code.name.player.musicplayer.model.Genre
import code.name.player.musicplayer.mvp.BasePresenter
import code.name.player.musicplayer.mvp.BaseView

import java.util.ArrayList

/**
 * @author Hemanth S (h4h13).
 */

interface GenreContract {
    interface GenreView : BaseView<ArrayList<Genre>>

    interface Presenter : BasePresenter<GenreView> {
        fun loadGenre()
    }
}
