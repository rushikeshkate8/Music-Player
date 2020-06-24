package code.name.player.musicplayer.ui.fragments.player.color

import android.animation.ArgbEvaluator
import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.drawable.Drawable
import android.os.AsyncTask
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.Toolbar
import code.name.player.appthemehelper.util.ATHUtil
import code.name.player.appthemehelper.util.ColorUtil
import code.name.player.appthemehelper.util.MaterialValueHelper
import code.name.player.appthemehelper.util.ToolbarContentTintHelper
import code.name.player.musicplayer.R
import code.name.player.musicplayer.glide.GlideApp
import code.name.player.musicplayer.glide.RetroGlideExtension
import code.name.player.musicplayer.glide.RetroMusicColoredTarget
import code.name.player.musicplayer.glide.palette.BitmapPaletteWrapper
import code.name.player.musicplayer.helper.MusicPlayerRemote
import code.name.player.musicplayer.model.Song
import code.name.player.musicplayer.model.lyrics.Lyrics
import code.name.player.musicplayer.ui.activities.LyricsActivity
import code.name.player.musicplayer.ui.fragments.base.AbsPlayerFragment
import code.name.player.musicplayer.util.MusicUtil
import code.name.player.musicplayer.util.RetroColorUtil
import code.name.player.musicplayer.util.ViewUtil
import com.bumptech.glide.request.transition.Transition
import kotlinx.android.synthetic.main.fragment_color_player.*

class ColorFragment : AbsPlayerFragment() {
    override fun toolbarGet(): Toolbar {
        return playerToolbar
    }

    override val paletteColor: Int
        get() = backgroundColor

    override fun onColorChanged(color: Int) {

    }

    override fun onFavoriteToggled() {

    }


    private var lastColor: Int = 0
    private var backgroundColor: Int = 0

    private var playbackControlsFragment: ColorPlaybackControlsFragment? = null

    private var valueAnimator: ValueAnimator? = null
    private var updateLyricsAsyncTask: AsyncTask<*, *, *>? = null
    private var lyrics: Lyrics? = null

    override fun onShow() {
        playbackControlsFragment!!.show()
    }

    override fun onHide() {
        playbackControlsFragment!!.hide()
        onBackPressed()
    }

    override fun onBackPressed(): Boolean {
        return false
    }

    override fun toolbarIconColor(): Int {
        return lastColor
    }

    override fun toggleFavorite(song: Song) {
        super.toggleFavorite(song)
        if (song.id == MusicPlayerRemote.currentSong.id) {
            updateIsFavorite()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        if (valueAnimator != null) {
            valueAnimator!!.cancel()
            valueAnimator = null
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        return inflater.inflate(R.layout.fragment_color_player, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpSubFragments()
        setUpPlayerToolbar()
        setupViews()
    }

    private fun setUpSubFragments() {
        playbackControlsFragment = childFragmentManager.findFragmentById(R.id.playbackControlsFragment) as ColorPlaybackControlsFragment?

    }

    private fun setUpPlayerToolbar() {
        playerToolbar.apply {
            inflateMenu(R.menu.menu_player)
            setNavigationOnClickListener { activity!!.onBackPressed() }
            setOnMenuItemClickListener(this@ColorFragment)
            ToolbarContentTintHelper.colorizeToolbar(this, ATHUtil.resolveColor(context, R.attr.iconColor), activity)
        }
    }

    override fun onPlayingMetaChanged() {
        super.onPlayingMetaChanged()
        updateSong()
        updateLyricsLocal()
    }

    override fun onServiceConnected() {
        super.onServiceConnected()
        updateSong()
        updateLyricsLocal()
    }

    private fun updateSong() {

        GlideApp.with(activity!!).asBitmapPalette()
                .load(RetroGlideExtension.getSongModel(MusicPlayerRemote.currentSong))
                .songOptions(MusicPlayerRemote.currentSong)
                .transition(RetroGlideExtension.getDefaultTransition())
                .into(object : RetroMusicColoredTarget(playerImage) {
                    override fun onColorReady(color: Int) {

                    }

                    override fun onResourceReady(resource: BitmapPaletteWrapper, glideAnimation: Transition<in BitmapPaletteWrapper>?) {
                        super.onResourceReady(resource, glideAnimation)
                        val palette = resource.palette
                        val swatch = RetroColorUtil.getSwatch(palette)

                        val textColor = RetroColorUtil.getTextColor(palette)
                        val backgroundColor = swatch.rgb

                        setColors(backgroundColor, textColor)
                    }

                    override fun onLoadFailed(errorDrawable: Drawable?) {
                        super.onLoadFailed(errorDrawable)
                        val backgroundColor = defaultFooterColor
                        val textColor = if (ColorUtil.isColorLight(defaultFooterColor))
                            MaterialValueHelper.getPrimaryTextColor(context, true)
                        else
                            MaterialValueHelper.getPrimaryTextColor(context, false)

                        setColors(backgroundColor, textColor)
                    }
                })

        /*SongGlideRequest.Builder.from(Glide.with(activity), MusicPlayerRemote.currentSong)
                .checkIgnoreMediaStore(activity!!)
                .generatePalette(activity).build().dontAnimate()
                .into(object : RetroMusicColoredTarget(playerImage) {
                    override fun onColorReady(color: Int) {
                        //setColors(color);
                    }

                    override fun onLoadFailed(e: Exception?, errorDrawable: Drawable?) {
                        super.onLoadFailed(e, errorDrawable)

                        val backgroundColor = defaultFooterColor
                        val textColor = if (ColorUtil.isColorLight(defaultFooterColor))
                            MaterialValueHelper.getPrimaryTextColor(context, true)
                        else
                            MaterialValueHelper.getPrimaryTextColor(context, false)

                        setColors(backgroundColor, textColor)
                    }

                    override fun onResourceReady(resource: BitmapPaletteWrapper,
                                                 glideAnimation: GlideAnimation<in BitmapPaletteWrapper>?) {
                        super.onResourceReady(resource, glideAnimation)
                        *//* MediaNotificationProcessor processor = new MediaNotificationProcessor(getContext(),
                getContext());
            Palette.Builder builder = MediaNotificationProcessor
                .generatePalette(resource.getBitmap());

            int backgroundColor = processor.getBackgroundColor(builder);
            int textColor = processor.getTextColor(builder);*//*

                        val palette = resource.palette
                        val swatch = RetroColorUtil.getSwatch(palette)

                        val textColor = RetroColorUtil.getTextColor(palette)
                        val backgroundColor = swatch.rgb

                        setColors(backgroundColor, textColor)
                    }
                })*/
    }

    private fun setColors(backgroundColor: Int, textColor: Int) {
        playbackControlsFragment!!.setDark(textColor, backgroundColor)

        colorGradientBackground?.setBackgroundColor(backgroundColor)

        ToolbarContentTintHelper.colorizeToolbar(playerToolbar, textColor, activity)

        lastColor = textColor

        this.backgroundColor = backgroundColor

        if (playerActivity != null) {
            playerActivity!!.setLightNavigationBar(ColorUtil.isColorLight(backgroundColor))
        }
        callbacks!!.onPaletteColorChanged()

    }

    private fun colorize(i: Int) {
        if (valueAnimator != null) {
            valueAnimator!!.cancel()
        }

        valueAnimator = ValueAnimator.ofObject(ArgbEvaluator(), paletteColor, i)
        valueAnimator!!.addUpdateListener { animation ->
            colorGradientBackground?.setBackgroundColor(animation.animatedValue as Int)
        }
        valueAnimator!!.setDuration(ViewUtil.RETRO_MUSIC_ANIM_TIME.toLong()).start()
    }

    @SuppressLint("StaticFieldLeak")
    private fun updateLyricsLocal() {
        if (updateLyricsAsyncTask != null) {
            updateLyricsAsyncTask!!.cancel(false)
        }
        val song = MusicPlayerRemote.currentSong
        updateLyricsAsyncTask = object : AsyncTask<Void?, Void?, Lyrics?>() {
            override fun onPreExecute() {
                super.onPreExecute()
                lyrics = null
                playerToolbar.menu.removeItem(R.id.action_show_lyrics)
            }

            override fun doInBackground(vararg params: Void?): Lyrics? {
                val data = MusicUtil.getLyrics(song)
                return if (TextUtils.isEmpty(data)) {
                    null
                } else Lyrics.parse(song, data)
            }

            override fun onPostExecute(l: Lyrics?) {
                lyrics = l
                if (lyrics == null) {
                    lyricsView.setText(R.string.no_lyrics_found)
                } else {
                    lyricsView.text = lyrics!!.text
                }
            }

            override fun onCancelled(s: Lyrics?) {
                onPostExecute(null)
            }
        }.execute()
    }

    private fun setupViews() {
        lyricsView.setOnClickListener {
            if (lyricsContainer!!.visibility == View.GONE) {
                lyricsContainer!!.visibility = View.VISIBLE
            } else {
                lyricsContainer!!.visibility = View.GONE
            }
        }
        playerImage.setOnClickListener {
            if (lyricsContainer!!.visibility == View.GONE) {
                lyricsContainer!!.visibility = View.VISIBLE
            } else {
                lyricsContainer!!.visibility = View.GONE
            }
        }
        expand.setOnClickListener { startActivity(Intent(context, LyricsActivity::class.java)) }
    }

    companion object {

        fun newInstance(): ColorFragment {
            val args = Bundle()
            val fragment = ColorFragment()
            fragment.arguments = args
            return fragment
        }
    }
}
