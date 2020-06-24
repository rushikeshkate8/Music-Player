package code.name.player.musicplayer.ui.adapter.song

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatActivity
import code.name.player.appthemehelper.ThemeStore
import code.name.player.appthemehelper.util.TintHelper
import code.name.player.musicplayer.model.Song
import code.name.player.musicplayer.util.MusicUtil
import java.util.*


class SimpleSongAdapter(context: AppCompatActivity,
                        songs: ArrayList<Song>,
                        @LayoutRes i: Int) : SongAdapter(context, songs, i, false, null) {

    private var textColor: Int = 0

    init {
        textColor = ThemeStore.textColorPrimary(context)
    }

    override fun swapDataSet(dataSet: ArrayList<Song>) {
        this.dataSet.clear()
        this.dataSet = dataSet
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(activity).inflate(itemLayoutRes, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        super.onBindViewHolder(holder, position)
        val fixedTrackNumber = MusicUtil.getFixedTrackNumber(dataSet[position].trackNumber)

        if (holder.imageText != null) {
            holder.imageText!!.text = if (fixedTrackNumber > 0) fixedTrackNumber.toString() else "-"
            holder.imageText!!.setTextColor(textColor)
        }

        if (holder.time != null) {
            holder.time!!.text = MusicUtil.getReadableDurationString(dataSet[position].duration)
            holder.time!!.setTextColor(textColor)
        }
        if (holder.title != null) {
            holder.title!!.setTextColor(textColor)
        }
        if (holder.menu != null) {
            TintHelper.setTintAuto(holder.menu!!, textColor, false)
        }
    }

    override fun getItemCount(): Int {
        return dataSet.size
    }

    fun setTextColor(textColor: Int) {
        this.textColor = textColor
        notifyDataSetChanged()
    }
}
