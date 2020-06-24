package code.name.player.musicplayer.dialogs

import android.os.Build
import android.os.Bundle
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import code.name.player.appthemehelper.ThemeStore
import code.name.player.musicplayer.R
import code.name.player.musicplayer.model.PlaylistSong
import code.name.player.musicplayer.util.PlaylistsUtil
import code.name.player.musicplayer.views.RoundedBottomSheetDialogFragment
import kotlinx.android.synthetic.main.dialog_remove_from_playlist.*
import java.util.*


class RemoveFromPlaylistDialog : RoundedBottomSheetDialogFragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        return inflater.inflate(R.layout.dialog_remove_from_playlist, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val songs = arguments!!.getParcelableArrayList<PlaylistSong>("songs")
        val title: Int
        val content: CharSequence
        if (songs!!.size > 1) {
            title = R.string.remove_songs_from_playlist_title
            content = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                Html.fromHtml(getString(R.string.remove_x_songs_from_playlist, songs.size), Html.FROM_HTML_MODE_LEGACY)
            } else {
                Html.fromHtml(getString(R.string.remove_x_songs_from_playlist, songs.size))
            }
        } else {
            title = R.string.remove_song_from_playlist_title
            content = Html.fromHtml(getString(R.string.remove_song_x_from_playlist, songs[0].title))
        }
        actionDelete.apply {
            text = content
            setTextColor(ThemeStore.textColorSecondary(context))
            setOnClickListener {
                PlaylistsUtil.removeFromPlaylist(activity!!, songs)
                dismiss()
            }
        }
        bannerTitle.apply {
            setText(title)
            setTextColor(ThemeStore.textColorPrimary(context))
        }

        actionCancel.apply {
            setTextColor(ThemeStore.textColorSecondary(context))
            setOnClickListener { dismiss() }
        }
    }

    companion object {

        fun create(song: PlaylistSong): RemoveFromPlaylistDialog {
            val list = ArrayList<PlaylistSong>()
            list.add(song)
            return create(list)
        }

        fun create(songs: ArrayList<PlaylistSong>): RemoveFromPlaylistDialog {
            val dialog = RemoveFromPlaylistDialog()
            val args = Bundle()
            args.putParcelableArrayList("songs", songs)
            dialog.arguments = args
            return dialog
        }
    }
}