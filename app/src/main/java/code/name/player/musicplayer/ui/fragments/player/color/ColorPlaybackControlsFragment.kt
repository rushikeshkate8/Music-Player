package code.name.player.musicplayer.ui.fragments.player.color

import android.animation.ObjectAnimator
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.drawable.LayerDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.DecelerateInterpolator
import android.view.animation.LinearInterpolator
import android.widget.SeekBar
import code.name.player.appthemehelper.util.ColorUtil
import code.name.player.appthemehelper.util.TintHelper
import code.name.player.musicplayer.R
import code.name.player.musicplayer.helper.MusicPlayerRemote
import code.name.player.musicplayer.helper.MusicProgressViewUpdateHelper
import code.name.player.musicplayer.helper.PlayPauseButtonOnClickHandler
import code.name.player.musicplayer.misc.SimpleOnSeekbarChangeListener
import code.name.player.musicplayer.service.MusicService
import code.name.player.musicplayer.ui.fragments.VolumeFragment
import code.name.player.musicplayer.ui.fragments.base.AbsPlayerControlsFragment
import code.name.player.musicplayer.util.MusicUtil
import kotlinx.android.synthetic.main.fragment_player_playback_controls.*
import kotlinx.android.synthetic.main.media_button.*
import kotlinx.android.synthetic.main.player_time.*

class ColorPlaybackControlsFragment : AbsPlayerControlsFragment() {

    private var lastPlaybackControlsColor: Int = 0
    private var lastDisabledPlaybackControlsColor: Int = 0
    private lateinit var progressViewUpdateHelper: MusicProgressViewUpdateHelper
    private lateinit var volumeFragment: VolumeFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        progressViewUpdateHelper = MusicProgressViewUpdateHelper(this)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_color_player_playback_controls, container, false)
    }

    override fun onResume() {
        super.onResume()
        progressViewUpdateHelper.start()
    }

    override fun onPause() {
        super.onPause()
        progressViewUpdateHelper.stop()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpMusicControllers()

        volumeFragment = childFragmentManager.findFragmentById(R.id.volumeFragment) as VolumeFragment
    }

    private fun updateSong() {
        val song = MusicPlayerRemote.currentSong
        title.text = song.title
        text.text = song.artistName
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

    override fun onRepeatModeChanged() {
        updateRepeatState()
    }

    override fun onShuffleModeChanged() {
        updateShuffleState()
    }


    fun setDark(textColor: Int, background: Int) {
        setDark(textColor)
        TintHelper.setTintAuto(playPauseButton, background, false)
        TintHelper.setTintAuto(playPauseButton, textColor, true)
    }

    override fun setDark(color: Int) {
        lastPlaybackControlsColor = color
        lastDisabledPlaybackControlsColor = ColorUtil.withAlpha(color, 0.5f)

        title!!.setTextColor(lastPlaybackControlsColor)
        text!!.setTextColor(lastDisabledPlaybackControlsColor)

        setProgressBarColor(lastPlaybackControlsColor, lastDisabledPlaybackControlsColor)

        volumeFragment.setTintable(lastPlaybackControlsColor)

        songCurrentProgress.setTextColor(lastDisabledPlaybackControlsColor)
        songTotalTime.setTextColor(lastDisabledPlaybackControlsColor)

        updateRepeatState()
        updateShuffleState()
        updatePrevNextColor()
    }

    private fun setProgressBarColor(c1: Int, c2: Int) {
        progressSlider.thumbTintList = ColorStateList.valueOf(c1)
        val ld = progressSlider.progressDrawable as LayerDrawable

        val clipDrawableProgress = ld.findDrawableByLayerId(android.R.id.progress)
        clipDrawableProgress.setColorFilter(c1, PorterDuff.Mode.SRC_IN)

        val clipDrawableBackground = ld.findDrawableByLayerId(android.R.id.background)
        clipDrawableBackground.setColorFilter(c2, PorterDuff.Mode.SRC_IN)
    }

    private fun setUpPlayPauseFab() {
        TintHelper.setTintAuto(playPauseButton, Color.WHITE, true)
        TintHelper.setTintAuto(playPauseButton, Color.BLACK, false)
        playPauseButton.setOnClickListener(PlayPauseButtonOnClickHandler())
    }


    private fun updatePlayPauseDrawableState() {
        when {
            MusicPlayerRemote.isPlaying -> playPauseButton.setImageResource(R.drawable.ic_pause_white_24dp)
            else -> playPauseButton.setImageResource(R.drawable.ic_play_arrow_white_24dp)
        }
    }


    private fun setUpMusicControllers() {
        setUpPlayPauseFab()
        setUpPrevNext()
        setUpRepeatButton()
        setUpShuffleButton()
        setUpProgressSlider()
    }

    private fun setUpPrevNext() {
        updatePrevNextColor()
        nextButton.setOnClickListener { MusicPlayerRemote.playNextSong() }
        previousButton.setOnClickListener { MusicPlayerRemote.back() }
    }

    private fun updatePrevNextColor() {
        nextButton.setColorFilter(lastPlaybackControlsColor, PorterDuff.Mode.SRC_IN)
        previousButton.setColorFilter(lastPlaybackControlsColor, PorterDuff.Mode.SRC_IN)
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


    public override fun show() {
        playPauseButton!!.animate()
                .scaleX(1f)
                .scaleY(1f)
                .rotation(360f)
                .setInterpolator(DecelerateInterpolator())
                .start()
    }

    public override fun hide() {
        playPauseButton.apply {
            scaleX = 0f
            scaleY = 0f
            rotation = 0f
        }
    }

    override fun setUpProgressSlider() {
        progressSlider.setOnSeekBarChangeListener(object : SimpleOnSeekbarChangeListener() {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                if (fromUser) {
                    MusicPlayerRemote.seekTo(progress)
                    onUpdateProgressViews(MusicPlayerRemote.songProgressMillis, MusicPlayerRemote.songDurationMillis)
                }
            }
        })
    }

    override fun onUpdateProgressViews(progress: Int, total: Int) {
        progressSlider!!.max = total

        val animator = ObjectAnimator.ofInt(progressSlider, "progress", progress)
        animator.duration = 1500
        animator.interpolator = LinearInterpolator()
        animator.start()

        songTotalTime.text = MusicUtil.getReadableDurationString(total.toLong())
        songCurrentProgress.text = MusicUtil.getReadableDurationString(progress.toLong())
    }
}
