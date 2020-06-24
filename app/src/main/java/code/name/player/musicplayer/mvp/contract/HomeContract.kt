package code.name.player.musicplayer.mvp.contract

import code.name.player.musicplayer.model.Home
import code.name.player.musicplayer.mvp.BasePresenter
import code.name.player.musicplayer.mvp.BaseView

interface HomeContract {

    interface HomeView : BaseView<ArrayList<Home>>

    interface HomePresenter : BasePresenter<HomeView> {

        fun homeSections();
    }
}