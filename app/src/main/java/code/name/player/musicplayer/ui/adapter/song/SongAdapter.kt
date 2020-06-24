package code.name.player.musicplayer.ui.adapter.song

import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatActivity
import androidx.core.util.Pair
import code.name.player.appthemehelper.util.ColorUtil
import code.name.player.appthemehelper.util.MaterialValueHelper
import code.name.player.musicplayer.R
import code.name.player.musicplayer.glide.GlideApp
import code.name.player.musicplayer.glide.RetroGlideExtension
import code.name.player.musicplayer.glide.RetroMusicColoredTarget
import code.name.player.musicplayer.helper.MusicPlayerRemote
import code.name.player.musicplayer.helper.SortOrder
import code.name.player.musicplayer.helper.menu.SongMenuHelper
import code.name.player.musicplayer.helper.menu.SongsMenuHelper
import code.name.player.musicplayer.interfaces.CabHolder
import code.name.player.musicplayer.model.Song
import code.name.player.musicplayer.ui.adapter.base.AbsMultiSelectAdapter
import code.name.player.musicplayer.ui.adapter.base.MediaEntryViewHolder
import code.name.player.musicplayer.util.MusicUtil
import code.name.player.musicplayer.util.NavigationUtil
import code.name.player.musicplayer.util.PreferenceUtil
import com.afollestad.materialcab.MaterialCab
import com.simplecityapps.recyclerview_fastscroll.views.FastScrollRecyclerView
import java.util.*

/**
 * Created by hemanths on 13/08/17.
 */

open class SongAdapter @JvmOverloads constructor(protected val activity: AppCompatActivity, dataSet: ArrayList<Song>,
                                                 @param:LayoutRes protected var itemLayoutRes: Int, usePalette: Boolean, cabHolder: CabHolder?,
                                                 showSectionName: Boolean = true) : AbsMultiSelectAdapter<SongAdapter.ViewHolder, Song>(activity, cabHolder, R.menu.menu_media_selection), MaterialCab.Callback, FastScrollRecyclerView.SectionedAdapter {
    var dataSet: ArrayList<Song>
        protected set

    protected var usePalette = false
    private var showSectionName = true

    init {
        this.dataSet = dataSet
        this.usePalette = usePalette
        this.showSectionName = showSectionName
        this.setHasStableIds(true)
    }

    open fun swapDataSet(dataSet: ArrayList<Song>) {
        this.dataSet = dataSet
        notifyDataSetChanged()
    }

    open fun usePalette(usePalette: Boolean) {
        this.usePalette = usePalette
        notifyDataSetChanged()
    }

    override fun getItemId(position: Int): Long {
        return dataSet[position].id.toLong()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(activity).inflate(itemLayoutRes, parent, false)
        return createViewHolder(view)
    }

    protected open fun createViewHolder(view: View): ViewHolder {
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val song = dataSet[position]

        val isChecked = isChecked(song)
        holder.itemView.isActivated = isChecked

        if (holder.adapterPosition == itemCount - 1) {
            if (holder.shortSeparator != null) {
                holder.shortSeparator!!.visibility = View.GONE
            }
        } else {
            if (holder.shortSeparator != null) {
                holder.shortSeparator!!.visibility = View.VISIBLE
            }
        }

        if (holder.title != null) {
            holder.title!!.text = getSongTitle(song)
        }
        if (holder.text != null) {
            holder.text!!.text = getSongText(song)
        }

        loadAlbumCover(song, holder)

    }

    private fun setColors(color: Int, holder: ViewHolder) {
        if (holder.paletteColorContainer != null) {
            holder.paletteColorContainer!!.setBackgroundColor(color)
            if (holder.title != null) {
                holder.title!!.setTextColor(MaterialValueHelper.getPrimaryTextColor(activity, ColorUtil.isColorLight(color)))
            }
            if (holder.text != null) {
                holder.text!!.setTextColor(MaterialValueHelper.getSecondaryTextColor(activity, ColorUtil.isColorLight(color)))
            }
        }
    }

    protected open fun loadAlbumCover(song: Song, holder: ViewHolder) {
        if (holder.image == null) {
            return
        }
        GlideApp.with(activity).asBitmapPalette()
                .load(RetroGlideExtension.getSongModel(song))
                .transition(RetroGlideExtension.getDefaultTransition())
                .songOptions(song)
                .into(object : RetroMusicColoredTarget(holder.image!!) {
                    override fun onColorReady(color: Int) {
                        setColors(color, holder)
                    }

                    override fun onLoadFailed(errorDrawable: Drawable?) {
                        super.onLoadFailed(errorDrawable)
                        setColors(defaultFooterColor, holder)
                    }
                })
    }

    private fun getSongTitle(song: Song): String? {
        return song.title
    }

    private fun getSongText(song: Song): String? {
        return song.artistName
    }

    override fun getItemCount(): Int {
        return dataSet.size
    }

    override fun getIdentifier(position: Int): Song? {
        return dataSet[position]
    }

    override fun getName(song: Song): String {
        return song.title!!
    }

    override fun onMultipleItemAction(menuItem: MenuItem,
                                      selection: ArrayList<Song>) {
        SongsMenuHelper.handleMenuClick(activity, selection, menuItem.itemId)
    }

    override fun getSectionName(position: Int): String {
        if (!showSectionName) {
            return "";
        }
        val sectionName: String? = when (PreferenceUtil.getInstance().songSortOrder) {
            SortOrder.SongSortOrder.SONG_A_Z, SortOrder.SongSortOrder.SONG_Z_A -> dataSet[position].title
            SortOrder.SongSortOrder.SONG_ALBUM -> dataSet[position].albumName
            SortOrder.SongSortOrder.SONG_ARTIST -> dataSet[position].artistName
            SortOrder.SongSortOrder.SONG_YEAR -> return MusicUtil.getYearString(dataSet[position].year)
            else -> {
                return ""
            }
        }

        return MusicUtil.getSectionName(sectionName)
    }

    open inner class ViewHolder(itemView: View) : MediaEntryViewHolder(itemView) {

        protected open var songMenuRes = SongMenuHelper.MENU_RES

        protected open val song: Song
            get() = dataSet[adapterPosition]

        init {
            setImageTransitionName(activity.getString(R.string.transition_album_art))
            if (menu != null) {
                menu!!.setOnClickListener(object : SongMenuHelper.OnClickSongMenu(activity) {
                    override val song: Song
                        get() = this@ViewHolder.song

                    override val menuRes: Int
                        get() = songMenuRes

                    override fun onMenuItemClick(item: MenuItem): Boolean {
                        return onSongMenuItemClick(item) || super.onMenuItemClick(item)
                    }
                })
            }
        }

        protected open fun onSongMenuItemClick(item: MenuItem): Boolean {
            if (image != null && image!!.visibility == View.VISIBLE) {
                when (item.itemId) {
                    R.id.action_go_to_album -> {
                        val albumPairs = arrayOf<Pair<*, *>>(Pair.create(imageContainer,
                                activity.resources.getString(R.string.transition_album_art)))
                        NavigationUtil.goToAlbum(activity, song.albumId, *albumPairs)
                        return true
                    }
                }
            }
            return false
        }

        override fun onClick(v: View?) {
            if (isInQuickSelectMode) {
                toggleChecked(adapterPosition)
            } else {
                MusicPlayerRemote.openQueue(dataSet, adapterPosition, true)
            }
        }

        override fun onLongClick(v: View?): Boolean {
            return toggleChecked(adapterPosition)
        }
    }

    companion object {

        val TAG: String = SongAdapter::class.java.simpleName
    }
}
