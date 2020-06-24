package code.name.player.musicplayer.helper.menu

import android.app.Activity
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.view.MenuItem
import android.widget.Toast

import java.util.ArrayList

import code.name.player.musicplayer.R
import code.name.player.musicplayer.App
import code.name.player.musicplayer.dialogs.AddToPlaylistDialog
import code.name.player.musicplayer.dialogs.DeletePlaylistDialog
import code.name.player.musicplayer.dialogs.RenamePlaylistDialog
import code.name.player.musicplayer.helper.MusicPlayerRemote
import code.name.player.musicplayer.loaders.PlaylistSongsLoader
import code.name.player.musicplayer.misc.WeakContextAsyncTask
import code.name.player.musicplayer.model.AbsCustomPlaylist
import code.name.player.musicplayer.model.Playlist
import code.name.player.musicplayer.model.Song
import code.name.player.musicplayer.util.PlaylistsUtil


object PlaylistMenuHelper {

    fun handleMenuClick(activity: AppCompatActivity,
                        playlist: Playlist, item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_play -> {
                MusicPlayerRemote.openQueue(getPlaylistSongs(activity, playlist), 9, true)
                return true
            }
            R.id.action_play_next -> {
                MusicPlayerRemote.playNext(getPlaylistSongs(activity, playlist))
                return true
            }
            R.id.action_add_to_playlist -> {
                AddToPlaylistDialog.create(getPlaylistSongs(activity, playlist))
                        .show(activity.supportFragmentManager, "ADD_PLAYLIST")
                return true
            }
            R.id.action_add_to_current_playing -> {
                MusicPlayerRemote.enqueue(getPlaylistSongs(activity, playlist))
                return true
            }
            R.id.action_rename_playlist -> {
                RenamePlaylistDialog.create(playlist.id.toLong())
                        .show(activity.supportFragmentManager, "RENAME_PLAYLIST")
                return true
            }
            R.id.action_delete_playlist -> {
                DeletePlaylistDialog.create(playlist)
                        .show(activity.supportFragmentManager, "DELETE_PLAYLIST")
                return true
            }
            R.id.action_save_playlist -> {
                SavePlaylistAsyncTask(activity).execute(playlist)
                return true
            }
        }
        return false
    }

    private fun getPlaylistSongs(activity: Activity,
                                 playlist: Playlist): ArrayList<Song> {
        val songs: ArrayList<Song>
        if (playlist is AbsCustomPlaylist) {
            songs = playlist.getSongs(activity).blockingFirst()
        } else {
            songs = PlaylistSongsLoader.getPlaylistSongList(activity, playlist).blockingFirst()
        }
        return songs
    }

    private class SavePlaylistAsyncTask internal constructor(context: Context) : WeakContextAsyncTask<Playlist, String, String>(context) {

        override fun doInBackground(vararg params: Playlist): String {
            return String.format(App.instance.applicationContext.getString(R.string
                    .saved_playlist_to), PlaylistsUtil.savePlaylist(App.instance.applicationContext, params[0]).blockingFirst())
        }

        override fun onPostExecute(string: String) {
            super.onPostExecute(string)
            val context = context
            if (context != null) {
                Toast.makeText(context, string, Toast.LENGTH_LONG).show()
            }
        }
    }
}
