package code.name.player.musicplayer.glide

import android.graphics.drawable.Drawable
import android.widget.ImageView
import code.name.player.appthemehelper.util.ATHUtil
import code.name.player.musicplayer.R
import code.name.player.musicplayer.glide.palette.BitmapPaletteTarget
import code.name.player.musicplayer.glide.palette.BitmapPaletteWrapper
import code.name.player.musicplayer.util.PreferenceUtil
import code.name.player.musicplayer.util.RetroColorUtil.getColor
import code.name.player.musicplayer.util.RetroColorUtil.getDominantColor
import com.bumptech.glide.request.transition.Transition


abstract class RetroMusicColoredTarget(view: ImageView) : BitmapPaletteTarget(view) {

    protected val defaultFooterColor: Int
        get() = ATHUtil.resolveColor(getView().context, R.attr.defaultFooterColor)

    protected val albumArtistFooterColor: Int
        get() = ATHUtil.resolveColor(getView().context, R.attr.cardBackgroundColor)

    override fun onLoadFailed(errorDrawable: Drawable?) {
        super.onLoadFailed(errorDrawable)
        onColorReady(defaultFooterColor)
    }

    override fun onResourceReady(resource: BitmapPaletteWrapper,
                                 glideAnimation: Transition<in BitmapPaletteWrapper>?) {
        super.onResourceReady(resource, glideAnimation)
        val defaultColor = defaultFooterColor

        val primaryColor = getColor(resource.palette, defaultColor)
        val dominantColor = getDominantColor(resource.bitmap, defaultColor)

        onColorReady(if (PreferenceUtil.getInstance().isDominantColor)
            dominantColor
        else
            primaryColor)
    }

    abstract fun onColorReady(color: Int)
}
