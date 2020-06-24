package code.name.player.musicplayer.service.notification

import android.app.PendingIntent
import android.content.ComponentName
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.os.Build
import android.text.Html
import androidx.core.app.NotificationCompat
import code.name.player.musicplayer.Constants.ACTION_QUIT
import code.name.player.musicplayer.Constants.ACTION_REWIND
import code.name.player.musicplayer.Constants.ACTION_SKIP
import code.name.player.musicplayer.Constants.ACTION_TOGGLE_PAUSE
import code.name.player.musicplayer.R
import code.name.player.musicplayer.glide.GlideApp
import code.name.player.musicplayer.glide.RetroGlideExtension
import code.name.player.musicplayer.glide.RetroSimpleTarget
import code.name.player.musicplayer.glide.palette.BitmapPaletteWrapper
import code.name.player.musicplayer.service.MusicService
import code.name.player.musicplayer.ui.activities.MainActivity
import code.name.player.musicplayer.util.PreferenceUtil
import code.name.player.musicplayer.util.RetroColorUtil
import com.bumptech.glide.request.target.Target
import com.bumptech.glide.request.transition.Transition

class PlayingNotificationImpl24 : PlayingNotification() {
    private var target: Target<BitmapPaletteWrapper>? = null
    @Synchronized
    override fun update() {
        stopped = false

        val song = service.currentSong
        val isPlaying = service.isPlaying

        val playButtonResId = if (isPlaying)
            R.drawable.ic_pause_white_24dp
        else
            R.drawable.ic_play_arrow_white_24dp

        val action = Intent(service, MainActivity::class.java)
        action.putExtra("expand", true)
        action.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        val clickIntent = PendingIntent
                .getActivity(service, 0, action, PendingIntent.FLAG_UPDATE_CURRENT)

        val serviceName = ComponentName(service, MusicService::class.java)
        val intent = Intent(ACTION_QUIT)
        intent.component = serviceName
        val deleteIntent = PendingIntent.getService(service, 0, intent, 0)

        val bigNotificationImageSize = service.resources
                .getDimensionPixelSize(R.dimen.notification_big_image_size)
        service.runOnUiThread {
            if (target != null) {
                GlideApp.with(service).clear(target);
            }
            target = GlideApp.with(service)
                    .asBitmapPalette()
                    .load(RetroGlideExtension.getSongModel(song))
                    .transition(RetroGlideExtension.getDefaultTransition())
                    .songOptions(song)
                    .into(object : RetroSimpleTarget<BitmapPaletteWrapper>(bigNotificationImageSize, bigNotificationImageSize) {
                        override fun onResourceReady(resource: BitmapPaletteWrapper, transition: Transition<in BitmapPaletteWrapper>?) {
                            update(resource.bitmap, when {
                                PreferenceUtil.getInstance().isDominantColor -> RetroColorUtil.getDominantColor(resource.bitmap, Color.TRANSPARENT)
                                else -> RetroColorUtil.getColor(resource.palette, Color.TRANSPARENT)
                            })
                        }

                        override fun onLoadFailed(errorDrawable: Drawable?) {
                            update(null, Color.TRANSPARENT)
                        }

                        fun update(bitmap: Bitmap?, color: Int) {
                            var bitmapFinal = bitmap
                            if (bitmapFinal == null) {
                                bitmapFinal = BitmapFactory.decodeResource(service.resources, R.drawable.default_album_art)
                            }
                            val playPauseAction = NotificationCompat.Action(
                                    playButtonResId,
                                    service.getString(R.string.action_play_pause),
                                    retrievePlaybackAction(ACTION_TOGGLE_PAUSE))

                            val closeAction = NotificationCompat.Action(
                                    R.drawable.ic_close_white_24dp,
                                    service.getString(R.string.close_notification),
                                    retrievePlaybackAction(ACTION_QUIT))

                            val previousAction = NotificationCompat.Action(
                                    R.drawable.ic_skip_previous_white_24dp,
                                    service.getString(R.string.action_previous),
                                    retrievePlaybackAction(ACTION_REWIND))

                            val nextAction = NotificationCompat.Action(
                                    R.drawable.ic_skip_next_white_24dp,
                                    service.getString(R.string.action_next),
                                    retrievePlaybackAction(ACTION_SKIP))

                            val builder = NotificationCompat.Builder(service,
                                    PlayingNotification.NOTIFICATION_CHANNEL_ID)
                                    .setSmallIcon(R.drawable.ic_notification)
                                    .setLargeIcon(bitmapFinal)
                                    .setContentIntent(clickIntent)
                                    .setDeleteIntent(deleteIntent)
                                    .setContentTitle(Html.fromHtml("<b>" + song.title + "</b>"))
                                    .setContentText(song.artistName)
                                    .setSubText(Html.fromHtml("<b>" + song.albumName + "</b>"))
                                    .setOngoing(isPlaying)
                                    .setShowWhen(false)
                                    .addAction(previousAction)
                                    .addAction(playPauseAction)
                                    .addAction(nextAction)
                                    .addAction(closeAction)

                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                builder.setStyle(androidx.media.app.NotificationCompat.MediaStyle()
                                        .setMediaSession(service.mediaSession.sessionToken)
                                        .setShowActionsInCompactView(0, 1, 2, 3, 4))
                                        .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                                if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.O && PreferenceUtil.getInstance().coloredNotification()) {
                                    builder.color = color
                                }
                            }

                            if (stopped) {
                                return  // notification has been stopped before loading was finished
                            }
                            updateNotifyModeAndPostNotification(builder.build())
                        }
                    })
        }
    }

    private fun retrievePlaybackAction(action: String): PendingIntent {
        val serviceName = ComponentName(service, MusicService::class.java)
        val intent = Intent(action)
        intent.component = serviceName
        return PendingIntent.getService(service, 0, intent, 0)
    }
}