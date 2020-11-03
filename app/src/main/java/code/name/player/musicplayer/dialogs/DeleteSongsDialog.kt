package code.name.player.musicplayer.dialogs

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import code.name.player.appthemehelper.ThemeStore
import code.name.player.appthemehelper.util.MaterialUtil
import code.name.player.musicplayer.R
import code.name.player.musicplayer.helper.MusicPlayerRemote
import code.name.player.musicplayer.model.Song
import code.name.player.musicplayer.util.MusicUtil
import code.name.player.musicplayer.views.RoundedBottomSheetDialogFragment
import com.facebook.ads.Ad
import com.facebook.ads.AdError
import com.facebook.ads.InterstitialAd
import com.facebook.ads.InterstitialAdListener
import kotlinx.android.synthetic.main.dialog_delete.*
import java.util.*

class DeleteSongsDialog : RoundedBottomSheetDialogFragment() {
    private var wasPlaying = false
    var interstitialAd: InterstitialAd? = null
    @SuppressLint("StringFormatMatches")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dialogTitle.setTextColor(ThemeStore.textColorPrimary(context!!))
        //noinspection unchecked,ConstantConditions
        val songs = arguments!!.getParcelableArrayList<Song>("songs")
        if (songs != null) {
            if(songs.size > 1) {
                interstitialAd = InterstitialAd(activity, "266586284404690_269005550829430")
                interstitialAd!!.loadAd()
                interstitialAd!!.setAdListener(object : InterstitialAdListener {
                    override fun onInterstitialDisplayed(ad: Ad?) {
                        // Interstitial ad displayed callback
                    }

                    override fun onInterstitialDismissed(ad: Ad?) {
                        // Interstitial dismissed callback
                        if (wasPlaying) {
                            MusicPlayerRemote.resumePlaying()
                            wasPlaying = false
                        }
                    }

                    override fun onError(ad: Ad?, adError: AdError) {
                        // Ad error callback
                    }

                    override fun onAdLoaded(ad: Ad?) {
                        // Interstitial ad is loaded and ready to be displayed
                        // Show the ad
                    }

                    override fun onAdClicked(ad: Ad?) {
                        // Ad clicked callback
                    }

                    override fun onLoggingImpression(ad: Ad?) {
                        // Ad impression logged callback
                        //if (!MusicPlayerRemote.isPlaying) {
                        //MusicPlayerRemote.resumePlaying()
                        //  }
                    }
                })
            }
        }
        val content: CharSequence
        if (songs != null) {
            content = if (songs.size > 1) {
               getString(R.string.delete_x_songs, songs.size)
            } else {
                getString(R.string.delete_song_x, songs[0].title)
            }
            dialogTitle.text = content
        }
        actionDelete.apply {
            setOnClickListener {
                if (songs != null) {
                    MusicUtil.deleteTracks(activity!!, songs)
                    if(songs.size > 1)
                    {
                        val handler = Handler()
                        handler.postDelayed(Runnable { // Check if interstitialAd has been loaded successfully
                            if(interstitialAd!!.isAdLoaded)
                            {
                                if(MusicPlayerRemote.isPlaying)
                                {
                                    wasPlaying = true
                                    MusicPlayerRemote.pauseSong()
                                }
                                interstitialAd!!.show()

                            }
                        }, 4000)
                    }
                }
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


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.dialog_delete, container, false)
    }

    companion object {

        fun create(song: Song): DeleteSongsDialog {
            val list = ArrayList<Song>()
            list.add(song)
            return create(list)
        }

        fun create(songs: ArrayList<Song>): DeleteSongsDialog {
            val dialog = DeleteSongsDialog()
            val args = Bundle()
            args.putParcelableArrayList("songs", songs)
            dialog.arguments = args
            return dialog
        }
    }
}

