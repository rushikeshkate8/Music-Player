package code.name.player.musicplayer.ui.adapter

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import java.util.ArrayList
import java.util.Locale
import androidx.recyclerview.widget.RecyclerView
import code.name.player.musicplayer.R
import code.name.player.musicplayer.model.Genre
import code.name.player.musicplayer.ui.adapter.base.MediaEntryViewHolder
import code.name.player.musicplayer.util.NavigationUtil


class GenreAdapter(private val mActivity: Activity, dataSet: ArrayList<Genre>, private val mItemLayoutRes: Int) : RecyclerView.Adapter<GenreAdapter.ViewHolder>() {
    var dataSet = ArrayList<Genre>()
        private set

    init {
        this.dataSet = dataSet
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GenreAdapter.ViewHolder {
        return ViewHolder(LayoutInflater.from(mActivity).inflate(mItemLayoutRes, parent, false))
    }

    override fun onBindViewHolder(holder: GenreAdapter.ViewHolder, position: Int) {
        val genre = dataSet[position]
        if (holder.title != null) {
            holder.title!!.text = genre.name
        }
        if (holder.text != null) {
            holder.text!!.text = String.format(Locale.getDefault(), "%d %s", genre.songCount, if (genre.songCount > 1)
                mActivity.getString(R.string.songs)
            else
                mActivity.getString(R.string.song))
        }

        if (holder.separator != null) {
            holder.separator!!.visibility = View.VISIBLE
        }
    }

    override fun getItemCount(): Int {
        return dataSet.size
    }

    fun swapDataSet(list: ArrayList<Genre>) {
        dataSet = list
        notifyDataSetChanged()
    }

    inner class ViewHolder(itemView: View) : MediaEntryViewHolder(itemView) {
        init {
            if (menu != null) {
                menu!!.visibility = View.GONE
            }
            assert(imageContainer != null)
            imageContainer!!.visibility = View.GONE
        }

        override fun onClick(v: View?) {
            super.onClick(v)
            val genre = dataSet[adapterPosition]
            NavigationUtil.goToGenre(mActivity, genre)
        }
    }
}
