package code.name.player.musicplayer.helper.menu

import android.content.Intent
import android.view.MenuItem
import android.view.View
import android.widget.PopupMenu
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentActivity
import code.name.player.musicplayer.R
import code.name.player.musicplayer.dialogs.AddToPlaylistDialog
import code.name.player.musicplayer.dialogs.DeleteSongsDialog
import code.name.player.musicplayer.dialogs.SongDetailDialog
import code.name.player.musicplayer.helper.MusicPlayerRemote
import code.name.player.musicplayer.interfaces.PaletteColorHolder
import code.name.player.musicplayer.model.Song
import code.name.player.musicplayer.ui.activities.tageditor.AbsTagEditorActivity
import code.name.player.musicplayer.ui.activities.tageditor.SongTagEditorActivity
import code.name.player.musicplayer.util.MusicUtil
import code.name.player.musicplayer.util.NavigationUtil


object SongMenuHelper {
    val MENU_RES = R.menu.menu_item_song

    fun handleMenuClick(activity: FragmentActivity, song: Song, menuItemId: Int): Boolean {
        when (menuItemId) {
            R.id.action_set_as_ringtone -> {
                MusicUtil.setRingtone(activity, song.id)
                return true
            }
            R.id.action_share -> {
                activity.startActivity(Intent.createChooser(MusicUtil.createShareSongFileIntent(song, activity), null))
                return true
            }
            R.id.action_delete_from_device -> {
                DeleteSongsDialog.create(song).show(activity.supportFragmentManager, "DELETE_SONGS")
                return true
            }
            R.id.action_add_to_playlist -> {
                AddToPlaylistDialog.create(song).show(activity.supportFragmentManager, "ADD_PLAYLIST")
                return true
            }
            R.id.action_play_next -> {
                MusicPlayerRemote.playNext(song)
                return true
            }
            R.id.action_add_to_current_playing -> {
                MusicPlayerRemote.enqueue(song)
                return true
            }
            R.id.action_tag_editor -> {
                val tagEditorIntent = Intent(activity, SongTagEditorActivity::class.java)
                tagEditorIntent.putExtra(AbsTagEditorActivity.EXTRA_ID, song.id)
                if (activity is PaletteColorHolder)
                    tagEditorIntent.putExtra(AbsTagEditorActivity.EXTRA_PALETTE, (activity as PaletteColorHolder).paletteColor)
                activity.startActivity(tagEditorIntent)
                return true
            }
            R.id.action_details -> {
                SongDetailDialog.create(song).show(activity.supportFragmentManager, "SONG_DETAILS")
                return true
            }
            R.id.action_go_to_album -> {
                NavigationUtil.goToAlbum(activity, song.albumId)
                return true
            }
            R.id.action_go_to_artist -> {
                NavigationUtil.goToArtist(activity, song.artistId)
                return true
            }
        }
        return false
    }

    abstract class OnClickSongMenu protected constructor(private val activity: AppCompatActivity) : View.OnClickListener, PopupMenu.OnMenuItemClickListener {

        open val menuRes: Int
            get() = MENU_RES

        abstract val song: Song

        override fun onClick(v: View) {
            val popupMenu = PopupMenu(activity, v)
            popupMenu.inflate(menuRes)
            popupMenu.setOnMenuItemClickListener(this)
            popupMenu.show()
        }

        override fun onMenuItemClick(item: MenuItem): Boolean {
            return handleMenuClick(activity, song, item.itemId)
        }
    }
}
