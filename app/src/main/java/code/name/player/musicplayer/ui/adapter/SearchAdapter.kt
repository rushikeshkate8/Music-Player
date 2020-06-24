package code.name.player.musicplayer.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.util.Pair
import androidx.recyclerview.widget.RecyclerView
import code.name.player.appthemehelper.ThemeStore
import code.name.player.musicplayer.R
import code.name.player.musicplayer.glide.GlideApp
import code.name.player.musicplayer.glide.RetroGlideExtension
import code.name.player.musicplayer.helper.MusicPlayerRemote
import code.name.player.musicplayer.helper.menu.SongMenuHelper
import code.name.player.musicplayer.model.Album
import code.name.player.musicplayer.model.Artist
import code.name.player.musicplayer.model.Song
import code.name.player.musicplayer.ui.adapter.base.MediaEntryViewHolder
import code.name.player.musicplayer.util.MusicUtil
import code.name.player.musicplayer.util.NavigationUtil
import java.util.*


class SearchAdapter(private val activity: AppCompatActivity, private var dataSet: List<Any>?) : RecyclerView.Adapter<SearchAdapter.ViewHolder>() {

    fun swapDataSet(dataSet: ArrayList<Any>) {
        this.dataSet = dataSet
        notifyDataSetChanged()
    }

    override fun getItemViewType(position: Int): Int {
        if (dataSet!![position] is Album) return ALBUM
        if (dataSet!![position] is Artist) return ARTIST
        return if (dataSet!![position] is Song) SONG else HEADER
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return if (viewType == HEADER) ViewHolder(LayoutInflater.from(activity).inflate(R.layout.sub_header, parent, false), viewType) else ViewHolder(LayoutInflater.from(activity).inflate(R.layout.item_list, parent, false), viewType)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        when (getItemViewType(position)) {
            ALBUM -> {
                val album = dataSet!![position] as Album
                holder.title!!.text = album.title
                holder.text!!.text = album.artistName
                GlideApp.with(activity)
                        .asDrawable()
                        .load(RetroGlideExtension.getSongModel(album.safeGetFirstSong()))
                        .transition(RetroGlideExtension.getDefaultTransition())
                        .songOptions(album.safeGetFirstSong())
                        .into(holder.image!!)
            }
            ARTIST -> {
                val artist = dataSet!![position] as Artist
                holder.title!!.text = artist.name
                holder.text!!.text = MusicUtil.getArtistInfoString(activity, artist)
                GlideApp.with(activity)
                        .asBitmap()
                        .load(RetroGlideExtension.getArtistModel(artist))
                        .transition(RetroGlideExtension.getDefaultTransition())
                        .artistOptions(artist)
                        .into(holder.image!!)
            }
            SONG -> {
                val song = dataSet!![position] as Song
                holder.title!!.text = song.title
                holder.text!!.text = song.albumName
            }
            else -> {
                holder.title!!.text = dataSet!![position].toString()
                holder.title!!.setTextColor(ThemeStore.accentColor(activity))
            }
        }
    }

    override fun getItemCount(): Int {
        return dataSet!!.size
    }

    inner class ViewHolder(itemView: View, itemViewType: Int) : MediaEntryViewHolder(itemView) {
        init {
            itemView.setOnLongClickListener(null)

            if (itemViewType != HEADER) {
                if (separator != null) {
                    separator!!.visibility = View.GONE
                }
            }

            if (menu != null) {
                if (itemViewType == SONG) {
                    menu!!.visibility = View.VISIBLE
                    menu!!.setOnClickListener(object : SongMenuHelper.OnClickSongMenu(activity) {
                        override val song: Song
                            get() = dataSet!![adapterPosition] as Song
                    })
                } else {
                    menu!!.visibility = View.GONE
                }
            }

            when (itemViewType) {
                ALBUM -> setImageTransitionName(activity.getString(R.string.transition_album_art))
                ARTIST -> setImageTransitionName(activity.getString(R.string.transition_artist_image))
                else -> {
                    val container = itemView.findViewById<View>(R.id.image_container)
                    if (container != null) {
                        container.visibility = View.GONE
                    }
                }
            }
        }

        override fun onClick(v: View?) {
            val item = dataSet!![adapterPosition]
            when (itemViewType) {
                ALBUM -> NavigationUtil.goToAlbum(activity,
                        (item as Album).id, Pair.create(image, activity.resources.getString(R.string.transition_album_art)))
                ARTIST -> NavigationUtil.goToArtist(activity,
                        (item as Artist).id, Pair.create(image, activity.resources.getString(R.string.transition_artist_image)))
                SONG -> {
                    val playList = ArrayList<Song>()
                    playList.add(item as Song)
                    MusicPlayerRemote.openQueue(playList, 0, true)
                }
            }
        }
    }

    companion object {

        private val HEADER = 0
        private val ALBUM = 1
        private val ARTIST = 2
        private val SONG = 3
    }
}
