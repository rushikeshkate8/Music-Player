package code.name.player.musicplayer.ui.activities

import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.view.MenuItem
import android.view.View
import android.widget.LinearLayout
import android.widget.Toast
import androidx.annotation.ColorInt
import androidx.annotation.StringRes
import androidx.fragment.app.Fragment
import code.name.player.appthemehelper.ThemeStore
import code.name.player.appthemehelper.util.ColorUtil
import code.name.player.appthemehelper.util.ToolbarContentTintHelper
import code.name.player.appthemehelper.util.VersionUtils
import code.name.player.musicplayer.R
import code.name.player.musicplayer.appshortcuts.DynamicShortcutManager
import code.name.player.musicplayer.ui.activities.base.AbsBaseActivity
import code.name.player.musicplayer.ui.fragments.settings.MainSettingsFragment
import code.name.player.musicplayer.util.PreferenceUtil
import com.afollestad.materialdialogs.color.ColorChooserDialog
import com.facebook.ads.AdSize
import com.facebook.ads.AdView
import com.facebook.ads.AudienceNetworkAds
import com.facebook.ads.InterstitialAd
import kotlinx.android.synthetic.main.activity_settings.*


class SettingsActivity : AbsBaseActivity(), ColorChooserDialog.ColorCallback, SharedPreferences.OnSharedPreferenceChangeListener {
    private val fragmentManager = supportFragmentManager
    private val TAG: String = code.name.player.musicplayer.ui.activities.SettingsActivity::class.java.getSimpleName()
    private var interstitialAd: InterstitialAd? = null
    private var adView: AdView? = null
    override fun onColorSelection(dialog: ColorChooserDialog, @ColorInt selectedColor: Int) {
        when (dialog.title) {
            R.string.primary_color -> {
                val theme = if (ColorUtil.isColorLight(selectedColor))
                    PreferenceUtil.getThemeResFromPrefValue("light")
                else
                    PreferenceUtil.getThemeResFromPrefValue("dark")

                ThemeStore.editTheme(this).activityTheme(theme).primaryColor(selectedColor).commit()
            }
            R.string.accent_color -> ThemeStore.editTheme(this).accentColor(selectedColor).commit()
        }
        if (VersionUtils.hasNougatMR())
            DynamicShortcutManager(this).updateDynamicShortcuts()
        recreate()
    }

    override fun onColorChooserDismissed(dialog: ColorChooserDialog) {

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        setStatusbarColorAuto()
        setNavigationbarColorAuto()
        AudienceNetworkAds.initialize(this)
        adView = AdView(this, "266586284404690_267119051018080", AdSize.BANNER_HEIGHT_50)
        val adContainer = findViewById<View>(R.id.settingActivityBanner) as LinearLayout
        adContainer.addView(adView)
        adView!!.loadAd()
        interstitialAd = InterstitialAd(this, "266586284404690_267321077664544")
        Handler().postDelayed({
            interstitialAd!!.loadAd()
        }, 10000)
        setLightNavigationBar(true)

        setupToolbar()

        if (savedInstanceState == null) {
            fragmentManager.beginTransaction().replace(R.id.contentFrame, MainSettingsFragment())
                    .commit()
        }
    }

    private fun setupToolbar() {
        setSupportActionBar(toolbar)
        title = null
        toolbar.apply {
            setBackgroundColor(ThemeStore.primaryColor(context))
            setNavigationOnClickListener { onBackPressed() }
            ToolbarContentTintHelper.colorBackButton(toolbar, ThemeStore.textColorSecondary(context))
        }
        appBarLayout.setBackgroundColor(ThemeStore.primaryColor(this))
        settingsTitle.setTextColor(ThemeStore.textColorPrimary(this))
    }

    fun setupFragment(fragment: Fragment, @StringRes titleName: Int) {
        val fragmentTransaction = fragmentManager
                .beginTransaction()
                .setCustomAnimations(R.anim.sliding_in_left, R.anim.sliding_out_right, android.R.anim.slide_in_left, android.R.anim.slide_out_right)

        settingsTitle.setText(titleName)

        if (detailContentFrame == null) {
            fragmentTransaction.replace(R.id.contentFrame, fragment, fragment.tag)
            fragmentTransaction.addToBackStack(null)
            fragmentTransaction.commit()
        } else {
            fragmentTransaction.replace(R.id.detailContentFrame, fragment, fragment.tag)
            fragmentTransaction.commit()
        }
    }

    override fun onBackPressed() {
        if (fragmentManager.backStackEntryCount == 0) {
            super.onBackPressed()
        } else {
            settingsTitle.setText(R.string.action_settings)
            fragmentManager.popBackStack()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            onBackPressed()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    public override fun onPause() {
        super.onPause()
        PreferenceUtil.getInstance().unregisterOnSharedPreferenceChangedListener(this)
    }

    public override fun onResume() {
        super.onResume()
        PreferenceUtil.getInstance().registerOnSharedPreferenceChangedListener(this)
    }
    override fun onDestroy() {
        if(interstitialAd?.isAdLoaded!!)
        {
            interstitialAd?.show()
        }
        if (adView != null) {
            adView!!.destroy()
        }
        interstitialAd?.destroy()
        super.onDestroy()
    }
    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences, key: String) {
        if (key == PreferenceUtil.PROFILE_IMAGE_PATH) {
            recreate()
        }
    }


    companion object {
        const val TAG: String = "SettingsActivity"
    }
}
