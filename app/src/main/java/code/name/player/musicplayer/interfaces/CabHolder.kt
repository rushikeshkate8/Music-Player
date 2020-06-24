package code.name.player.musicplayer.interfaces

import com.afollestad.materialcab.MaterialCab


interface CabHolder {

    fun openCab(menuRes: Int, callback: MaterialCab.Callback): MaterialCab
}
