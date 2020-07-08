package code.name.player.musicplayer.mvp


interface BaseView<T> {
    fun loading()

    fun showData(list: T)

    fun showEmptyView()

    fun completed()
}
