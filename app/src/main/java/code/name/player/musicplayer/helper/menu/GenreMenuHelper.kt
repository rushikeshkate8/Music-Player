package code.name.player.musicplayer.helper.menu

import android.app.Activity
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import code.name.player.musicplayer.R
import code.name.player.musicplayer.dialogs.AddToPlaylistDialog
import code.name.player.musicplayer.helper.MusicPlayerRemote
import code.name.player.musicplayer.loaders.GenreLoader
import code.name.player.musicplayer.model.Genre
import code.name.player.musicplayer.model.Song
import java.util.*

/**
 * @author Hemanth S (h4h13).
 */

object GenreMenuHelper {
    fun handleMenuClick(activity: AppCompatActivity, genre: Genre, item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_play -> {
                MusicPlayerRemote.openQueue(getGenreSongs(activity, genre), 0, true)
                return true
            }
            R.id.action_play_next -> {
                MusicPlayerRemote.playNext(getGenreSongs(activity, genre))
                return true
            }
            R.id.action_add_to_playlist -> {
                AddToPlaylistDialog.create(getGenreSongs(activity, genre))
                        .show(activity.supportFragmentManager, "ADD_PLAYLIST")
                return true
            }
            R.id.action_add_to_current_playing -> {
                MusicPlayerRemote.enqueue(getGenreSongs(activity, genre))
                return true
            }
        }
        return false
    }

    private fun getGenreSongs(activity: Activity, genre: Genre): ArrayList<Song> {
        return GenreLoader.getSongs(activity, genre.id).blockingFirst()
    }
}
