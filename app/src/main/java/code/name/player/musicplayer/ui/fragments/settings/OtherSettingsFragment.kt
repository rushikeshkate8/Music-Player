package code.name.player.musicplayer.ui.fragments.settings

import android.os.Bundle

import code.name.player.musicplayer.R

/**
 * @author Hemanth S (h4h13).
 */

class OtherSettingsFragment : AbsSettingsFragment() {
    override fun invalidateSettings() {

    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        addPreferencesFromResource(R.xml.pref_blacklist)
        addPreferencesFromResource(R.xml.pref_playlists)
        addPreferencesFromResource(R.xml.pref_advanced)
    }
}
