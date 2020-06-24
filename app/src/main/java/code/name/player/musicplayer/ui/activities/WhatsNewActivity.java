package code.name.player.musicplayer.ui.activities;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.webkit.WebView;
import android.widget.TextView;

import com.afollestad.materialdialogs.internal.ThemeSingleton;
import com.google.android.material.appbar.AppBarLayout;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import code.name.player.appthemehelper.ThemeStore;
import code.name.player.appthemehelper.util.ColorUtil;
import code.name.player.appthemehelper.util.ToolbarContentTintHelper;
import code.name.player.musicplayer.R;
import code.name.player.musicplayer.ui.activities.base.AbsBaseActivity;
import code.name.player.musicplayer.util.PreferenceUtil;

public class WhatsNewActivity extends AbsBaseActivity {
    WebView webView;
    TextView title;
    Toolbar toolbar;
    AppBarLayout appBarLayout;


    private static void setChangelogRead(@NonNull Context context) {
        try {
            PackageInfo pInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            int currentVersion = pInfo.versionCode;
            PreferenceUtil.getInstance().setLastChangeLogVersion(currentVersion);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }

    private static String colorToHex(int color) {
        return Integer.toHexString(color).substring(2);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_whats_new);

        webView = findViewById(R.id.webView);
        title = findViewById(R.id.bannerTitle);
        toolbar = findViewById(R.id.toolbar);
        appBarLayout= findViewById(R.id.appBarLayout);


        setStatusbarColorAuto();
        setNavigationbarColorAuto();
        setTaskDescriptionColorAuto();

        toolbar.setBackgroundColor(ThemeStore.Companion.primaryColor(this));
        appBarLayout.setBackgroundColor(ThemeStore.Companion.primaryColor(this));
        setSupportActionBar(toolbar);
        setTitle(null);
        toolbar.setNavigationOnClickListener(v -> onBackPressed());
        title.setTextColor(ThemeStore.Companion.textColorPrimary(this));
        ToolbarContentTintHelper.colorBackButton(toolbar, ThemeStore.Companion.accentColor(this));

        try {
            // Load from phonograph-changelog.html in the assets folder
            StringBuilder buf = new StringBuilder();
            InputStream json = getAssets().open("retro-changelog.html");
            BufferedReader in = new BufferedReader(new InputStreamReader(json, "UTF-8"));
            String str;
            while ((str = in.readLine()) != null)
                buf.append(str);
            in.close();

            // Inject color values for WebView body background and links
            final String backgroundColor = colorToHex(ThemeStore.Companion.primaryColor(this));
            final String contentColor = ThemeSingleton.get().darkTheme ? "#ffffff" : "#000000";
            webView.loadData(buf.toString()
                            .replace("{style-placeholder}",
                                    String.format("body { background-color: %s; color: %s; }", backgroundColor, contentColor))
                            .replace("{link-color}", colorToHex(ThemeSingleton.get().positiveColor.getDefaultColor()))
                            .replace("{link-color-active}", colorToHex(ColorUtil.INSTANCE.lightenColor(ThemeSingleton.get().positiveColor.getDefaultColor())))
                    , "text/html", "UTF-8");
        } catch (Throwable e) {
            webView.loadData("<h1>Unable to load</h1><p>" + e.getLocalizedMessage() + "</p>", "text/html", "UTF-8");
        }
        setChangelogRead(this);
    }
}