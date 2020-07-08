package code.name.player.musicplayer.mvp.presenter

import code.name.player.musicplayer.model.Genre
import code.name.player.musicplayer.mvp.Presenter
import code.name.player.musicplayer.mvp.contract.GenreContract
import java.util.*


class GenrePresenter(
        private val view: GenreContract.GenreView) : Presenter(), GenreContract.Presenter {

    override fun subscribe() {
        loadGenre()
    }

    override fun unsubscribe() {
        disposable.clear()
    }

    override fun loadGenre() {
        disposable.add(repository.allGenres
                .subscribeOn(schedulerProvider.computation())
                .observeOn(schedulerProvider.ui())
                .doOnSubscribe { view.loading() }
                .subscribe({ this.showList(it) },
                        { view.showEmptyView() },
                        { view.completed() }))
    }

    private fun showList(genres: ArrayList<Genre>) {
        if (genres.isEmpty()) {
            view.showEmptyView()
        } else {
            view.showData(genres)
        }
    }
}
