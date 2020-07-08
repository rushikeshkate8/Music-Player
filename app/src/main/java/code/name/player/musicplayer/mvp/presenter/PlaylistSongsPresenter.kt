package code.name.player.musicplayer.mvp.presenter

import code.name.player.musicplayer.model.Playlist
import code.name.player.musicplayer.mvp.Presenter
import code.name.player.musicplayer.mvp.contract.PlaylistSongsContract


class PlaylistSongsPresenter(private val view: PlaylistSongsContract.PlaylistSongsView,
                             private val mPlaylist: Playlist) : Presenter(), PlaylistSongsContract.Presenter {


    override fun subscribe() {
        loadSongs(mPlaylist)
    }

    override fun unsubscribe() {
        disposable.clear()
    }

    override fun loadSongs(playlist: Playlist) {
        disposable.add(repository.getPlaylistSongs(playlist)
                .subscribeOn(schedulerProvider.io())
                .observeOn(schedulerProvider.ui())
                .doOnSubscribe { view.loading() }
                .subscribe({ songs -> view.showData(songs) },
                        { view.showEmptyView() },
                        { view.completed() }))
    }
}
