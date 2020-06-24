package code.name.player.appthemehelper.common

import androidx.appcompat.widget.Toolbar

import code.name.player.appthemehelper.util.ToolbarContentTintHelper

class ATHActionBarActivity : ATHToolbarActivity() {

    override fun getATHToolbar(): Toolbar? {
        return ToolbarContentTintHelper.getSupportActionBarView(supportActionBar)
    }
}
