package code.name.player.musicplayer.ui.activities.tageditor

import android.app.Activity
import android.app.SearchManager
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.view.animation.OvershootInterpolator
import code.name.player.appthemehelper.ThemeStore
import code.name.player.appthemehelper.util.TintHelper
import code.name.player.musicplayer.R
import code.name.player.musicplayer.ui.activities.base.AbsBaseActivity
import code.name.player.musicplayer.util.RetroUtil
import com.afollestad.materialdialogs.MaterialDialog
import kotlinx.android.synthetic.main.activity_album_tag_editor.*
import org.jaudiotagger.audio.AudioFile
import org.jaudiotagger.audio.AudioFileIO
import org.jaudiotagger.tag.FieldKey
import java.io.File

abstract class AbsTagEditorActivity : AbsBaseActivity() {


    private lateinit var items: Array<CharSequence>
    protected var id: Int = 0
        private set
    private var paletteColorPrimary: Int = 0
    private var isInNoImageMode: Boolean = false
    private var songPaths: List<String>? = null

    protected val show: MaterialDialog
        get() = MaterialDialog.Builder(this@AbsTagEditorActivity)
                .title(R.string.update_image)
                .items(*items)
                .itemsCallback { _, _, position, _ ->
                    when (position) {
                        0 -> getImageFromLastFM()
                        1 -> startImagePicker()
                        2 -> searchImageOnWeb()
                        3 -> deleteImage()
                    }
                }.show()

    protected abstract val contentViewLayout: Int

    internal val albumArtist: String?
        get() {
            return try {
                getAudioFile(songPaths!![0]).tagOrCreateAndSetDefault
                        .getFirst(FieldKey.ALBUM_ARTIST)
            } catch (ignored: Exception) {
                null
            }

        }

    protected val songTitle: String?
        get() {
            return try {
                getAudioFile(songPaths!![0]).tagOrCreateAndSetDefault.getFirst(FieldKey.TITLE)
            } catch (ignored: Exception) {
                null
            }

        }

    protected val albumTitle: String?
        get() {
            return try {
                getAudioFile(songPaths!![0]).tagOrCreateAndSetDefault.getFirst(FieldKey.ALBUM)
            } catch (ignored: Exception) {
                null
            }

        }

    protected val artistName: String?
        get() {
            return try {
                getAudioFile(songPaths!![0]).tagOrCreateAndSetDefault.getFirst(FieldKey.ARTIST)
            } catch (ignored: Exception) {
                null
            }

        }

    protected val albumArtistName: String?
        get() {
            return try {
                getAudioFile(songPaths!![0]).tagOrCreateAndSetDefault
                        .getFirst(FieldKey.ALBUM_ARTIST)
            } catch (ignored: Exception) {
                null
            }

        }

    protected val genreName: String?
        get() {
            return try {
                getAudioFile(songPaths!![0]).tagOrCreateAndSetDefault.getFirst(FieldKey.GENRE)
            } catch (ignored: Exception) {
                null
            }

        }

    protected val songYear: String?
        get() {
            return try {
                getAudioFile(songPaths!![0]).tagOrCreateAndSetDefault.getFirst(FieldKey.YEAR)
            } catch (ignored: Exception) {
                null
            }

        }

    protected val trackNumber: String?
        get() {
            return try {
                getAudioFile(songPaths!![0]).tagOrCreateAndSetDefault.getFirst(FieldKey.TRACK)
            } catch (ignored: Exception) {
                null
            }

        }

    protected val lyrics: String?
        get() {
            return try {
                getAudioFile(songPaths!![0]).tagOrCreateAndSetDefault.getFirst(FieldKey.LYRICS)
            } catch (ignored: Exception) {
                null
            }

        }


    protected val albumArt: Bitmap?
        get() {
            try {
                val artworkTag = getAudioFile(songPaths!![0]).tagOrCreateAndSetDefault
                        .firstArtwork
                if (artworkTag != null) {
                    val artworkBinaryData = artworkTag.binaryData
                    return BitmapFactory.decodeByteArray(artworkBinaryData, 0, artworkBinaryData.size)
                }
                return null
            } catch (ignored: Exception) {
                return null
            }

        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(contentViewLayout)


        getIntentExtras()

        songPaths = getSongPaths()
        if (songPaths!!.isEmpty()) {
            finish()
            return
        }

        setUpViews()


        setNavigationbarColorAuto()
        setTaskDescriptionColorAuto()
    }

    private fun setUpViews() {
        setUpScrollView()
        setUpFab()
        setUpImageView()
    }

    private fun setUpScrollView() {
        //observableScrollView.setScrollViewCallbacks(observableScrollViewCallbacks);
    }

    private fun setUpImageView() {
        loadCurrentImage()
        items = arrayOf(getString(R.string.download_from_last_fm), getString(R.string.pick_from_local_storage), getString(R.string.web_search), getString(R.string.remove_cover))
        editorImage.setOnClickListener { show }
    }

    private fun startImagePicker() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "image/*"
        startActivityForResult(
                Intent.createChooser(intent, getString(R.string.pick_from_local_storage)),
                REQUEST_CODE_SELECT_IMAGE)
    }

    protected abstract fun loadCurrentImage()

    protected abstract fun getImageFromLastFM()

    protected abstract fun searchImageOnWeb()

    protected abstract fun deleteImage()

    private fun setUpFab() {
        saveFab.apply {
            scaleX = 0f
            scaleY = 0f
            isEnabled = false
            setOnClickListener { save() }
            TintHelper.setTintAuto(this, ThemeStore.accentColor(this@AbsTagEditorActivity), true)
        }
    }

    protected abstract fun save()

    private fun getIntentExtras() {
        val intentExtras = intent.extras
        if (intentExtras != null) {
            id = intentExtras.getInt(EXTRA_ID)
        }
    }

    protected abstract fun getSongPaths(): List<String>

    protected fun searchWebFor(vararg keys: String) {
        val stringBuilder = StringBuilder()
        for (key in keys) {
            stringBuilder.append(key)
            stringBuilder.append(" ")
        }
        val intent = Intent(Intent.ACTION_WEB_SEARCH)
        intent.putExtra(SearchManager.QUERY, stringBuilder.toString())
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)

        startActivity(intent)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        when (id) {
            android.R.id.home -> {
                super.onBackPressed()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    protected fun setNoImageMode() {
        isInNoImageMode = true
        imageContainer!!.visibility = View.GONE
        editorImage.visibility = View.GONE
        editorImage.isEnabled = false

        setColors(intent.getIntExtra(EXTRA_PALETTE, ThemeStore.primaryColor(this)))
    }

    protected fun dataChanged() {
        showFab()
    }

    private fun showFab() {
        saveFab.animate()
                .setDuration(500)
                .setInterpolator(OvershootInterpolator())
                .scaleX(1f)
                .scaleY(1f)
                .start()
        saveFab.isEnabled = true
    }

    protected fun setImageBitmap(bitmap: Bitmap?, bgColor: Int) {
        if (bitmap == null) {
            editorImage.setImageResource(R.drawable.default_album_art)
        } else {
            editorImage.setImageBitmap(bitmap)
        }
        setColors(bgColor)
    }

    protected open fun setColors(color: Int) {
        paletteColorPrimary = color
    }

    protected fun writeValuesToFiles(fieldKeyValueMap: Map<FieldKey, String>,
                                     artworkInfo: ArtworkInfo?) {
        RetroUtil.hideSoftKeyboard(this)

        WriteTagsAsyncTask(this)
                .execute(WriteTagsAsyncTask.LoadingInfo(getSongPaths(), fieldKeyValueMap, artworkInfo))
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            REQUEST_CODE_SELECT_IMAGE -> if (resultCode == Activity.RESULT_OK) {
                val selectedImage = data!!.data
                loadImageFromFile(selectedImage)
            }
        }
    }

    protected abstract fun loadImageFromFile(selectedFile: Uri?)

    private fun getAudioFile(path: String): AudioFile {
        try {
            return AudioFileIO.read(File(path))
        } catch (e: Exception) {
            Log.e(TAG, "Could not read audio file $path", e)
            return AudioFile()
        }

    }

    class ArtworkInfo constructor(val albumId: Int, val artwork: Bitmap?)

    companion object {

        const val EXTRA_ID = "extra_id"
        const val EXTRA_PALETTE = "extra_palette"
        private val TAG = AbsTagEditorActivity::class.java.simpleName
        private const val REQUEST_CODE_SELECT_IMAGE = 1000
    }
}
