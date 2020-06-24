package code.name.player.musicplayer.ui.adapter

import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import code.name.player.musicplayer.interfaces.CabHolder
import code.name.player.musicplayer.model.Song
import code.name.player.musicplayer.ui.adapter.song.SongAdapter
import java.util.*

class SpanSongsAdapter(activity: AppCompatActivity, dataSet: ArrayList<Song>, itemLayoutRes: Int, usePalette: Boolean, cabHolder: CabHolder?) : SongAdapter(activity, dataSet, itemLayoutRes, usePalette, cabHolder) {

    override fun onBindViewHolder(holder: SongAdapter.ViewHolder, position: Int) {
        super.onBindViewHolder(holder, position)
        if (position == 0) {
            val params = StaggeredGridLayoutManager.LayoutParams(StaggeredGridLayoutManager.LayoutParams.WRAP_CONTENT, StaggeredGridLayoutManager.LayoutParams.MATCH_PARENT)
            params.isFullSpan = true
            holder.itemView.layoutParams = params
        }
    }
}
