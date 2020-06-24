package code.name.player.musicplayer.appshortcuts

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import code.name.player.musicplayer.Constants.ACTION_PLAY_PLAYLIST
import code.name.player.musicplayer.Constants.INTENT_EXTRA_PLAYLIST
import code.name.player.musicplayer.Constants.INTENT_EXTRA_SHUFFLE_MODE
import code.name.player.musicplayer.appshortcuts.shortcuttype.LastAddedShortcutType
import code.name.player.musicplayer.appshortcuts.shortcuttype.SearchShortCutType
import code.name.player.musicplayer.appshortcuts.shortcuttype.ShuffleAllShortcutType
import code.name.player.musicplayer.appshortcuts.shortcuttype.TopTracksShortcutType
import code.name.player.musicplayer.model.Playlist
import code.name.player.musicplayer.model.smartplaylist.LastAddedPlaylist
import code.name.player.musicplayer.model.smartplaylist.MyTopTracksPlaylist
import code.name.player.musicplayer.model.smartplaylist.ShuffleAllPlaylist
import code.name.player.musicplayer.service.MusicService
import code.name.player.musicplayer.ui.activities.SearchActivity


class AppShortcutLauncherActivity : Activity() {

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        var shortcutType = SHORTCUT_TYPE_NONE

        // Set shortcutType from the intent extras
        val extras = intent.extras
        if (extras != null) {
            shortcutType = extras.getInt(KEY_SHORTCUT_TYPE, SHORTCUT_TYPE_NONE)
        }

        when (shortcutType) {
            SHORTCUT_TYPE_SHUFFLE_ALL -> {
                startServiceWithPlaylist(MusicService.SHUFFLE_MODE_SHUFFLE,
                        ShuffleAllPlaylist(applicationContext))
                DynamicShortcutManager.reportShortcutUsed(this, ShuffleAllShortcutType.id)
            }
            SHORTCUT_TYPE_TOP_TRACKS -> {
                startServiceWithPlaylist(MusicService.SHUFFLE_MODE_NONE,
                        MyTopTracksPlaylist(applicationContext))
                DynamicShortcutManager.reportShortcutUsed(this, TopTracksShortcutType.id)
            }
            SHORTCUT_TYPE_LAST_ADDED -> {
                startServiceWithPlaylist(MusicService.SHUFFLE_MODE_NONE,
                        LastAddedPlaylist(applicationContext))
                DynamicShortcutManager.reportShortcutUsed(this, LastAddedShortcutType.id)
            }
            SHORTCUT_TYPE_SEARCH -> {
                startActivity(Intent(this, SearchActivity::class.java))
                DynamicShortcutManager.reportShortcutUsed(this, SearchShortCutType.id)
            }
        }
        finish()
    }

    private fun startServiceWithPlaylist(shuffleMode: Int, playlist: Playlist) {
        val intent = Intent(this, MusicService::class.java)
        intent.action = ACTION_PLAY_PLAYLIST

        val bundle = Bundle()
        bundle.putParcelable(INTENT_EXTRA_PLAYLIST, playlist)
        bundle.putInt(INTENT_EXTRA_SHUFFLE_MODE, shuffleMode)

        intent.putExtras(bundle)

        startService(intent)
    }

    companion object {
        const val KEY_SHORTCUT_TYPE = "code.name.player.musicplayer.appshortcuts.ShortcutType"
        const val SHORTCUT_TYPE_SHUFFLE_ALL = 0
        const val SHORTCUT_TYPE_TOP_TRACKS = 1
        const val SHORTCUT_TYPE_LAST_ADDED = 2
        const val SHORTCUT_TYPE_SEARCH = 3
        const val SHORTCUT_TYPE_NONE = 4
    }
}
