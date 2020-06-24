package code.name.player.musicplayer.views;

import android.content.Context;
import android.util.AttributeSet;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import code.name.player.appthemehelper.ThemeStore;
import code.name.player.appthemehelper.util.ATHUtil;
import code.name.player.appthemehelper.util.ColorUtil;
import code.name.player.appthemehelper.util.NavigationViewUtil;
import code.name.player.musicplayer.R;
import code.name.player.musicplayer.util.PreferenceUtil;

public class BottomNavigationBarTinted extends BottomNavigationView {
    public BottomNavigationBarTinted(Context context) {
        this(context, null);
    }

    public BottomNavigationBarTinted(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BottomNavigationBarTinted(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setLabelVisibilityMode(PreferenceUtil.getInstance().getTabTitleMode());
        setBackgroundColor(ThemeStore.Companion.primaryColor(context));
        setSelectedItemId(PreferenceUtil.getInstance().getLastPage());

        int iconColor = ATHUtil.INSTANCE.resolveColor(context, R.attr.iconColor);
        int accentColor = ThemeStore.Companion.accentColor(context);
        NavigationViewUtil.INSTANCE.setItemIconColors(this, ColorUtil.INSTANCE.withAlpha(iconColor, 0.5f), accentColor);
        NavigationViewUtil.INSTANCE.setItemTextColors(this, ColorUtil.INSTANCE.withAlpha(iconColor, 0.5f), accentColor);

    }
}
