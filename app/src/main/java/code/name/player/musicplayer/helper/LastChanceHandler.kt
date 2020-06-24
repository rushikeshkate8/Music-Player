package code.name.player.musicplayer.helper

import android.content.Intent
import androidx.core.app.ActivityCompat
import code.name.player.musicplayer.App
import code.name.player.musicplayer.ui.activities.ErrorHandlerActivity

class TopExceptionHandler() : Thread.UncaughtExceptionHandler {
    private val defaultUEH: Thread.UncaughtExceptionHandler = Thread.getDefaultUncaughtExceptionHandler()

    override fun uncaughtException(t: Thread, e: Throwable) {
        var arr = e.stackTrace
        var report = e.toString() + "\n\n"
        report += "--------- Stack trace ---------\n\n"
        for (i in arr.indices) {
            report += "    " + arr[i].toString() + "\n"
        }
        report += "-------------------------------\n\n"

        // If the exception was thrown in a background thread inside
        // AsyncTask, then the actual exception can be found with getCause

        report += "--------- Cause ---------\n\n"
        val cause = e.cause
        if (cause != null) {
            report += cause.toString() + "\n\n"
            arr = cause.stackTrace
            for (i in arr.indices) {
                report += "    " + arr[i].toString() + "\n"
            }
        }
        report += "-------------------------------\n\n"
        ActivityCompat.startActivity(App.context, Intent(App.context, ErrorHandlerActivity::class.java)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                .putExtra("error", report), null)
        defaultUEH.uncaughtException(t, e)
    }
}