package code.name.player.musicplayer.ui.fragments.settings

import android.os.Bundle

import code.name.player.musicplayer.R

/**
 * @author Hemanth S (h4h13).
 */

class ImageSettingFragment : AbsSettingsFragment() {
    override fun invalidateSettings() {
        val autoDownloadImagesPolicy = findPreference("auto_download_images_policy")
        setSummary(autoDownloadImagesPolicy)
        autoDownloadImagesPolicy.setOnPreferenceChangeListener { _, o ->
            setSummary(autoDownloadImagesPolicy, o)
            true
        }

    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        addPreferencesFromResource(R.xml.pref_images)
    }
}
