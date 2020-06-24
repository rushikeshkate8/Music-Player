package code.name.player.musicplayer.appwidgets

import android.app.PendingIntent
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.text.TextUtils
import android.view.View
import android.widget.RemoteViews
import code.name.player.appthemehelper.util.MaterialValueHelper
import code.name.player.musicplayer.Constants
import code.name.player.musicplayer.R
import code.name.player.musicplayer.appwidgets.base.BaseAppWidget
import code.name.player.musicplayer.glide.GlideApp
import code.name.player.musicplayer.glide.RetroGlideExtension
import code.name.player.musicplayer.glide.RetroSimpleTarget
import code.name.player.musicplayer.service.MusicService
import code.name.player.musicplayer.ui.activities.MainActivity
import code.name.player.musicplayer.util.RetroUtil
import com.bumptech.glide.request.target.Target
import com.bumptech.glide.request.transition.Transition


class AppWidgetBig : BaseAppWidget() {
    private var target: Target<Bitmap>? = null // for cancellation

    /**
     * Initialize given widgets to default state, where we launch Music on default click and hide
     * actions if service not running.
     */
    override fun defaultAppWidget(context: Context, appWidgetIds: IntArray) {
        val appWidgetView = RemoteViews(context.packageName,
                R.layout.app_widget_big)

        appWidgetView.setViewVisibility(R.id.media_titles, View.INVISIBLE)
        appWidgetView.setImageViewResource(R.id.image, R.drawable.default_album_art)
        appWidgetView.setImageViewBitmap(R.id.button_next, BaseAppWidget.createBitmap(
                RetroUtil.getTintedVectorDrawable(context, R.drawable.ic_skip_next_white_24dp,
                        MaterialValueHelper.getPrimaryTextColor(context, false))!!, 1f))
        appWidgetView.setImageViewBitmap(R.id.button_prev, BaseAppWidget.Companion.createBitmap(
                RetroUtil.getTintedVectorDrawable(context, R.drawable.ic_skip_previous_white_24dp,
                        MaterialValueHelper.getPrimaryTextColor(context, false))!!, 1f))
        appWidgetView.setImageViewBitmap(R.id.button_toggle_play_pause, BaseAppWidget.Companion.createBitmap(
                RetroUtil.getTintedVectorDrawable(context, R.drawable.ic_play_arrow_white_24dp,
                        MaterialValueHelper.getPrimaryTextColor(context, false))!!, 1f))

        linkButtons(context, appWidgetView)
        pushUpdate(context, appWidgetIds, appWidgetView)
    }

    /**
     * Update all active widget instances by pushing changes
     */
    override fun performUpdate(service: MusicService, appWidgetIds: IntArray?) {
        val appWidgetView = RemoteViews(service.packageName,
                R.layout.app_widget_big)

        val isPlaying = service.isPlaying
        val song = service.currentSong

        // Set the titles and artwork
        if (TextUtils.isEmpty(song.title) && TextUtils.isEmpty(song.artistName)) {
            appWidgetView.setViewVisibility(R.id.media_titles, View.INVISIBLE)
        } else {
            appWidgetView.setViewVisibility(R.id.media_titles, View.VISIBLE)
            appWidgetView.setTextViewText(R.id.title, song.title)
            appWidgetView.setTextViewText(R.id.text, getSongArtistAndAlbum(song))
        }

        // Set correct drawable for pause state
        val playPauseRes = if (isPlaying) R.drawable.ic_pause_white_24dp else R.drawable.ic_play_arrow_white_24dp
        appWidgetView.setImageViewBitmap(R.id.button_toggle_play_pause, BaseAppWidget.createBitmap(
                RetroUtil.getTintedVectorDrawable(service, playPauseRes,
                        MaterialValueHelper.getPrimaryTextColor(service, false))!!, 1f))

        // Set prev/next button drawables
        appWidgetView.setImageViewBitmap(R.id.button_next, BaseAppWidget.Companion.createBitmap(
                RetroUtil.getTintedVectorDrawable(service, R.drawable.ic_skip_next_white_24dp,
                        MaterialValueHelper.getPrimaryTextColor(service, false))!!, 1f))
        appWidgetView.setImageViewBitmap(R.id.button_prev, BaseAppWidget.Companion.createBitmap(
                RetroUtil.getTintedVectorDrawable(service, R.drawable.ic_skip_previous_white_24dp,
                        MaterialValueHelper.getPrimaryTextColor(service, false))!!, 1f))

        // Link actions buttons to intents
        linkButtons(service, appWidgetView)

        // Load the album cover async and push the update on completion
        val p = RetroUtil.getScreenSize(service)
        val widgetImageSize = Math.min(p.x, p.y)
        val appContext = service.applicationContext
        service.runOnUiThread {
            if (target != null) {
                GlideApp.with(appContext).clear(target)
            }
            target = GlideApp.with(appContext)
                    .asBitmap()
                    .load(RetroGlideExtension.getSongModel(song))
                    .transition(RetroGlideExtension.getDefaultTransition())
                    .songOptions(song)
                    .into(object : RetroSimpleTarget<Bitmap>(widgetImageSize, widgetImageSize) {
                        override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                            update(resource)
                        }

                        override fun onLoadFailed(errorDrawable: Drawable?) {
                            update(null)
                        }

                        private fun update(bitmap: Bitmap?) {
                            if (bitmap == null) {
                                appWidgetView.setImageViewResource(R.id.image, R.drawable.default_album_art)
                            } else {
                                appWidgetView.setImageViewBitmap(R.id.image, bitmap)
                            }
                            pushUpdate(appContext, appWidgetIds, appWidgetView)
                        }
                    })
        }
    }

    /**
     * Link up various button actions using [PendingIntent].
     */
    private fun linkButtons(context: Context, views: RemoteViews) {
        val action = Intent(context, MainActivity::class.java).putExtra("expand", true)
        var pendingIntent: PendingIntent

        val serviceName = ComponentName(context, MusicService::class.java)

        // Home
        action.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        pendingIntent = PendingIntent.getActivity(context, 0, action, 0)
        views.setOnClickPendingIntent(R.id.clickable_area, pendingIntent)

        // Previous track
        pendingIntent = buildPendingIntent(context, Constants.ACTION_REWIND, serviceName)
        views.setOnClickPendingIntent(R.id.button_prev, pendingIntent)

        // Play and pause
        pendingIntent = buildPendingIntent(context, Constants.ACTION_TOGGLE_PAUSE, serviceName)
        views.setOnClickPendingIntent(R.id.button_toggle_play_pause, pendingIntent)

        // Next track
        pendingIntent = buildPendingIntent(context, Constants.ACTION_SKIP, serviceName)
        views.setOnClickPendingIntent(R.id.button_next, pendingIntent)


    }

    companion object {


        const val NAME: String = "app_widget_big"

        private var mInstance: AppWidgetBig? = null

        val instance: AppWidgetBig
            @Synchronized get() {
                if (mInstance == null) {
                    mInstance = AppWidgetBig()
                }
                return mInstance!!
            }

    }
}
