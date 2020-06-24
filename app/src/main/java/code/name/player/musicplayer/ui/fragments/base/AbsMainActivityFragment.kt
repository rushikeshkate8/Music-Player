package code.name.player.musicplayer.ui.fragments.base

import android.os.Build
import android.os.Bundle
import android.view.View

import code.name.player.appthemehelper.ThemeStore
import code.name.player.appthemehelper.util.ColorUtil
import code.name.player.appthemehelper.util.VersionUtils
import code.name.player.musicplayer.R
import code.name.player.musicplayer.dialogs.OptionsSheetDialogFragment
import code.name.player.musicplayer.ui.activities.MainActivity

abstract class AbsMainActivityFragment : AbsMusicServiceFragment() {

    val mainActivity: MainActivity
        get() = activity as MainActivity

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setHasOptionsMenu(true)

        mainActivity.setNavigationbarColorAuto()
        mainActivity.setLightNavigationBar(true)
        mainActivity.setTaskDescriptionColorAuto()
        mainActivity.hideStatusBar()
        mainActivity.setBottomBarVisibility(View.VISIBLE)
    }

    private fun setStatusbarColor(view: View, color: Int) {
        val statusBar = view.findViewById<View>(R.id.status_bar)
        if (statusBar != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                statusBar.setBackgroundColor(color)
                mainActivity.setLightStatusbarAuto(color)
            } else {
                statusBar.setBackgroundColor(color)
            }
        }
    }

    fun setStatusbarColorAuto(view: View) {
        // we don't want to use statusbar color because we are doing the color darkening on our own to support KitKat

        if (VersionUtils.hasMarshmallow()) {
            setStatusbarColor(view, ThemeStore.primaryColor(context!!))
        } else {
            setStatusbarColor(view, ColorUtil.darkenColor(ThemeStore.primaryColor(context!!)))
        }
    }

    protected fun showMainMenu() {
        OptionsSheetDialogFragment.newInstance().show(childFragmentManager, "Main Menu")
    }
}
