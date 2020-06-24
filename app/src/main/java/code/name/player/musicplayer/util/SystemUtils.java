package code.name.player.musicplayer.util;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.util.DisplayMetrics;
import android.view.ViewGroup;

import code.name.player.musicplayer.App;

public class SystemUtils {

    private static final String STATUS_BAR_HEIGHT_RES_NAME = "status_bar_height";
    private static final String NAV_BAR_HEIGHT_RES_NAME = "navigation_bar_height";
    private static final String NAV_BAR_HEIGHT_LANDSCAPE_RES_NAME = "navigation_bar_height_landscape";
    private static final String NAV_BAR_WIDTH_RES_NAME = "navigation_bar_width";
    private static final String SHOW_NAV_BAR_RES_NAME = "config_showNavigationBar";
    private final float mSmallestWidthDp;
    private final boolean mInPortrait;

    private Activity activity;

    public SystemUtils(Activity activity) {
        this.activity = activity;
        Resources resources = activity.getResources();
        mSmallestWidthDp = getSmallestWidthDp(activity);
        mInPortrait = (resources.getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT);
    }

    private static boolean hasNavBar(Resources resources) {
        int id = resources.getIdentifier(SHOW_NAV_BAR_RES_NAME, "bool", "android");
        if (id > 0)
            return resources.getBoolean(id);
        else
            return false;
    }

    public static int getNavigationBarHeight() {
        int result = 0;
        int resourceId = App.Companion.getContext().getResources().getIdentifier("navigation_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = App.Companion.getContext().getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    public int getComboHeight() {
        if (isNavigationAtBottom()) {
            return getNavigationBarWidth();
        } else {
            return getNavigationBarHeight();
        }
    }

    public int getNavigationBarWidth() {
        Resources res = activity.getResources();
        int result = 0;
        if (hasNavBar(activity.getResources())) {
            if (!isNavigationAtBottom())
                return getInternalDimensionSize(res, NAV_BAR_WIDTH_RES_NAME);
        }
        return result;
    }

    public void addPadding(ViewGroup viewGroup) {
        Context context = viewGroup.getContext();
        Resources resources = context.getResources();
        ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) viewGroup.getLayoutParams();
        if (isNavigationAtBottom()) {
            params.leftMargin = getNavigationBarWidth();
            params.rightMargin = getNavigationBarWidth();
        } else {
            params.bottomMargin = getNavigationBarHeight();
        }
    }

    private int getInternalDimensionSize(Resources res, String key) {
        int result = 0;
        int resourceId = res.getIdentifier(key, "dimen", "android");
        if (resourceId > 0) {
            result = res.getDimensionPixelSize(resourceId);
        }
        return result;
    }

    private float getSmallestWidthDp(Activity activity) {
        DisplayMetrics metrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getRealMetrics(metrics);
        float widthDp = metrics.widthPixels / metrics.density;
        float heightDp = metrics.heightPixels / metrics.density;
        return Math.min(widthDp, heightDp);
    }

    boolean isNavigationAtBottom() {
        return (mSmallestWidthDp >= 600 || mInPortrait);
    }

}
