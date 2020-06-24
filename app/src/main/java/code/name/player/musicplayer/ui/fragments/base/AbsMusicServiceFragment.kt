package code.name.player.musicplayer.ui.fragments.base

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import code.name.player.musicplayer.interfaces.MusicServiceEventListener
import code.name.player.musicplayer.ui.activities.base.AbsMusicServiceActivity

/**
 * Created by hemanths on 18/08/17.
 */

open class AbsMusicServiceFragment : Fragment(), MusicServiceEventListener {

    var playerActivity: AbsMusicServiceActivity? = null
        private set

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        try {
            playerActivity = context as AbsMusicServiceActivity?
        } catch (e: ClassCastException) {
            throw RuntimeException(context!!.javaClass.simpleName + " must be an instance of " + AbsMusicServiceActivity::class.java.simpleName)
        }

    }

    override fun onDetach() {
        super.onDetach()
        playerActivity = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        playerActivity!!.addMusicServiceEventListener(this)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        playerActivity!!.removeMusicServiceEventListener(this)
    }

    override fun onPlayingMetaChanged() {

    }

    override fun onServiceConnected() {

    }

    override fun onServiceDisconnected() {

    }

    override fun onQueueChanged() {

    }

    override fun onPlayStateChanged() {

    }

    override fun onRepeatModeChanged() {

    }

    override fun onShuffleModeChanged() {

    }

    override fun onMediaStoreChanged() {

    }
}
