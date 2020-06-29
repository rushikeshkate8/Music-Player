package code.name.player.musicplayer.ui.activitiesimport android.content.ActivityNotFoundExceptionimport android.content.Intentimport android.net.Uriimport android.os.Bundleimport androidx.appcompat.app.AppCompatActivityimport kotlinx.android.synthetic.main.activity_error_handler.*class ErrorHandlerActivity : AppCompatActivity() {    override fun onCreate(savedInstanceState: Bundle?) {        super.onCreate(savedInstanceState)        setContentView(code.name.player.musicplayer.R.layout.activity_error_handler)        clearAppData.setOnClickListener {            try {                val intent = Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS)                intent.data = Uri.parse("package:$packageName")                startActivity(intent)            } catch (e: ActivityNotFoundException) {                val intent = Intent(android.provider.Settings.ACTION_MANAGE_APPLICATIONS_SETTINGS)                startActivity(intent)            }        }        showCrashError.text = String.format("%s", intent.getStringExtra("error"))    }}