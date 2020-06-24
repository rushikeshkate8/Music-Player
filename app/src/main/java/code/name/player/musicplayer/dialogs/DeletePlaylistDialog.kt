package code.name.player.musicplayer.dialogs

import android.os.Bundle
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import code.name.player.appthemehelper.ThemeStore
import code.name.player.appthemehelper.util.MaterialUtil
import code.name.player.musicplayer.R
import code.name.player.musicplayer.model.Playlist
import code.name.player.musicplayer.util.PlaylistsUtil
import code.name.player.musicplayer.views.RoundedBottomSheetDialogFragment
import kotlinx.android.synthetic.main.dialog_delete.*
import java.util.*


class DeletePlaylistDialog : RoundedBottomSheetDialogFragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.dialog_delete, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val playlists = arguments!!.getParcelableArrayList<Playlist>("playlist")
        val content: CharSequence

        content = if (playlists!!.size > 1) {
            Html.fromHtml(getString(R.string.delete_x_playlists, playlists.size))
        } else {
            Html.fromHtml(getString(R.string.delete_playlist_x, playlists[0].name))
        }
        dialogTitle.text = content
        dialogTitle.setTextColor(ThemeStore.textColorPrimary(context!!))

        actionDelete.apply {
            setText(R.string.action_delete)
            setOnClickListener {
                PlaylistsUtil.deletePlaylists(context, playlists)
                dismiss()
            }
            MaterialUtil.setTint(this)
            icon = ContextCompat.getDrawable(context, R.drawable.ic_delete_white_24dp)
        }
        actionCancel.apply {
            MaterialUtil.setTint(this, false)
            setOnClickListener { dismiss() }
            icon = ContextCompat.getDrawable(context, R.drawable.ic_close_white_24dp)
        }
    }


    companion object {

        fun create(playlist: Playlist): DeletePlaylistDialog {
            val list = ArrayList<Playlist>()
            list.add(playlist)
            return create(list)
        }

        fun create(playlist: ArrayList<Playlist>): DeletePlaylistDialog {
            val dialog = DeletePlaylistDialog()
            val args = Bundle()
            args.putParcelableArrayList("playlist", playlist)
            dialog.arguments = args
            return dialog
        }
    }

}