package code.name.player.musicplayer.mvp.presenter

import android.os.Bundle
import code.name.player.musicplayer.model.Artist
import code.name.player.musicplayer.mvp.Presenter
import code.name.player.musicplayer.mvp.contract.ArtistDetailContract
import code.name.player.musicplayer.ui.activities.ArtistDetailActivity


/**
 * Created by hemanths on 20/08/17.
 */

class ArtistDetailsPresenter(private val view: ArtistDetailContract.ArtistsDetailsView,
                             private val bundle: Bundle) : Presenter(), ArtistDetailContract.Presenter {

    override fun subscribe() {
        loadArtistById()
    }

    override fun unsubscribe() {
        disposable.clear()
    }

    override fun loadArtistById() {
        disposable.add(repository.getArtistById(bundle.getInt(ArtistDetailActivity.EXTRA_ARTIST_ID).toLong())
                .subscribeOn(schedulerProvider.computation())
                .observeOn(schedulerProvider.ui())
                .doOnSubscribe { view.loading() }
                .subscribe({ this.showArtist(it) },
                        { view.showEmptyView() },
                        { view.completed() }))
    }

    private fun showArtist(album: Artist?) {
        if (album != null) {
            view.showData(album)
        } else {
            view.showEmptyView()
        }
    }
}
