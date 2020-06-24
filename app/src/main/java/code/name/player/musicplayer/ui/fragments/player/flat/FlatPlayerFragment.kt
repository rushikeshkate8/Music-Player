package code.name.player.musicplayer.ui.fragments.player.flat

import android.animation.ArgbEvaluator
import android.animation.ValueAnimator
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.Toolbar
import code.name.player.appthemehelper.util.ATHUtil
import code.name.player.appthemehelper.util.ColorUtil
import code.name.player.appthemehelper.util.MaterialValueHelper
import code.name.player.appthemehelper.util.ToolbarContentTintHelper
import code.name.player.musicplayer.R
import code.name.player.musicplayer.helper.MusicPlayerRemote
import code.name.player.musicplayer.model.Song
import code.name.player.musicplayer.ui.fragments.base.AbsPlayerFragment
import code.name.player.musicplayer.ui.fragments.player.PlayerAlbumCoverFragment
import code.name.player.musicplayer.util.PreferenceUtil
import code.name.player.musicplayer.util.ViewUtil
import code.name.player.musicplayer.views.DrawableGradient
import kotlinx.android.synthetic.main.fragment_flat_player.*

class FlatPlayerFragment : AbsPlayerFragment(), PlayerAlbumCoverFragment.Callbacks {
    override fun toolbarGet(): Toolbar {
        return playerToolbar
    }

    private var valueAnimator: ValueAnimator? = null
    private lateinit var flatPlaybackControlsFragment: FlatPlaybackControlsFragment
    private var lastColor: Int = 0
    override val paletteColor: Int
        get() = lastColor

    private fun setUpSubFragments() {
        flatPlaybackControlsFragment = childFragmentManager.findFragmentById(R.id.playbackControlsFragment) as FlatPlaybackControlsFragment
        val playerAlbumCoverFragment = childFragmentManager.findFragmentById(R.id.playerAlbumCoverFragment) as PlayerAlbumCoverFragment
        playerAlbumCoverFragment.setCallbacks(this)
    }

    private fun setUpPlayerToolbar() {
        playerToolbar.inflateMenu(R.menu.menu_player)
        playerToolbar.setNavigationOnClickListener { _ -> activity!!.onBackPressed() }
        playerToolbar.setOnMenuItemClickListener(this)
        ToolbarContentTintHelper.colorizeToolbar(playerToolbar, ATHUtil.resolveColor(context,
                R.attr.iconColor), activity)
    }

    private fun colorize(i: Int) {
        if (valueAnimator != null) {
            valueAnimator!!.cancel()
        }

        valueAnimator = ValueAnimator.ofObject(ArgbEvaluator(), android.R.color.transparent, i)
        valueAnimator!!.addUpdateListener { animation ->
            val drawable = DrawableGradient(GradientDrawable.Orientation.TOP_BOTTOM,
                    intArrayOf(animation.animatedValue as Int, android.R.color.transparent), 0)
            colorGradientBackground?.background = drawable

        }
        valueAnimator!!.setDuration(ViewUtil.RETRO_MUSIC_ANIM_TIME.toLong()).start()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_flat_player, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpPlayerToolbar()
        setUpSubFragments()

    }

    override fun onShow() {
        flatPlaybackControlsFragment.show()
    }

    override fun onHide() {
        flatPlaybackControlsFragment.hide()
        onBackPressed()
    }

    override fun onBackPressed(): Boolean {
        return false
    }

    override fun toolbarIconColor(): Int {
        val isLight = ColorUtil.isColorLight(paletteColor)
        return if (PreferenceUtil.getInstance().adaptiveColor)
            MaterialValueHelper.getPrimaryTextColor(context, isLight)
        else
            ATHUtil.resolveColor(context, R.attr.iconColor)
    }

    override fun onColorChanged(color: Int) {
        lastColor = color
        flatPlaybackControlsFragment.setDark(color)
        callbacks!!.onPaletteColorChanged()

        val isLight = ColorUtil.isColorLight(color)

        //TransitionManager.beginDelayedTransition(mToolbar);
        val iconColor = if (PreferenceUtil.getInstance().adaptiveColor)
            MaterialValueHelper.getPrimaryTextColor(context!!, isLight)
        else
            ATHUtil.resolveColor(context!!, R.attr.iconColor)
        ToolbarContentTintHelper.colorizeToolbar(playerToolbar, iconColor, activity)
        if (PreferenceUtil.getInstance().adaptiveColor) {
            colorize(color)
        }
    }


    override fun onFavoriteToggled() {
        toggleFavorite(MusicPlayerRemote.currentSong)
    }


    override fun toggleFavorite(song: Song) {
        super.toggleFavorite(song)
        if (song.id == MusicPlayerRemote.currentSong.id) {
            updateIsFavorite()
        }
    }
}
