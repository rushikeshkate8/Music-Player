package code.name.player.musicplayer.helper.menu

import android.content.Intent
import android.net.Uri
import androidx.fragment.app.FragmentActivity
import code.name.player.musicplayer.R
import code.name.player.musicplayer.dialogs.AddToPlaylistDialog
import code.name.player.musicplayer.dialogs.DeleteSongsDialog
import code.name.player.musicplayer.helper.MusicPlayerRemote
import code.name.player.musicplayer.model.Song
import java.io.File
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
            R.id.share_multiple -> {
                val intent = Intent()
                intent.action = Intent.ACTION_SEND_MULTIPLE
                //intent.putExtra(Intent.EXTRA_SUBJECT, "Here are some files.")
                intent.type = "audio/*" /* This example is sharing jpeg images. */
                val files: ArrayList<Uri> = ArrayList<Uri>()
                for (song in songs)
                {
                    val file = File(song.data)
                    val sharePath = file.absolutePath
                    val uri = Uri.parse(sharePath)
                    files.add(uri)
                }
                intent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, files)
                activity.startActivity(intent)
                return true
            }
        }
        return false
    }
}


