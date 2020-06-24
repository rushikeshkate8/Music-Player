package code.name.player.musicplayer.ui.adapter.album

import android.graphics.drawable.Drawable
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import code.name.player.appthemehelper.util.ColorUtil
import code.name.player.appthemehelper.util.MaterialValueHelper
import code.name.player.musicplayer.glide.GlideApp
import code.name.player.musicplayer.glide.RetroGlideExtension
import code.name.player.musicplayer.glide.RetroMusicColoredTarget
import code.name.player.musicplayer.helper.HorizontalAdapterHelper
import code.name.player.musicplayer.interfaces.CabHolder
import code.name.player.musicplayer.model.Album
import code.name.player.musicplayer.util.MusicUtil
import java.util.*


class HorizontalAlbumAdapter(activity: AppCompatActivity, dataSet: ArrayList<Album>, usePalette: Boolean, cabHolder: CabHolder?) : AlbumAdapter(activity, dataSet, HorizontalAdapterHelper.LAYOUT_RES, usePalette, cabHolder) {

    override fun createViewHolder(view: View, viewType: Int): ViewHolder {
        val params = view.layoutParams as ViewGroup.MarginLayoutParams
        HorizontalAdapterHelper.applyMarginToLayoutParams(activity, params, viewType)
        return ViewHolder(view)
    }

    override fun setColors(color: Int, holder: ViewHolder) {
        if (holder.title != null) {
            holder.title!!.setTextColor(MaterialValueHelper.getPrimaryTextColor(activity, ColorUtil.isColorLight(color)))
        }
        if (holder.text != null) {
            holder.text!!.setTextColor(MaterialValueHelper.getSecondaryTextColor(activity, ColorUtil.isColorLight(color)))
        }
    }

    override fun loadAlbumCover(album: Album, holder: ViewHolder) {
        if (holder.image == null) return

        GlideApp.with(activity)
                .asBitmapPalette()
                .load(RetroGlideExtension.getSongModel(album.safeGetFirstSong()))
                .transition(RetroGlideExtension.getDefaultTransition())
                .songOptions(album.safeGetFirstSong())
                .dontAnimate()
                .into(object : RetroMusicColoredTarget(holder.image!!) {
                    override fun onColorReady(color: Int) {
                        if (usePalette)
                            setColors(color, holder)
                        else
                            setColors(albumArtistFooterColor, holder)
                    }

                    override fun onLoadFailed(errorDrawable: Drawable?) {
                        super.onLoadFailed(errorDrawable)
                        setColors(albumArtistFooterColor, holder)
                    }
                })
    }

    override fun getAlbumText(album: Album): String? {
        return MusicUtil.getYearString(album.year)
    }

    override fun getItemViewType(position: Int): Int {
        return HorizontalAdapterHelper.getItemViewtype(position, itemCount)
    }

    override fun getItemCount(): Int {
        return dataSet.size
    }

    companion object {
        val TAG: String = AlbumAdapter::class.java.simpleName
    }
}
