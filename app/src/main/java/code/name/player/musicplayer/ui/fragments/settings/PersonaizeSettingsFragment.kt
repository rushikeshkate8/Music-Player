package code.name.player.musicplayer.ui.fragments.settings

import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import androidx.preference.TwoStatePreference
import code.name.player.musicplayer.App
import code.name.player.musicplayer.R
import code.name.player.musicplayer.util.PreferenceUtil


class PersonaizeSettingsFragment : AbsSettingsFragment(), SharedPreferences.OnSharedPreferenceChangeListener {

    override fun invalidateSettings() {
        val cornerWindow = findPreference("corner_window") as TwoStatePreference
        cornerWindow.setOnPreferenceChangeListener { _, newValue ->
            if (newValue as Boolean && App.isProVersion) { /*App.isProVersion*/
                showProToastAndNavigate(activity!!.getString(R.string.pref_title_round_corners))
                return@setOnPreferenceChangeListener false
            }
            activity!!.recreate()
            return@setOnPreferenceChangeListener true
        }


        val toggleFullScreen = findPreference("toggle_full_screen") as TwoStatePreference
        toggleFullScreen.setOnPreferenceChangeListener { _, _ ->
            activity!!.recreate()
            true
        }


    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        addPreferencesFromResource(R.xml.pref_ui)
        addPreferencesFromResource(R.xml.pref_window)
        addPreferencesFromResource(R.xml.pref_lockscreen)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        PreferenceUtil.getInstance().registerOnSharedPreferenceChangedListener(this)
    }

    override fun onDestroyView() {
        super.onDestroyView()

        PreferenceUtil.getInstance().unregisterOnSharedPreferenceChangedListener(this)
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences, key: String) {
        when (key) {
            PreferenceUtil.CAROUSEL_EFFECT -> invalidateSettings()
        }
    }

}
