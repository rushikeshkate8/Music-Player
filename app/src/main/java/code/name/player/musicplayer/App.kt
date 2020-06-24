package code.name.player.musicplayer

import android.content.Context
import android.widget.Toast
import androidx.multidex.MultiDexApplication
import code.name.player.appthemehelper.ThemeStore
import code.name.player.appthemehelper.util.VersionUtils
import code.name.player.musicplayer.appshortcuts.DynamicShortcutManager
import com.anjlab.android.iab.v3.BillingProcessor
import com.anjlab.android.iab.v3.TransactionDetails
import uk.co.chrisjenx.calligraphy.CalligraphyConfig


class App : MultiDexApplication() {

    lateinit var billingProcessor: BillingProcessor

    override fun onCreate() {
        super.onCreate()
        instance = this

        // default theme
        if (!ThemeStore.isConfigured(this, 3)) {
            ThemeStore.editTheme(this)
                    .accentColorRes(R.color.md_green_A200)
                    .coloredNavigationBar(true)
                    .commit()
        }

        if (VersionUtils.hasNougatMR())
            DynamicShortcutManager(this).initDynamicShortcuts()


        CalligraphyConfig.initDefault(CalligraphyConfig.Builder()
                .setDefaultFont(R.font.circular_std_book)
                .build()
        )

        // automatically restores purchases
        billingProcessor = BillingProcessor(this, BuildConfig.GOOGLE_PLAY_LICENSING_KEY,
                object : BillingProcessor.IBillingHandler {
                    override fun onProductPurchased(productId: String, details: TransactionDetails?) {}

                    override fun onPurchaseHistoryRestored() {
                        Toast.makeText(this@App, R.string.restored_previous_purchase_please_restart, Toast.LENGTH_LONG).show();
                    }

                    override fun onBillingError(errorCode: Int, error: Throwable?) {}

                    override fun onBillingInitialized() {}
                })
    }

    override fun onTerminate() {
        super.onTerminate()
        billingProcessor.release()
    }

    companion object {

        const val PRO_VERSION_PRODUCT_ID = "pro_version"

        lateinit var instance: App
            private set

        val context: Context
            get() = instance.applicationContext

        val isProVersion: Boolean
            get() = BuildConfig.DEBUG || instance.billingProcessor.isPurchased(PRO_VERSION_PRODUCT_ID)
    }
}
