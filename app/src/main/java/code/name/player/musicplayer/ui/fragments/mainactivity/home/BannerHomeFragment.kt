package code.name.player.musicplayer.ui.fragments.mainactivity.home

import android.graphics.Bitmap
import android.graphics.Color
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import code.name.player.appthemehelper.ThemeStore
import code.name.player.appthemehelper.util.TintHelper
import code.name.player.musicplayer.Constants.USER_BANNER
import code.name.player.musicplayer.Constants.USER_PROFILE
import code.name.player.musicplayer.R
import code.name.player.musicplayer.glide.GlideApp
import code.name.player.musicplayer.helper.MusicPlayerRemote
import code.name.player.musicplayer.interfaces.MainActivityFragmentCallbacks
import code.name.player.musicplayer.loaders.SongLoader
import code.name.player.musicplayer.model.Home
import code.name.player.musicplayer.model.smartplaylist.HistoryPlaylist
import code.name.player.musicplayer.model.smartplaylist.LastAddedPlaylist
import code.name.player.musicplayer.model.smartplaylist.MyTopTracksPlaylist
import code.name.player.musicplayer.mvp.contract.HomeContract
import code.name.player.musicplayer.mvp.presenter.HomePresenter
import code.name.player.musicplayer.ui.adapter.HomeAdapter
import code.name.player.musicplayer.ui.fragments.base.AbsMainActivityFragment
import code.name.player.musicplayer.util.Compressor
import code.name.player.musicplayer.util.NavigationUtil
import code.name.player.musicplayer.util.PreferenceUtil
import com.bumptech.glide.load.engine.DiskCacheStrategy
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_banner_home.*
import java.io.File
import java.util.*

class BannerHomeFragment : AbsMainActivityFragment(), MainActivityFragmentCallbacks, HomeContract.HomeView {

    val disposable: CompositeDisposable = CompositeDisposable()
    private lateinit var homePresenter: HomePresenter
    private lateinit var contentContainerView: View
    private lateinit var lastAdded: View
    private lateinit var topPlayed: View
    private lateinit var actionShuffle: View
    private lateinit var history: View
    private lateinit var userImage: ImageView
    private lateinit var toolbar: Toolbar

    override fun onCreateView(inflater: LayoutInflater, viewGroup: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(if (PreferenceUtil.getInstance().isHomeBanner) R.layout.fragment_banner_home else R.layout.fragment_home, viewGroup, false)
    }

    private val displayMetrics: DisplayMetrics
        get() {
            val display = mainActivity.windowManager.defaultDisplay
            val metrics = DisplayMetrics()
            display.getMetrics(metrics)
            return metrics
        }

    private fun getTimeOfTheDay() {
        val c = Calendar.getInstance()
        val timeOfDay = c.get(Calendar.HOUR_OF_DAY)

        var images = arrayOf<String>()
        when (timeOfDay) {
            in 0..5 -> images = resources.getStringArray(R.array.night)
            in 6..11 -> images = resources.getStringArray(R.array.morning)
            in 12..15 -> images = resources.getStringArray(R.array.after_noon)
            in 16..19 -> images = resources.getStringArray(R.array.evening)
            in 20..23 -> images = resources.getStringArray(R.array.night)
        }

        val day = images[Random().nextInt(images.size)]
        loadTimeImage(day)
    }


    private fun loadTimeImage(day: String) {
        if (bannerImage != null) {
            if (PreferenceUtil.getInstance().bannerImage.isEmpty()) {
                GlideApp.with(activity!!)
                        .load(day)
                        .placeholder(R.drawable.material_design_default)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .into(bannerImage!!)
            } else {
                disposable.add(Compressor(context!!)
                        .setQuality(100)
                        .setCompressFormat(Bitmap.CompressFormat.WEBP)
                        .compressToBitmapAsFlowable(File(PreferenceUtil.getInstance().bannerImage, USER_BANNER))
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe { bannerImage!!.setImageBitmap(it) })
            }
        }
    }

    private fun loadImageFromStorage(imageView: ImageView) {
        disposable.add(Compressor(context!!)
                .setMaxHeight(300)
                .setMaxWidth(300)
                .setQuality(75)
                .setCompressFormat(Bitmap.CompressFormat.WEBP)
                .compressToBitmapAsFlowable(File(PreferenceUtil.getInstance().profileImage, USER_PROFILE))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    imageView.setImageBitmap(it)
                }) {
                    imageView.setImageDrawable(ContextCompat.getDrawable(context!!, R.drawable.ic_person_flat))
                })
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        homePresenter = HomePresenter(this)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        toolbar = view.findViewById(R.id.toolbar)

        if (!PreferenceUtil.getInstance().isHomeBanner)
            setStatusbarColorAuto(view)

        lastAdded = view.findViewById(R.id.lastAdded)
        lastAdded.setOnClickListener {
            NavigationUtil.goToPlaylistNew(activity!!, LastAddedPlaylist(activity!!))
        }

        topPlayed = view.findViewById(R.id.topPlayed)
        topPlayed.setOnClickListener {
            NavigationUtil.goToPlaylistNew(activity!!, MyTopTracksPlaylist(activity!!))
        }

        actionShuffle = view.findViewById(R.id.actionShuffle)
        actionShuffle.setOnClickListener {
            MusicPlayerRemote.openAndShuffleQueue(SongLoader.getAllSongs(activity!!).blockingFirst(), true)
        }

        history = view.findViewById(R.id.history)
        history.setOnClickListener {
            NavigationUtil.goToPlaylistNew(activity!!, HistoryPlaylist(activity!!))
        }

        userImage = view.findViewById(R.id.userImage)
        userImage.setOnClickListener { showMainMenu() }

        homePresenter = HomePresenter(this)

        contentContainerView = view.findViewById(R.id.contentContainer)
        contentContainerView.setBackgroundColor(ThemeStore.primaryColor(context!!))

        //bannerTitle.setTextColor(ThemeStore.textColorPrimary(context!!))

        setupToolbar()
        homePresenter.subscribe()

        loadImageFromStorage(userImage)
        getTimeOfTheDay()

    }

    private fun setupToolbar() {
        toolbar.navigationIcon = TintHelper.createTintedDrawable(ContextCompat.getDrawable(context!!, R.drawable.ic_search_white_24dp), ThemeStore.textColorSecondary(context!!))
        mainActivity.title = null
        mainActivity.setSupportActionBar(toolbar)
        toolbar.setBackgroundColor(Color.TRANSPARENT)
    }

    override fun handleBackPress(): Boolean {
        return false
    }

    override fun onDestroyView() {
        super.onDestroyView()
        disposable.dispose()
        homePresenter.unsubscribe()
    }

    override fun loading() {

    }

    override fun showEmptyView() {

    }

    override fun completed() {

    }

    override fun showData(list: ArrayList<Home>) {
        recyclerView.apply {
            val homeAdapter = HomeAdapter(mainActivity, list, displayMetrics)
            layoutManager = LinearLayoutManager(mainActivity)
            adapter = homeAdapter
        }
    }

    companion object {

        const val TAG: String = "BannerHomeFragment"

        fun newInstance(): BannerHomeFragment {
            val args = Bundle()
            val fragment = BannerHomeFragment()
            fragment.arguments = args
            return fragment
        }
    }
}