package code.name.player.musicplayer.dialogs

import android.content.Context
import android.content.res.ColorStateList
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import code.name.player.appthemehelper.ThemeStore
import code.name.player.appthemehelper.util.MaterialUtil
import code.name.player.musicplayer.R
import code.name.player.musicplayer.model.Song
import code.name.player.musicplayer.util.PlaylistsUtil
import code.name.player.musicplayer.views.RoundedBottomSheetDialogFragment
import kotlinx.android.synthetic.main.dialog_playlist.*
import java.util.*


class CreatePlaylistDialog : RoundedBottomSheetDialogFragment() {


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        return inflater.inflate(R.layout.dialog_playlist, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val accentColor = ThemeStore.accentColor(Objects.requireNonNull<Context>(context))

        MaterialUtil.setTint(actionCreate, true)
        MaterialUtil.setTint(actionCancel, false)
        MaterialUtil.setTint(actionNewPlaylistContainer, true)

        actionNewPlaylist.setHintTextColor(ColorStateList.valueOf(accentColor))
        actionNewPlaylist.setTextColor(ThemeStore.textColorPrimary(context!!))
        bannerTitle.setTextColor(ThemeStore.textColorPrimary(context!!))


        actionCancel.setOnClickListener { dismiss() }
        actionCreate.setOnClickListener {
            if (activity == null) {
                return@setOnClickListener
            }
            if (!actionNewPlaylist!!.text!!.toString().trim { it <= ' ' }.isEmpty()) {
                val playlistId = PlaylistsUtil
                        .createPlaylist(activity!!, actionNewPlaylist!!.text!!.toString())
                if (playlistId != -1 && activity != null) {

                    val songs = arguments!!.getParcelableArrayList<Song>("songs")
                    if (songs != null) {
                        PlaylistsUtil.addToPlaylist(activity!!, songs, playlistId, true)
                    }
                }
            }
            dismiss()
        }
    }

    companion object {

        @JvmOverloads
        fun create(song: Song? = null): CreatePlaylistDialog {
            val list = ArrayList<Song>()
            if (song != null) {
                list.add(song)
            }
            return create(list)
        }

        fun create(songs: ArrayList<Song>): CreatePlaylistDialog {
            val dialog = CreatePlaylistDialog()
            val args = Bundle()
            args.putParcelableArrayList("songs", songs)
            dialog.arguments = args
            return dialog
        }
    }
}