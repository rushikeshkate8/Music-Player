package code.name.player.musicplayer.ui.fragments.player.cardblur

import android.graphics.Color
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.preference.PreferenceManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.Toolbar
import code.name.player.appthemehelper.util.ToolbarContentTintHelper
import code.name.player.musicplayer.R
import code.name.player.musicplayer.glide.BlurTransformation
import code.name.player.musicplayer.glide.GlideApp
import code.name.player.musicplayer.glide.RetroGlideExtension
import code.name.player.musicplayer.glide.RetroMusicColoredTarget
import code.name.player.musicplayer.helper.MusicPlayerRemote
import code.name.player.musicplayer.model.Song
import code.name.player.musicplayer.ui.fragments.base.AbsPlayerFragment
import code.name.player.musicplayer.ui.fragments.player.PlayerAlbumCoverFragment
import code.name.player.musicplayer.ui.fragments.player.normal.PlayerFragment
import kotlinx.android.synthetic.main.fragment_card_blur_player.*

class CardBlurFragment : AbsPlayerFragment(), PlayerAlbumCoverFragment.Callbacks {
    override fun toolbarGet(): Toolbar {
        return playerToolbar
    }

    private var lastColor: Int = 0
    override val paletteColor: Int
        get() = lastColor
    private lateinit var playbackControlsFragment: CardBlurPlaybackControlsFragment


    override fun onShow() {
        playbackControlsFragment.show()
    }

    override fun onHide() {
        playbackControlsFragment.hide()
        onBackPressed()
    }

    override fun onBackPressed(): Boolean {
        return false
    }

    override fun toolbarIconColor(): Int {
        return Color.WHITE
    }

    override fun onColorChanged(color: Int) {
        playbackControlsFragment.setDark(color)
        lastColor = color
        callbacks!!.onPaletteColorChanged()
        ToolbarContentTintHelper.colorizeToolbar(playerToolbar, Color.WHITE, activity)

        playerToolbar.setTitleTextColor(Color.WHITE)
        playerToolbar.setSubtitleTextColor(Color.WHITE)
    }

    override fun toggleFavorite(song: Song) {
        super.toggleFavorite(song)
        if (song.id == MusicPlayerRemote.currentSong.id) {
            updateIsFavorite()
        }
    }

    override fun onFavoriteToggled() {
        toggleFavorite(MusicPlayerRemote.currentSong)
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        return inflater.inflate(R.layout.fragment_card_blur_player, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpSubFragments()
        setUpPlayerToolbar()
    }

    private fun setUpSubFragments() {
        playbackControlsFragment = childFragmentManager.findFragmentById(R.id.playbackControlsFragment) as CardBlurPlaybackControlsFragment
        val playerAlbumCoverFragment = childFragmentManager.findFragmentById(R.id.playerAlbumCoverFragment) as PlayerAlbumCoverFragment?
        if (playerAlbumCoverFragment != null) {
            playerAlbumCoverFragment.setCallbacks(this)
            playerAlbumCoverFragment.removeEffect()
        }

    }

    private fun setUpPlayerToolbar() {
        playerToolbar.apply {
            inflateMenu(R.menu.menu_player)
            setNavigationOnClickListener { activity!!.onBackPressed() }
            setTitleTextColor(Color.WHITE)
            setSubtitleTextColor(Color.WHITE)
            ToolbarContentTintHelper.colorizeToolbar(playerToolbar, Color.WHITE, activity)
        }.setOnMenuItemClickListener(this)
    }

    override fun onServiceConnected() {
        updateIsFavorite()
        updateBlur()
        updateSong()
    }

    override fun onPlayingMetaChanged() {
        updateIsFavorite()
        updateBlur()
        updateSong()
    }

    private fun updateSong() {
        val song = MusicPlayerRemote.currentSong
        playerToolbar.apply {
            title = song.title
            subtitle = song.artistName
        }
    }

    private fun updateBlur() {
        val activity = activity ?: return
        val blurAmount = PreferenceManager.getDefaultSharedPreferences(context)
                .getInt("new_blur_amount", 25)

        colorBackground!!.clearColorFilter()

        GlideApp.with(activity)
                .asBitmapPalette()
                .load(RetroGlideExtension.getSongModel(MusicPlayerRemote.currentSong))
                .transition(RetroGlideExtension.getDefaultTransition())
                .transform(BlurTransformation.Builder(activity).blurRadius(blurAmount.toFloat()).build())
                .songOptions(MusicPlayerRemote.currentSong)
                .override(320, 480)
                .into(object : RetroMusicColoredTarget(colorBackground) {
                    override fun onColorReady(color: Int) {
                        if (color == defaultFooterColor) {
                            colorBackground!!.setColorFilter(color)
                        }
                    }

                    override fun onLoadFailed(errorDrawable: Drawable?) {
                        super.onLoadFailed(errorDrawable)

                    }
                })

    }

    companion object {

        fun newInstance(): PlayerFragment {
            return PlayerFragment()
        }
    }
}
