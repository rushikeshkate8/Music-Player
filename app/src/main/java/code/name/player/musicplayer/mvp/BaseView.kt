package code.name.player.musicplayer.mvp

/**
 * Created by hemanths on 09/08/17.
 */

interface BaseView<T> {
    fun loading()

    fun showData(list: T)

    fun showEmptyView()

    fun completed()
}
