package code.name.player.musicplayer.mvp

import code.name.player.musicplayer.Injection
import code.name.player.musicplayer.providers.interfaces.Repository
import code.name.player.musicplayer.util.schedulers.BaseSchedulerProvider
import io.reactivex.disposables.CompositeDisposable


open class Presenter {
    protected var repository: Repository = Injection.provideRepository()
    protected var disposable: CompositeDisposable = CompositeDisposable()
    protected var schedulerProvider: BaseSchedulerProvider = Injection.provideSchedulerProvider()
}
