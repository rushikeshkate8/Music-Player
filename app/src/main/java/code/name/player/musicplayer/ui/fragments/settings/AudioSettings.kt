package code.name.player.musicplayer.ui.fragments.settings

import android.content.Intent
import android.media.audiofx.AudioEffect
import android.os.Bundle

import code.name.player.musicplayer.R
import code.name.player.musicplayer.util.NavigationUtil
import code.name.player.musicplayer.util.PreferenceUtil

/**
 * @author Hemanth S (h4h13).
 */

class AudioSettings : AbsSettingsFragment() {
    override fun invalidateSettings() {
        val findPreference = findPreference("equalizer")
        if (!hasEqualizer() && PreferenceUtil.getInstance().selectedEqualizer != "retro") {
            findPreference.isEnabled = false
            findPreference.summary = resources.getString(R.string.no_equalizer)
        } else {
            findPreference.isEnabled = true
        }
        findPreference.setOnPreferenceClickListener {
            NavigationUtil.openEqualizer(activity!!)
            true
        }


    }

    private fun hasEqualizer(): Boolean {
        val effects = Intent(AudioEffect.ACTION_DISPLAY_AUDIO_EFFECT_CONTROL_PANEL)

        val pm = activity!!.packageManager
        val ri = pm.resolveActivity(effects, 0)
        return ri != null
    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        addPreferencesFromResource(R.xml.pref_audio)
    }
}
