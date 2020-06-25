package code.name.player.musicplayer.dialogs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import code.name.player.appthemehelper.ThemeStore
import code.name.player.musicplayer.R
import code.name.player.musicplayer.loaders.PlaylistLoader
import code.name.player.musicplayer.model.Song
import code.name.player.musicplayer.ui.adapter.playlist.AddToPlaylist
import code.name.player.musicplayer.views.RoundedBottomSheetDialogFragment
import kotlinx.android.synthetic.main.dialog_add_to_playlist.*
import java.util.*

/**
 * @author Karim Abou Zeid (kabouzeid), Aidan Follestad (afollestad)
 */
class AddToPlaylistDialog : RoundedBottomSheetDialogFragment() {


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        return inflater.inflate(R.layout.dialog_add_to_playlist, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        actionAddPlaylist.setOnClickListener {
            val songs = arguments!!.getParcelableArrayList<Song>("songs")
            CreatePlaylistDialog.create(songs!!).show(activity!!.supportFragmentManager, "ADD_TO_PLAYLIST")
            dismiss()
        }

        bannerTitle.setTextColor(ThemeStore.textColorPrimary(context!!))
        val songs = arguments!!.getParcelableArrayList<Song>("songs")
        val playlists = PlaylistLoader.getAllPlaylists(activity!!).blockingFirst()
        val playlistAdapter = dialog?.let { AddToPlaylist(activity!!, playlists, R.layout.item_playlist, songs!!, it) }
        recyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            itemAnimator = DefaultItemAnimator()
            adapter = playlistAdapter
        }
    }

    companion object {

        fun create(song: Song): AddToPlaylistDialog {
            val list = ArrayList<Song>()
            list.add(song)
            return create(list)
        }

        fun create(songs: ArrayList<Song>): AddToPlaylistDialog {
            val dialog = AddToPlaylistDialog()
            val args = Bundle()
            args.putParcelableArrayList("songs", songs)
            dialog.arguments = args
            return dialog
        }
    }
}