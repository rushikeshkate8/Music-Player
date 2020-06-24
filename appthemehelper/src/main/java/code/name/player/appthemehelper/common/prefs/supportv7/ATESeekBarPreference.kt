package code.name.player.appthemehelper.common.prefs.supportv7

import android.content.Context
import android.util.AttributeSet
import android.widget.SeekBar
import androidx.preference.PreferenceViewHolder
import androidx.preference.SeekBarPreference
import code.name.player.appthemehelper.R
import code.name.player.appthemehelper.ThemeStore
import code.name.player.appthemehelper.util.TintHelper

class ATESeekBarPreference : SeekBarPreference {
    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int, defStyleRes: Int) : super(context, attrs, defStyleAttr, defStyleRes) {
        init()
    }

    private fun init() {

    }

    override fun onBindViewHolder(view: PreferenceViewHolder) {
        super.onBindViewHolder(view)
        val seekBar = view.findViewById(R.id.seekbar) as SeekBar
        TintHelper.setTintAuto(seekBar, ThemeStore.accentColor(context), true)
    }
}
