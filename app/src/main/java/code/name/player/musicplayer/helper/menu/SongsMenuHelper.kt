package code.name.player.musicplayer.helper.menu

import androidx.fragment.app.FragmentActivity
import code.name.player.musicplayer.R
import code.name.player.musicplayer.dialogs.AddToPlaylistDialog
import code.name.player.musicplayer.dialogs.DeleteSongsDialog
import code.name.player.musicplayer.helper.MusicPlayerRemote
import code.name.player.musicplayer.model.Song
import java.util.*


object SongsMenuHelper {
    fun handleMenuClick(activity: FragmentActivity, songs: ArrayList<Song>, menuItemId: Int): Boolean {
        when (menuItemId) {
            R.id.action_play_next -> {
                MusicPlayerRemote.playNext(songs)
                return true
            }
            R.id.action_add_to_current_playing -> {
                MusicPlayerRemote.enqueue(songs)
                return true
            }
            R.id.action_add_to_playlist -> {
                AddToPlaylistDialog.create(songs).show(activity.supportFragmentManager, "ADD_PLAYLIST")
                return true
            }
            R.id.action_delete_from_device -> {
                DeleteSongsDialog.create(songs).show(activity.supportFragmentManager, "DELETE_SONGS")
                return true
            }
        }
        return false
    }
}
