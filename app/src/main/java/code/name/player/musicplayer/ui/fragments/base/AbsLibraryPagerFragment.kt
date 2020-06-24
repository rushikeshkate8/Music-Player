package code.name.player.musicplayer.ui.fragments.base

import android.os.Bundle
import code.name.player.musicplayer.ui.fragments.mainactivity.LibraryFragment

open class AbsLibraryPagerFragment : AbsMusicServiceFragment() {


    val libraryFragment: LibraryFragment
        get() = parentFragment as LibraryFragment

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setHasOptionsMenu(true)
    }
}
