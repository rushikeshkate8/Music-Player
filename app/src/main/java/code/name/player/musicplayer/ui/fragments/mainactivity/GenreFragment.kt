package code.name.player.musicplayer.ui.fragments.mainactivity

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import androidx.recyclerview.widget.LinearLayoutManager
import code.name.player.musicplayer.R
import code.name.player.musicplayer.model.Genre
import code.name.player.musicplayer.mvp.contract.GenreContract
import code.name.player.musicplayer.mvp.presenter.GenrePresenter
import code.name.player.musicplayer.ui.adapter.GenreAdapter
import code.name.player.musicplayer.ui.fragments.base.AbsLibraryPagerRecyclerViewFragment
import code.name.player.musicplayer.util.PreferenceUtil
import java.util.*

class GenreFragment : AbsLibraryPagerRecyclerViewFragment<GenreAdapter, LinearLayoutManager>(), GenreContract.GenreView {

    private var mPresenter: GenrePresenter? = null

    override val emptyMessage: Int
        get() = R.string.no_genres

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        mPresenter = GenrePresenter(this)
    }

    override fun setMenuVisibility(menuVisible: Boolean) {
        super.setMenuVisibility(menuVisible)
        if (menuVisible) {
            libraryFragment.setTitle(if (PreferenceUtil.getInstance().tabTitles()) R.string.library else R.string.genres)
        }
    }

    override fun onResume() {
        super.onResume()
        libraryFragment.setTitle(if (PreferenceUtil.getInstance().tabTitles()) R.string.library else R.string.genres)
        if (adapter!!.dataSet.isEmpty()) {
            mPresenter!!.subscribe()
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        mPresenter!!.unsubscribe()
    }

    override fun createLayoutManager(): LinearLayoutManager {
        return LinearLayoutManager(activity)
    }

    override fun createAdapter(): GenreAdapter {
        val dataSet = adapter!!.dataSet
        return GenreAdapter(libraryFragment.mainActivity, dataSet, R.layout.item_list)
    }

    override fun loading() {

    }

    override fun showData(list: ArrayList<Genre>) {
        adapter!!.swapDataSet(list)
    }

    override fun showEmptyView() {
        adapter!!.swapDataSet(ArrayList())
    }

    override fun completed() {

    }


    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        super.onCreateOptionsMenu(menu, inflater)
        menu!!.removeItem(R.id.action_sort_order)
        menu.removeItem(R.id.action_grid_size)
        menu.removeItem(R.id.action_new_playlist)
    }

    companion object {

        fun newInstance(): GenreFragment {
            val args = Bundle()
            val fragment = GenreFragment()
            fragment.arguments = args
            return fragment
        }
    }
}
