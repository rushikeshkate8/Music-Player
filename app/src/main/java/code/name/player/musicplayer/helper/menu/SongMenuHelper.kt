package code.name.player.musicplayer.helper.menu

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Handler
import android.provider.Settings
import android.view.MenuItem
import android.view.View
import android.widget.PopupMenu
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentActivity
import code.name.monkey.retromusic.util.RingtoneManager
import code.name.player.musicplayer.App.Companion.context
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
import com.afollestad.materialdialogs.MaterialDialog
import com.facebook.ads.Ad
import com.facebook.ads.AdError
import com.facebook.ads.InterstitialAd
import com.facebook.ads.InterstitialAdListener


object SongMenuHelper {
    private var wasPlaying = false
    val MENU_RES = R.menu.menu_item_song
    @RequiresApi(Build.VERSION_CODES.M)
    fun handleMenuClick(activity: FragmentActivity, song: Song, menuItemId: Int): Boolean {
        when (menuItemId) {
            R.id.action_set_as_ringtone -> {
                val settingsCanWrite = Settings.System.canWrite(activity)
                if (!settingsCanWrite)
                    {
                        MaterialDialog.Builder(activity)
                                .title(activity.getString(R.string.set_ringtone))
                                .content(activity.getString(R.string.set_ringtone_allow_permission_messege))
                                .positiveText(android.R.string.ok)
                                .onPositive { dialog, which ->
                                    val intent = Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS)
                                    intent.data = Uri.parse("package:" + activity.applicationContext.packageName)
                                    activity.startActivity(intent)
                                }
                                .negativeText(android.R.string.cancel)
                                .onNegative { dialog, which ->  }
                                .show()
                    }
                else
                    {
                        var interstitialAd: InterstitialAd? = null
                        interstitialAd = InterstitialAd(activity, "266586284404690_268951104168208")
                        interstitialAd!!.loadAd()
                        interstitialAd!!.setAdListener(object : InterstitialAdListener {
                            override fun onInterstitialDisplayed(ad: Ad?) {
                                // Interstitial ad displayed callback
                            }
                            override fun onInterstitialDismissed(ad: Ad?) {
                                // Interstitial dismissed callback
                                if (wasPlaying) {
                                    MusicPlayerRemote.resumePlaying()
                                    wasPlaying = false
                                }
                            }

                            override fun onError(ad: Ad?, adError: AdError) {
                                // Ad error callback
                            }

                            override fun onAdLoaded(ad: Ad?) {
                                // Interstitial ad is loaded and ready to be displayed
                                // Show the ad
                            }

                            override fun onAdClicked(ad: Ad?) {
                                // Ad clicked callback
                            }

                            override fun onLoggingImpression(ad: Ad?) {
                                // Ad impression logged callback
                                //if (!MusicPlayerRemote.isPlaying) {
                                //MusicPlayerRemote.resumePlaying()
                                //  }
                            }
                        })
                        val ringtoneManager = RingtoneManager(activity)
                        ringtoneManager.setRingtone(song)
                        val handler = Handler()
                        handler.postDelayed(Runnable { // Check if interstitialAd has been loaded successfully
                            if(interstitialAd.isAdLoaded)
                            {
                                if(MusicPlayerRemote.isPlaying)
                                {
                                    wasPlaying = true
                                    MusicPlayerRemote.pauseSong()
                                }
                                interstitialAd.show()
                            }
                        }, 6000)
                    }
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

        @RequiresApi(Build.VERSION_CODES.M)
        override fun onMenuItemClick(item: MenuItem): Boolean {
            return handleMenuClick(activity, song, item.itemId)
        }
    }
}
