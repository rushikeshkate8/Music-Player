package code.name.player.musicplayer

import code.name.player.musicplayer.providers.RepositoryImpl
import code.name.player.musicplayer.providers.interfaces.Repository
import code.name.player.musicplayer.rest.KogouClient
import code.name.player.musicplayer.rest.service.KuGouApiService
import code.name.player.musicplayer.util.schedulers.BaseSchedulerProvider
import code.name.player.musicplayer.util.schedulers.SchedulerProvider

object Injection {

    fun provideRepository(): Repository {
        return RepositoryImpl.instance
    }

    fun provideSchedulerProvider(): BaseSchedulerProvider {
        return SchedulerProvider.getInstance()
    }

}
