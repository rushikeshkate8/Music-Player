package code.name.player.musicplayer.ui.adapter.song

import android.view.View
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatActivity
import code.name.player.appthemehelper.ThemeStore
import code.name.player.musicplayer.R
import code.name.player.musicplayer.helper.MusicPlayerRemote
import code.name.player.musicplayer.interfaces.CabHolder
import code.name.player.musicplayer.model.Song
import java.util.*


class ShuffleButtonSongAdapter(activity: AppCompatActivity,
                               dataSet: ArrayList<Song>,
                               @LayoutRes itemLayoutRes: Int,
                               usePalette: Boolean,
                               cabHolder: CabHolder?) : AbsOffsetSongAdapter(activity, dataSet, itemLayoutRes, usePalette, cabHolder) {

    override fun createViewHolder(view: View): SongAdapter.ViewHolder {
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: SongAdapter.ViewHolder, position: Int) {
        if (holder.itemViewType == AbsOffsetSongAdapter.OFFSET_ITEM) {
            val accentColor = ThemeStore.accentColor(activity.applicationContext)
            if (holder.title != null) {
                holder.title!!.text = activity.resources.getString(R.string.action_shuffle_all)
                holder.title!!.setTextColor(accentColor)
            }
            if (holder.text != null) {
                holder.text!!.visibility = View.GONE
            }
            if (holder.menu != null) {
                holder.menu!!.visibility = View.GONE
            }
            if (holder.image != null) {
                val padding = activity.resources.getDimensionPixelSize(R.dimen.default_item_margin) / 2
                holder.image!!.setPadding(padding, padding, padding, padding)
                holder.image!!.setColorFilter(accentColor)
                holder.image!!.setImageResource(R.drawable.ic_shuffle_white_24dp)
            }
            if (holder.separator != null) {
                holder.separator!!.visibility = View.VISIBLE
            }
            if (holder.shortSeparator != null) {
                holder.shortSeparator!!.visibility = View.GONE
            }
        } else {
            super.onBindViewHolder(holder, position - 1)
        }
    }

    inner class ViewHolder(itemView: View) : AbsOffsetSongAdapter.ViewHolder(itemView) {

        override fun onClick(v: View?) {
            if (itemViewType == AbsOffsetSongAdapter.OFFSET_ITEM) {
                MusicPlayerRemote.openAndShuffleQueue(dataSet, true)
                return
            }
            super.onClick(v)
        }
    }
}