package code.name.player.musicplayer.ui.activities

import android.graphics.Color
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.LinearLayout
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import code.name.player.appthemehelper.ThemeStore
import code.name.player.appthemehelper.util.ColorUtil
import code.name.player.appthemehelper.util.ToolbarContentTintHelper
import code.name.player.musicplayer.R
import code.name.player.musicplayer.helper.MusicPlayerRemote
import code.name.player.musicplayer.helper.menu.PlaylistMenuHelper
import code.name.player.musicplayer.interfaces.CabHolder
import code.name.player.musicplayer.loaders.PlaylistLoader
import code.name.player.musicplayer.model.AbsCustomPlaylist
import code.name.player.musicplayer.model.Playlist
import code.name.player.musicplayer.model.Song
import code.name.player.musicplayer.mvp.contract.PlaylistSongsContract
import code.name.player.musicplayer.mvp.presenter.PlaylistSongsPresenter
import code.name.player.musicplayer.ui.activities.base.AbsSlidingMusicPanelActivity
import code.name.player.musicplayer.ui.adapter.song.OrderablePlaylistSongAdapter
import code.name.player.musicplayer.ui.adapter.song.PlaylistSongAdapter
import code.name.player.musicplayer.ui.adapter.song.SongAdapter
import code.name.player.musicplayer.util.PlaylistsUtil
import code.name.player.musicplayer.util.RetroColorUtil
import code.name.player.musicplayer.util.ViewUtil
import com.afollestad.materialcab.MaterialCab
import com.facebook.ads.*
import com.h6ah4i.android.widget.advrecyclerview.animator.RefactoredDefaultItemAnimator
import com.h6ah4i.android.widget.advrecyclerview.draggable.RecyclerViewDragDropManager
import com.h6ah4i.android.widget.advrecyclerview.utils.WrapperAdapterUtils
import kotlinx.android.synthetic.main.activity_playlist_detail.*
import java.util.*


class PlaylistDetailActivity : AbsSlidingMusicPanelActivity(), CabHolder, PlaylistSongsContract.PlaylistSongsView {
    private var playlist: Playlist? = null
    private var adView: AdView? = null
    private var cab: MaterialCab? = null
    private lateinit var adapter: SongAdapter
    private var wrappedAdapter: RecyclerView.Adapter<*>? = null
    private var recyclerViewDragDropManager: RecyclerViewDragDropManager? = null
    private var songsPresenter: PlaylistSongsPresenter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        setDrawUnderStatusBar()
        super.onCreate(savedInstanceState)
        setStatusbarColor(Color.TRANSPARENT)
        setNavigationbarColorAuto()
        setTaskDescriptionColorAuto()
        setLightNavigationBar(true)
        setLightStatusbar(ColorUtil.isColorLight(ThemeStore.primaryColor(this)))
        toggleBottomNavigationView(true)
        AudienceNetworkAds.initialize(this)
        // Instantiate an AdView object.
        // NOTE: The placement ID from the Facebook Monetization Manager identifies your App.
        // To get test ads, add IMG_16_9_APP_INSTALL# to your placement id. Remove this when your app is ready to serve real ads.


        // Instantiate an AdView object.
        // NOTE: The placement ID from the Facebook Monetization Manager identifies your App.
        // To get test ads, add IMG_16_9_APP_INSTALL# to your placement id. Remove this when your app is ready to serve real ads.
        adView = AdView(this, "266586284404690_267246107672041", AdSize.BANNER_HEIGHT_50)
        // Find the Ad Container
        val adContainer = findViewById<View>(R.id.playlist_banner_ad) as LinearLayout

        // Add the ad view to your activity layout
        adContainer.addView(adView)
        // Request an ad
        adView!!.loadAd()
        playlist = intent.extras!!.getParcelable(EXTRA_PLAYLIST)
        songsPresenter = PlaylistSongsPresenter(this, playlist!!)

        setUpToolBar()
        setUpRecyclerView()
    }

    override fun createContentView(): View {
        return wrapSlidingMusicPanel(R.layout.activity_playlist_detail)
    }

    private fun setUpRecyclerView() {
        ViewUtil.setUpFastScrollRecyclerViewColor(this, recyclerView, ThemeStore.accentColor(this))
        recyclerView.layoutManager = LinearLayoutManager(this)
        if (playlist is AbsCustomPlaylist) {
            adapter = PlaylistSongAdapter(this, ArrayList(), R.layout.item_list, false, this)
            recyclerView!!.adapter = adapter
        } else {
            recyclerViewDragDropManager = RecyclerViewDragDropManager()
            val animator = RefactoredDefaultItemAnimator()
            adapter = OrderablePlaylistSongAdapter(this, ArrayList(), R.layout.item_list, false, this,
                    object : OrderablePlaylistSongAdapter.OnMoveItemListener {
                        override fun onMoveItem(fromPosition: Int, toPosition: Int) {
                            if (PlaylistsUtil.moveItem(this@PlaylistDetailActivity, playlist!!.id, fromPosition, toPosition)) {
                                val song = adapter.dataSet.removeAt(fromPosition)
                                adapter.dataSet.add(toPosition, song)
                                adapter.notifyItemMoved(fromPosition, toPosition)
                            }
                        }
                    })
            wrappedAdapter = recyclerViewDragDropManager!!.createWrappedAdapter(adapter)

            recyclerView.adapter = wrappedAdapter
            recyclerView.itemAnimator = animator

            recyclerViewDragDropManager!!.attachRecyclerView(recyclerView!!)
        }
        adapter.registerAdapterDataObserver(object : RecyclerView.AdapterDataObserver() {
            override fun onChanged() {
                super.onChanged()
                checkIsEmpty()
            }
        })
        recyclerView!!.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (dy > 0) {
                    actionShuffle.setShowTitle(false)
                } else if (dy < 0) {
                    actionShuffle.setShowTitle(true)
                }
            }
        })
        actionShuffle.setOnClickListener {
            if (adapter.dataSet.isEmpty()) {
                return@setOnClickListener
            }
            MusicPlayerRemote.openAndShuffleQueue(adapter.dataSet, true)
        }
    }

    override fun onResume() {
        super.onResume()
        songsPresenter!!.subscribe()
    }

    private fun setUpToolBar() {
        bannerTitle.text = playlist!!.name
        bannerTitle.setTextColor(ThemeStore.textColorPrimary(this))
        actionShuffle.setColor(ThemeStore.accentColor(this))

        val primaryColor = ThemeStore.primaryColor(this)
        toolbar!!.apply {
            setBackgroundColor(primaryColor)
            setSupportActionBar(toolbar)
            setNavigationIcon(R.drawable.ic_keyboard_backspace_black_24dp)
            ToolbarContentTintHelper.colorBackButton(this, ThemeStore.textColorSecondary(this@PlaylistDetailActivity))
        }
        title = null
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(if (playlist is AbsCustomPlaylist) R.menu.menu_smart_playlist_detail else R.menu.menu_playlist_detail, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        when (id) {
            android.R.id.home -> {
                onBackPressed()
                return true
            }
        }
        return PlaylistMenuHelper.handleMenuClick(this, playlist!!, item)
    }

    override fun openCab(menuRes: Int, callback: MaterialCab.Callback): MaterialCab {
        if (cab != null && cab!!.isActive) {
            cab!!.finish()
        }
        cab = MaterialCab(this, R.id.cab_stub)
                .setMenu(menuRes)
                .setCloseDrawableRes(R.drawable.ic_close_white_24dp)
                .setBackgroundColor(
                        RetroColorUtil.shiftBackgroundColorForLightText(ThemeStore.primaryColor(this)))
                .start(callback)
        return cab!!
    }

    override fun onBackPressed() {
        if (cab != null && cab!!.isActive) {
            cab!!.finish()
        } else {
            recyclerView!!.stopScroll()
            super.onBackPressed()
        }
    }

    override fun onMediaStoreChanged() {
        super.onMediaStoreChanged()

        if (playlist !is AbsCustomPlaylist) {
            // Playlist deleted
            if (!PlaylistsUtil.doesPlaylistExist(this, playlist!!.id)) {
                finish()
                return
            }

            // Playlist renamed
            val playlistName = PlaylistsUtil.getNameForPlaylist(this, playlist!!.id.toLong())
            if (playlistName != playlist!!.name) {
                playlist = PlaylistLoader.getPlaylist(this, playlist!!.id).blockingFirst()
                setToolbarTitle(playlist!!.name)
            }
        }
        songsPresenter!!.subscribe()
    }

    private fun setToolbarTitle(title: String) {
        supportActionBar!!.title = title
    }

    private fun checkIsEmpty() {
        empty.visibility = if (adapter.itemCount == 0) View.VISIBLE else View.GONE
        emptyText.visibility = if (adapter.itemCount == 0) View.VISIBLE else View.GONE
    }

    public override fun onPause() {
        if (recyclerViewDragDropManager != null) {
            recyclerViewDragDropManager!!.cancelDrag()
        }
        super.onPause()
        songsPresenter!!.unsubscribe()
    }

    override fun onDestroy() {
        adView?.destroy()
        if (recyclerViewDragDropManager != null) {
            recyclerViewDragDropManager!!.release()
            recyclerViewDragDropManager = null
        }

        if (recyclerView != null) {
            recyclerView!!.itemAnimator = null
            recyclerView!!.adapter = null
        }

        if (wrappedAdapter != null) {
            WrapperAdapterUtils.releaseAll(wrappedAdapter)
            wrappedAdapter = null
        }
        super.onDestroy()
    }

    override fun loading() {}

    override fun showEmptyView() {
        empty.visibility = View.VISIBLE
        emptyText.visibility = View.VISIBLE
    }

    override fun completed() {}

    override fun showData(list: ArrayList<Song>) {
        adapter.swapDataSet(list)
    }

    companion object {
        var EXTRA_PLAYLIST = "extra_playlist"
    }
}
