package code.name.player.musicplayer.ui.fragments.mainactivity

import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import android.view.Menu
import android.view.MenuInflater

import java.util.ArrayList

import code.name.player.musicplayer.R
import code.name.player.musicplayer.model.Playlist
import code.name.player.musicplayer.mvp.contract.PlaylistContract
import code.name.player.musicplayer.mvp.presenter.PlaylistPresenter
import code.name.player.musicplayer.ui.adapter.playlist.PlaylistAdapter
import code.name.player.musicplayer.ui.fragments.base.AbsLibraryPagerRecyclerViewFragment
import code.name.player.musicplayer.util.PreferenceUtil


class PlaylistsFragment : AbsLibraryPagerRecyclerViewFragment<PlaylistAdapter, LinearLayoutManager>(), PlaylistContract.PlaylistView {

    private var presenter: PlaylistPresenter? = null

    override val emptyMessage: Int
        get() = R.string.no_playlists

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        presenter = PlaylistPresenter(this)
    }

    override fun createLayoutManager(): LinearLayoutManager {
        return LinearLayoutManager(activity)
    }

    override fun createAdapter(): PlaylistAdapter {
        return PlaylistAdapter(libraryFragment.mainActivity, ArrayList(),
                R.layout.item_list, libraryFragment)
    }

    override fun setMenuVisibility(menuVisible: Boolean) {
        super.setMenuVisibility(menuVisible)
        if (menuVisible) {
            libraryFragment.setTitle(if (PreferenceUtil.getInstance().tabTitles()) R.string.library else R.string.playlists)
        }
    }

    override fun onResume() {
        super.onResume()
        libraryFragment.setTitle(if (PreferenceUtil.getInstance().tabTitles()) R.string.library else R.string.playlists)
        if (adapter!!.dataSet.isEmpty()) {
            presenter!!.subscribe()
        }
    }

    override fun onDestroy() {
        presenter!!.unsubscribe()
        super.onDestroy()
    }

    override fun onMediaStoreChanged() {
        super.onMediaStoreChanged()
        presenter!!.loadPlaylists()
    }

    override fun loading() {

    }

    override fun showEmptyView() {
        adapter!!.swapDataSet(ArrayList())
    }

    override fun completed() {

    }

    override fun showData(list: ArrayList<Playlist>) {
        adapter!!.swapDataSet(list)
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        super.onCreateOptionsMenu(menu, inflater)
        menu!!.removeItem(R.id.action_shuffle_all)
        menu.removeItem(R.id.action_sort_order)
        menu.removeItem(R.id.action_grid_size)
    }

    companion object {

        fun newInstance(): PlaylistsFragment {
            val args = Bundle()
            val fragment = PlaylistsFragment()
            fragment.arguments = args
            return fragment
        }
    }
}
