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
import code.name.player.musicplayer.glide.palette.BitmapPaletteWrapper
import code.name.player.musicplayer.service.MusicService
import code.name.player.musicplayer.ui.activities.MainActivity
import code.name.player.musicplayer.util.RetroUtil
import com.bumptech.glide.request.target.Target
import com.bumptech.glide.request.transition.Transition

class AppWidgetCard : BaseAppWidget() {
    private var target: Target<BitmapPaletteWrapper>? = null // for cancellation

    /**
     * Initialize given widgets to default state, where we launch Music on default click and hide
     * actions if service not running.
     */
    override fun defaultAppWidget(context: Context, appWidgetIds: IntArray) {
        val appWidgetView = RemoteViews(context.packageName,
                R.layout.app_widget_card)

        appWidgetView.setViewVisibility(R.id.media_titles, View.INVISIBLE)
        appWidgetView.setImageViewResource(R.id.image, R.drawable.default_album_art)
        appWidgetView.setImageViewBitmap(R.id.button_next, BaseAppWidget.Companion.createBitmap(RetroUtil.getTintedVectorDrawable(context, R.drawable.ic_skip_next_white_24dp, MaterialValueHelper.getSecondaryTextColor(context, true))!!, 1f))
        appWidgetView.setImageViewBitmap(R.id.button_prev, BaseAppWidget.Companion.createBitmap(RetroUtil.getTintedVectorDrawable(context, R.drawable.ic_skip_previous_white_24dp, MaterialValueHelper.getSecondaryTextColor(context, true))!!, 1f))
        appWidgetView.setImageViewBitmap(R.id.button_toggle_play_pause, BaseAppWidget.Companion.createBitmap(RetroUtil.getTintedVectorDrawable(context, R.drawable.ic_play_arrow_white_24dp, MaterialValueHelper.getSecondaryTextColor(context, true))!!, 1f))

        linkButtons(context, appWidgetView)
        pushUpdate(context, appWidgetIds, appWidgetView)
    }

    /**
     * Update all active widget instances by pushing changes
     */
    override fun performUpdate(service: MusicService, appWidgetIds: IntArray?) {
        val appWidgetView = RemoteViews(service.packageName,
                R.layout.app_widget_card)

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
        appWidgetView.setImageViewBitmap(R.id.button_toggle_play_pause, BaseAppWidget.Companion.createBitmap(
                RetroUtil.getTintedVectorDrawable(service, playPauseRes,
                        MaterialValueHelper.getSecondaryTextColor(service, true))!!, 1f))

        // Set prev/next button drawables
        appWidgetView.setImageViewBitmap(R.id.button_next, BaseAppWidget.Companion.createBitmap(
                RetroUtil.getTintedVectorDrawable(service, R.drawable.ic_skip_next_white_24dp,
                        MaterialValueHelper.getSecondaryTextColor(service, true))!!, 1f))
        appWidgetView.setImageViewBitmap(R.id.button_prev, BaseAppWidget.Companion.createBitmap(
                RetroUtil.getTintedVectorDrawable(service, R.drawable.ic_skip_previous_white_24dp,
                        MaterialValueHelper.getSecondaryTextColor(service, true))!!, 1f))

        // Link actions buttons to intents
        linkButtons(service, appWidgetView)

        if (imageSize == 0) {
            imageSize = service.resources.getDimensionPixelSize(R.dimen.app_widget_card_image_size)
        }
        if (cardRadius == 0f) {
            cardRadius = service.resources.getDimension(R.dimen.app_widget_card_radius)
        }
        val appContext = service.applicationContext
        // Load the album cover async and push the update on completion
        service.runOnUiThread {
            if (target != null) {
                GlideApp.with(appContext).clear(target)
            }
            GlideApp.with(appContext)
                    .asBitmapPalette()
                    .load(RetroGlideExtension.getSongModel(song))
                    .transition(RetroGlideExtension.getDefaultTransition())
                    .songOptions(song)
                    .into(object : RetroSimpleTarget<BitmapPaletteWrapper>(imageSize, imageSize) {
                        override fun onResourceReady(resource: BitmapPaletteWrapper, transition: Transition<in BitmapPaletteWrapper>?) {
                            val palette = resource.palette
                            update(resource.bitmap, palette.getVibrantColor(palette
                                    .getMutedColor(MaterialValueHelper.getSecondaryTextColor(service, true))))
                        }

                        override fun onLoadFailed(errorDrawable: Drawable?) {
                            super.onLoadFailed(errorDrawable)
                            update(null, MaterialValueHelper.getSecondaryTextColor(service, true))
                        }

                        private fun update(bitmap: Bitmap?, color: Int) {
                            // Set correct drawable for pause state
                            val playPauseRest = if (isPlaying) R.drawable.ic_pause_white_24dp
                            else
                                R.drawable.ic_play_arrow_white_24dp
                            appWidgetView.setImageViewBitmap(R.id.button_toggle_play_pause, BaseAppWidget.createBitmap(RetroUtil.getTintedVectorDrawable(service, playPauseRest, color)!!, 1f))
                            // Set prev/next button drawables
                            appWidgetView.setImageViewBitmap(R.id.button_next, BaseAppWidget.createBitmap(RetroUtil.getTintedVectorDrawable(service, R.drawable.ic_skip_next_white_24dp, color)!!, 1f))
                            appWidgetView.setImageViewBitmap(R.id.button_prev, BaseAppWidget.createBitmap(RetroUtil.getTintedVectorDrawable(service, R.drawable.ic_skip_previous_white_24dp, color)!!, 1f))

                            val image = getAlbumArtDrawable(service.resources, bitmap)
                            val roundedBitmap = BaseAppWidget.Companion.createRoundedBitmap(image, imageSize, imageSize, cardRadius, 0f, cardRadius, 0f)
                            appWidgetView.setImageViewBitmap(R.id.image, roundedBitmap)

                            pushUpdate(service, appWidgetIds, appWidgetView)
                        }
                    })
        }
    }

    /**
     * Link up various button actions using [PendingIntent].
     */
    private fun linkButtons(context: Context, views: RemoteViews) {
        val action: Intent = Intent(context, MainActivity::class.java).putExtra("expand", true)
        var pendingIntent: PendingIntent

        val serviceName = ComponentName(context, MusicService::class.java)

        // Home
        action.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        pendingIntent = PendingIntent.getActivity(context, 0, action, 0)
        views.setOnClickPendingIntent(R.id.image, pendingIntent)
        views.setOnClickPendingIntent(R.id.media_titles, pendingIntent)

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

        const val NAME = "app_widget_card"

        private var mInstance: AppWidgetCard? = null
        private var imageSize = 0
        private var cardRadius = 0f

        val instance: AppWidgetCard
            @Synchronized get() {
                if (mInstance == null) {
                    mInstance = AppWidgetCard()
                }
                return mInstance!!
            }
    }
}
