package code.name.player.musicplayer.ui.activities.base

import android.os.Bundle
import code.name.player.appthemehelper.ATHActivity
import code.name.player.musicplayer.helper.TopExceptionHandler

abstract class AbsCrashCollector : ATHActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Thread.setDefaultUncaughtExceptionHandler(TopExceptionHandler())
    }
}