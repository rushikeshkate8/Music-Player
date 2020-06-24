package code.name.player.musicplayer.views

import android.content.Context
import android.content.res.ColorStateList
import android.util.AttributeSet
import code.name.player.appthemehelper.ThemeStore
import code.name.player.appthemehelper.util.ATHUtil
import code.name.player.appthemehelper.util.ColorUtil
import code.name.player.appthemehelper.util.MaterialValueHelper
import code.name.player.musicplayer.R
import code.name.player.musicplayer.util.RetroUtil
import com.google.android.material.button.MaterialButton

class MaterialButtonTextColor @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = -1) : MaterialButton(context, attrs, defStyleAttr) {

    init {
        setTextColor(MaterialValueHelper.getPrimaryTextColor(getContext(), ColorUtil.isColorLight(ThemeStore.primaryColor(getContext()))))
        iconTint = ColorStateList.valueOf(ATHUtil.resolveColor(context, R.attr.iconColor))
        iconPadding = RetroUtil.convertDpToPixel(16f, getContext()).toInt()
        rippleColor = ColorStateList.valueOf(ColorUtil.withAlpha(ThemeStore.accentColor(context), 0.4f))
        minHeight = RetroUtil.convertDpToPixel(52f, context).toInt()
    }
}
