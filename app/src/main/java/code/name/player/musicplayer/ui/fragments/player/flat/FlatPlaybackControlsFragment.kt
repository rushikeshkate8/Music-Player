package code.name.player.musicplayer.ui.fragments.player.flat

import android.animation.ObjectAnimator
import android.graphics.PorterDuff
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.DecelerateInterpolator
import android.view.animation.LinearInterpolator
import android.widget.SeekBar
import code.name.player.appthemehelper.ThemeStore
import code.name.player.appthemehelper.util.ATHUtil
import code.name.player.appthemehelper.util.ColorUtil
import code.name.player.appthemehelper.util.MaterialValueHelper
import code.name.player.appthemehelper.util.TintHelper
import code.name.player.musicplayer.R
import code.name.player.musicplayer.helper.MusicPlayerRemote
import code.name.player.musicplayer.helper.MusicProgressViewUpdateHelper
import code.name.player.musicplayer.helper.MusicProgressViewUpdateHelper.Callback
import code.name.player.musicplayer.helper.PlayPauseButtonOnClickHandler
import code.name.player.musicplayer.misc.SimpleOnSeekbarChangeListener
import code.name.player.musicplayer.service.MusicService
import code.name.player.musicplayer.ui.fragments.base.AbsPlayerControlsFragment
import code.name.player.musicplayer.util.MusicUtil
import code.name.player.musicplayer.util.PreferenceUtil
import code.name.player.musicplayer.util.ViewUtil
import kotlinx.android.synthetic.main.fragment_flat_player_playback_controls.*
import kotlinx.android.synthetic.main.player_time.*
import kotlinx.android.synthetic.main.volume_controls.*

class FlatPlaybackControlsFragment : AbsPlayerControlsFragment(), Callback {

    private var lastPlaybackControlsColor: Int = 0
    private var lastDisabledPlaybackControlsColor: Int = 0
    private var progressViewUpdateHelper: MusicProgressViewUpdateHelper? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        progressViewUpdateHelper = MusicProgressViewUpdateHelper(this)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_flat_player_playback_controls, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpMusicControllers()
        hideVolumeIfAvailable()
    }

    override fun onResume() {
        super.onResume()
        progressViewUpdateHelper!!.start()
    }

    override fun onPause() {
        super.onPause()
        progressViewUpdateHelper!!.stop()
    }

    override fun onUpdateProgressViews(progress: Int, total: Int) {
        progressSlider.max = total

        val animator = ObjectAnimator.ofInt(progressSlider, "progress", progress)
        animator.duration = 1500
        animator.interpolator = LinearInterpolator()
        animator.start()

        songTotalTime.text = MusicUtil.getReadableDurationString(total.toLong())
        songCurrentProgress.text = MusicUtil.getReadableDurationString(progress.toLong())
    }

    private fun hideVolumeIfAvailable() {
        volumeFragmentContainer.visibility = if (PreferenceUtil.getInstance().volumeToggle) View.VISIBLE else View.GONE
    }

    public override fun show() {
        playPauseButton!!.animate()
                .scaleX(1f)
                .scaleY(1f)
                .setInterpolator(DecelerateInterpolator())
                .start()
    }


    public override fun hide() {
        playPauseButton!!.apply {
            scaleX = 0f
            scaleY = 0f
            rotation = 0f
        }
    }

    override fun setDark(color: Int) {
        val colorBg = ATHUtil.resolveColor(activity, android.R.attr.colorBackground)
        val isDark = ColorUtil.isColorLight(colorBg)
        if (isDark) {
            lastPlaybackControlsColor = MaterialValueHelper.getSecondaryTextColor(activity, true)
            lastDisabledPlaybackControlsColor = MaterialValueHelper.getSecondaryDisabledTextColor(activity, true)
        } else {
            lastPlaybackControlsColor = MaterialValueHelper.getPrimaryTextColor(activity, false)
            lastDisabledPlaybackControlsColor = MaterialValueHelper.getPrimaryDisabledTextColor(activity, false)
        }
        val accentColor = ThemeStore.accentColor(context!!)
        val b = PreferenceUtil.getInstance().adaptiveColor
        updateTextColors(if (b) color else accentColor)
        setProgressBarColor(if (b) color else accentColor)


        updateRepeatState()
        updateShuffleState()
    }

    private fun setProgressBarColor(dark: Int) {
        ViewUtil.setProgressDrawable(progressSlider, dark)
    }

    private fun updateTextColors(color: Int) {
        val isDark = ColorUtil.isColorLight(color)
        val darkColor = ColorUtil.darkenColor(color)
        val colorPrimary = MaterialValueHelper.getPrimaryTextColor(context, isDark)
        val colorSecondary = MaterialValueHelper.getSecondaryTextColor(context, ColorUtil.isColorLight(darkColor))

        TintHelper.setTintAuto(playPauseButton!!, colorPrimary, false)
        TintHelper.setTintAuto(playPauseButton!!, color, true)

        title.setBackgroundColor(color)
        title.setTextColor(colorPrimary)
        text.setBackgroundColor(darkColor)
        text.setTextColor(colorSecondary)
    }

    override fun onServiceConnected() {
        updatePlayPauseDrawableState()
        updateRepeatState()
        updateShuffleState()
        updateSong()
    }

    override fun onPlayingMetaChanged() {
        super.onPlayingMetaChanged()
        updateSong()
    }

    override fun onPlayStateChanged() {
        updatePlayPauseDrawableState()
    }

    private fun setUpPlayPauseFab() {
        playPauseButton.setOnClickListener(PlayPauseButtonOnClickHandler())
    }

    private fun updatePlayPauseDrawableState() {
        if (MusicPlayerRemote.isPlaying) {
            playPauseButton.setImageResource(R.drawable.ic_pause_white_24dp)
        } else {
            playPauseButton.setImageResource(R.drawable.ic_play_arrow_white_24dp)
        }
    }

    private fun setUpMusicControllers() {
        setUpPlayPauseFab()
        setUpRepeatButton()
        setUpShuffleButton()
        setUpProgressSlider()
    }

    private fun updateSong() {
        //TransitionManager.beginDelayedTransition(viewGroup, new ChangeText().setChangeBehavior(ChangeText.CHANGE_BEHAVIOR_OUT_IN));
        val song = MusicPlayerRemote.currentSong
        title.text = song.title
        text.text = song.artistName

    }

    override fun setUpProgressSlider() {
        progressSlider!!.setOnSeekBarChangeListener(object : SimpleOnSeekbarChangeListener() {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                if (fromUser) {
                    MusicPlayerRemote.seekTo(progress)
                    onUpdateProgressViews(MusicPlayerRemote.songProgressMillis,
                            MusicPlayerRemote.songDurationMillis)
                }
            }
        })
    }

    override fun onRepeatModeChanged() {
        updateRepeatState()
    }

    override fun onShuffleModeChanged() {
        updateShuffleState()
    }

    private fun setUpRepeatButton() {
        repeatButton.setOnClickListener { MusicPlayerRemote.cycleRepeatMode() }
    }

    override fun updateRepeatState() {
        when (MusicPlayerRemote.repeatMode) {
            MusicService.REPEAT_MODE_NONE -> {
                repeatButton.setImageResource(R.drawable.ic_repeat_white_24dp)
                repeatButton.setColorFilter(lastDisabledPlaybackControlsColor, PorterDuff.Mode.SRC_IN)
            }
            MusicService.REPEAT_MODE_ALL -> {
                repeatButton.setImageResource(R.drawable.ic_repeat_white_24dp)
                repeatButton.setColorFilter(lastPlaybackControlsColor, PorterDuff.Mode.SRC_IN)
            }
            MusicService.REPEAT_MODE_THIS -> {
                repeatButton.setImageResource(R.drawable.ic_repeat_one_white_24dp)
                repeatButton.setColorFilter(lastPlaybackControlsColor, PorterDuff.Mode.SRC_IN)
            }
        }
    }

    private fun setUpShuffleButton() {
        shuffleButton.setOnClickListener { MusicPlayerRemote.toggleShuffleMode() }
    }

    override fun updateShuffleState() {
        when (MusicPlayerRemote.shuffleMode) {
            MusicService.SHUFFLE_MODE_SHUFFLE -> shuffleButton.setColorFilter(lastPlaybackControlsColor, PorterDuff.Mode.SRC_IN)
            else -> shuffleButton.setColorFilter(lastDisabledPlaybackControlsColor, PorterDuff.Mode.SRC_IN)
        }
    }

}
