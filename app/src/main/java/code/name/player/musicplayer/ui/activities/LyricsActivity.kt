package code.name.player.musicplayer.ui.activities

import android.annotation.SuppressLint
import android.os.AsyncTask
import android.os.Bundle
import android.text.InputType
import android.text.TextUtils
import android.view.*
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.viewpager.widget.ViewPager
import code.name.player.appthemehelper.ThemeStore
import code.name.player.appthemehelper.util.TintHelper
import code.name.player.musicplayer.App
import code.name.player.musicplayer.R
import code.name.player.musicplayer.helper.MusicPlayerRemote
import code.name.player.musicplayer.helper.MusicProgressViewUpdateHelper
import code.name.player.musicplayer.model.Song
import code.name.player.musicplayer.model.lyrics.Lyrics
import code.name.player.musicplayer.ui.activities.base.AbsMusicServiceActivity
import code.name.player.musicplayer.ui.activities.tageditor.WriteTagsAsyncTask
import code.name.player.musicplayer.ui.fragments.base.AbsMusicServiceFragment
import code.name.player.musicplayer.util.LyricUtil
import code.name.player.musicplayer.util.MusicUtil
import code.name.player.musicplayer.util.PreferenceUtil
import code.name.player.musicplayer.util.RetroUtil
import com.afollestad.materialdialogs.MaterialDialog
import com.facebook.ads.AdSize
import com.facebook.ads.AdView
import com.facebook.ads.AudienceNetworkAds
import kotlinx.android.synthetic.main.activity_lyrics.*
import kotlinx.android.synthetic.main.fragment_lyrics.*
import kotlinx.android.synthetic.main.fragment_synced.*
import org.jaudiotagger.tag.FieldKey
import java.io.File
import java.util.*

class LyricsActivity : AbsMusicServiceActivity(), View.OnClickListener, ViewPager.OnPageChangeListener {
    private var adView: AdView? = null
    override fun onPageScrollStateChanged(state: Int) {

    }

    override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {

    }

    override fun onPageSelected(position: Int) {
        PreferenceUtil.getInstance().lyricsOptions = position
    }

    override fun onClick(v: View?) {
        when (viewPager.currentItem) {
            0 -> showSyncedLyrics()
            1 -> showLyricsSaveDialog()
        }
    }

    private lateinit var song: Song
    private var lyricsString: String? = null

    private val googleSearchLrcUrl: String
        get() {
            var baseUrl = "http://www.google.com/search?"
            var query = song.title + "+" + song.artistName
            query = "q=" + query.replace(" ", "+") + " .lrc"
            baseUrl += query
            return baseUrl
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lyrics)
        AudienceNetworkAds.initialize(this)
        setStatusbarColorAuto()
        setTaskDescriptionColorAuto()
        setNavigationbarColorAuto()
        // Instantiate an AdView object.
        // NOTE: The placement ID from the Facebook Monetization Manager identifies your App.
        // To get test ads, add IMG_16_9_APP_INSTALL# to your placement id. Remove this when your app is ready to serve real ads.


        // Instantiate an AdView object.
        // NOTE: The placement ID from the Facebook Monetization Manager identifies your App.
        // To get test ads, add IMG_16_9_APP_INSTALL# to your placement id. Remove this when your app is ready to serve real ads.
        adView = AdView(this, "266586284404690_267140967682555", AdSize.BANNER_HEIGHT_50)

        // Find the Ad Container

        // Find the Ad Container
        val adContainer: LinearLayout = findViewById<View>(R.id.lyrics) as LinearLayout

        // Add the ad view to your activity layout

        // Add the ad view to your activity layout
        adContainer.addView(adView)

        // Request an ad

        // Request an ad
        adView!!.loadAd()
        appBarLayout.setBackgroundColor(ThemeStore.primaryColor(this))
        toolbar.apply {
            setBackgroundColor(ThemeStore.primaryColor(this@LyricsActivity))
            navigationIcon = TintHelper.createTintedDrawable(ContextCompat.getDrawable(this@LyricsActivity, R.drawable.ic_keyboard_backspace_black_24dp), ThemeStore.textColorSecondary(this@LyricsActivity))
            setSupportActionBar(toolbar)
        }


        TintHelper.setTintAuto(fab, ThemeStore.accentColor(this), true)
        setupWakelock()

        viewPager.apply {
            adapter = PagerAdapter(supportFragmentManager)
            currentItem = PreferenceUtil.getInstance().lyricsOptions
            addOnPageChangeListener(this@LyricsActivity)
        }

        tabs.apply {
            setupWithViewPager(viewPager)
            setSelectedTabIndicator(TintHelper.createTintedDrawable(ContextCompat.getDrawable(this@LyricsActivity, R.drawable.tab_indicator), ThemeStore.accentColor(this@LyricsActivity)))
            setTabTextColors(ThemeStore.textColorSecondary(this@LyricsActivity), ThemeStore.accentColor(this@LyricsActivity))
            setSelectedTabIndicatorColor(ThemeStore.accentColor(context))
        }
        fab.setOnClickListener(this)
    }

    override fun onPlayingMetaChanged() {
        super.onPlayingMetaChanged()
        song = MusicPlayerRemote.currentSong
        toolbar.title = song.title
        toolbar.subtitle = song.artistName
    }

    override fun onServiceConnected() {
        super.onServiceConnected()
        song = MusicPlayerRemote.currentSong
        toolbar.title = song.title
        toolbar.subtitle = song.artistName
    }

    private fun setupWakelock() {
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            onBackPressed()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun showSyncedLyrics() {
        var content = ""
        try {
            content = LyricUtil.getStringFromFile(song.title, song.artistName)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        MaterialDialog.Builder(this)
                .title("Add lyrics")
                .neutralText("Search")
                .content("Add time frame lyrics")
                .negativeText("Delete")
                .onNegative { _, _ ->
                    LyricUtil.deleteLrcFile(song.title, song.artistName)
                }
                .onNeutral { _, _ -> RetroUtil.openUrl(this@LyricsActivity, googleSearchLrcUrl) }
                .inputType(InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_FLAG_MULTI_LINE)
                .input("Paste lyrics here", content) { _, input ->
                    LyricUtil.writeLrcToLoc(song.title, song.artistName, input.toString())
                }.show()
    }

    private fun showLyricsSaveDialog() {
        val content: String = if (lyricsString == null) {
            ""
        } else {
            lyricsString!!
        }
        MaterialDialog.Builder(this)
                .title("Add lyrics")
                .neutralText("Search")
                .onNeutral { _, _ -> RetroUtil.openUrl(this@LyricsActivity, getGoogleSearchUrl(song.title, song.artistName)) }
                .inputType(InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_FLAG_MULTI_LINE)
                .input("Paste lyrics here", content) { _, input ->
                    val fieldKeyValueMap = EnumMap<FieldKey, String>(FieldKey::class.java)
                    fieldKeyValueMap[FieldKey.LYRICS] = input.toString()
                    WriteTagsAsyncTask(this@LyricsActivity).execute(WriteTagsAsyncTask.LoadingInfo(getSongPaths(song), fieldKeyValueMap, null))
                }
                .show()
    }

    private fun getSongPaths(song: Song): ArrayList<String> {
        val paths = ArrayList<String>(1)
        paths.add(song.data!!)
        return paths
    }

    private fun getGoogleSearchUrl(title: String?, text: String?): String {
        var baseUrl = "http://www.google.com/search?"
        var query = "$title+$text"
        query = "q=" + query.replace(" ", "+") + " lyrics"
        baseUrl += query
        return baseUrl
    }

    class PagerAdapter(fm: FragmentManager) : FragmentStatePagerAdapter(fm) {
        override fun getItem(position: Int): Fragment {
            return when (position) {
                0 -> SyncedLyricsFragment()
                1 -> OfflineLyricsFragment()
                else -> SyncedLyricsFragment()
            }
        }

        override fun getPageTitle(position: Int): CharSequence? {
            return when (position) {
                0 -> App.context.getString(R.string.synced_lyrics)
                1 -> App.context.getString(R.string.normal_lyrics)
                else -> {
                    App.context.getString(R.string.synced_lyrics)
                }
            }
        }

        override fun getCount(): Int {
            return 2
        }

    }

    abstract class BaseLyricsFragment : AbsMusicServiceFragment() {
        abstract fun upDateSong()

        override fun onPlayingMetaChanged() {
            super.onPlayingMetaChanged()
            upDateSong()
        }

        override fun onServiceConnected() {
            super.onServiceConnected()
            upDateSong()
        }

    }

    class OfflineLyricsFragment : BaseLyricsFragment() {
        override fun upDateSong() {
            loadSongLyrics()
        }

        private var updateLyricsAsyncTask: AsyncTask<*, *, *>? = null
        private var lyrics: Lyrics? = null

        @SuppressLint("StaticFieldLeak")
        private fun loadSongLyrics() {
            if (updateLyricsAsyncTask != null) {
                updateLyricsAsyncTask!!.cancel(false)
            }
            val song = MusicPlayerRemote.currentSong
            updateLyricsAsyncTask = object : AsyncTask<Void?, Void?, Lyrics?>() {
                override fun doInBackground(vararg params: Void?): Lyrics? {
                    val data = MusicUtil.getLyrics(song)
                    return if (TextUtils.isEmpty(data)) {
                        null
                    } else Lyrics.parse(song, data)
                }

                override fun onPreExecute() {
                    super.onPreExecute()
                    lyrics = null
                }

                override fun onPostExecute(l: Lyrics?) {
                    lyrics = l
                    offlineLyrics?.visibility = View.VISIBLE
                    if (l == null) {
                        offlineLyrics?.setText(R.string.no_lyrics_found)
                        return
                    }
                    (activity as LyricsActivity).lyricsString = l.data
                    offlineLyrics?.text = l.data
                }

                override fun onCancelled(s: Lyrics?) {
                    onPostExecute(null)
                }
            }.execute()
        }

        override fun onDestroyView() {
            super.onDestroyView()
            if (updateLyricsAsyncTask != null && !updateLyricsAsyncTask!!.isCancelled) {
                updateLyricsAsyncTask!!.cancel(true)
            }
        }

        override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
            return inflater.inflate(R.layout.fragment_lyrics, container, false)
        }
    }

    class SyncedLyricsFragment : BaseLyricsFragment(), MusicProgressViewUpdateHelper.Callback {
        override fun upDateSong() {
            loadLRCLyrics()
        }

        private lateinit var updateHelper: MusicProgressViewUpdateHelper
        override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
            return inflater.inflate(R.layout.fragment_synced, container, false)
        }

        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            updateHelper = MusicProgressViewUpdateHelper(this, 500, 1000)
        }

        override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
            super.onViewCreated(view, savedInstanceState)
            setupLyricsView()
        }

        private fun setupLyricsView() {
            lyricsView.apply {
                setOnPlayerClickListener { progress, _ -> MusicPlayerRemote.seekTo(progress.toInt()) }
                setDefaultColor(ContextCompat.getColor(context, R.color.md_grey_400))
                setHintColor(ThemeStore.textColorPrimary(context))
                setHighLightColor(ThemeStore.textColorPrimary(context))
                setTextSize(RetroUtil.convertDpToPixel(18f, context).toInt())
            }
        }

        override fun onResume() {
            super.onResume()
            updateHelper.start()
        }

        override fun onPause() {
            super.onPause()
            updateHelper.stop()
        }

        override fun onUpdateProgressViews(progress: Int, total: Int) {
            lyricsView.setCurrentTimeMillis(progress.toLong())
        }

        private fun loadLRCLyrics() {
            val song = MusicPlayerRemote.currentSong
            if (LyricUtil.isLrcFileExist(song.title, song.artistName)) {
                showLyricsLocal(LyricUtil.getLocalLyricFile(song.title, song.artistName))
            }
        }

        private fun showLyricsLocal(file: File?) {
            if (file == null) {
                lyricsView.reset()
            } else {
                lyricsView.setLyricFile(file, "UTF-8")
            }
        }
    }
}