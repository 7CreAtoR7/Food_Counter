package org.tensorflow.codelabs.objectdetection

import android.annotation.SuppressLint
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.*
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.CompoundButton
import android.widget.ImageView
import android.widget.Switch
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.FileProvider
import androidx.drawerlayout.widget.DrawerLayout
import androidx.exifinterface.media.ExifInterface
import androidx.lifecycle.lifecycleScope
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.navigation.NavigationView
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.tensorflow.lite.support.image.TensorImage
import org.tensorflow.lite.task.vision.detector.Detection
import org.tensorflow.lite.task.vision.detector.ObjectDetector
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.math.max
import kotlin.math.min


class MainActivity : AppCompatActivity(), View.OnClickListener,
    NavigationView.OnNavigationItemSelectedListener {
    companion object {
        const val TAG = "TFLite - ODT"
        const val REQUEST_IMAGE_CAPTURE: Int = 1
        private const val MAX_FONT_SIZE = 96F
        var ingList: ArrayList<String> = ArrayList()
        // список, в котором будут храниться полученные
        // с фотографии, а затем полученные с сервера ингредиенты
    }

    private lateinit var captureImageFab: FloatingActionButton
    private lateinit var inputImageView: ImageView
    private lateinit var currentPhotoPath: String
    private lateinit var myPreference: MyPreference

    private var detectObjects = arrayOf("Baked Good", "Salad", "Seafood")


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        supportActionBar?.hide()

        captureImageFab = findViewById(R.id.selectPhoto)
        inputImageView = findViewById(R.id.imageView)
        val drawerLayout: DrawerLayout = findViewById(R.id.drawerLayout)

        captureImageFab.setOnClickListener(this)

        //Для загрузки темы при старте
        if (loadState()) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            setTheme(R.style.dark_theme)
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            setTheme(R.style.stock)
        }

        // Тулбар для включения бокового бара
        setSupportActionBar(toolbar)
        val toggle =
            ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open, R.string.close)
        toggle.isDrawerIndicatorEnabled = true
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        // Реакция на клик элементов в боковом меню
        nav_menu.setNavigationItemSelectedListener(this)
    }

    // Смена языка
    override fun attachBaseContext(newBase: Context?) {
        myPreference = MyPreference(newBase!!)
        val lang = myPreference.getLoginCount()
        super.attachBaseContext(MyContextWrapper.wrap(newBase, lang))
    }

    // Реакция на клик в боковом меню
    @SuppressLint("UseSwitchCompatOrMaterialCode")
    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.nav_language) {
            val intent = Intent(this, Language::class.java)
            startActivity(intent)
        }
        // Смена темы
        if (item.itemId == R.id.nav_theme) {
            item.setActionView(R.layout.theme_switch)
            val themeSwitch: Switch = item.actionView.findViewById(R.id.action_switch)
            if (loadState()) themeSwitch.isChecked = true

            themeSwitch.setOnCheckedChangeListener { _: CompoundButton, isChecked: Boolean ->
                if (isChecked) {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                    saveState(true)
                    recreate()
                } else {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                    saveState(false)
                }
            }
        }

        if (item.itemId == R.id.nav_about) {
            val intent = Intent(this, About::class.java)
            startActivity(intent)
        }
        return false
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            setViewAndDetect(getCapturedImage())
        }
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.selectPhoto -> {
                try {
                    dispatchTakePictureIntent()
                } catch (e: ActivityNotFoundException) {
                    Log.e(TAG, e.message.toString())
                }
            }
        }
    }


    private fun runObjectDetection(bitmap: Bitmap) {
        val image = TensorImage.fromBitmap(bitmap)

        val options = ObjectDetector.ObjectDetectorOptions.builder()
            .setMaxResults(10) // может распознать до 10 объектов на фото
            .setScoreThreshold(0.5f)
            .build()
        val detector = ObjectDetector.createFromFileAndOptions(
            this,
            "salad.tflite",
            options
        )

        val results = detector.detect(image)

        debugPrint(results)

        val resultToDisplay = results.map {
            val category = it.categories.first()
            val text = "${category.label}, ${category.score.times(100).toInt()}%"

            DetectionResult(it.boundingBox, text)
        }
        drawDetectionResult(bitmap, resultToDisplay)

        // после анализа фото открывается новая активность
        try {
            val newFile = File(currentPhotoPath)
            newFile.delete()
        } catch (e: Exception) {
        }

        val intent = Intent(this, Ingredients::class.java)
        intent.putExtra("IngredientsList", ingList)
        startActivity(intent)
    }

    private fun debugPrint(results: List<Detection>) { // для вывода в логе об объектах
        for (obj in results) {
            for (category in obj.categories) {
                if (category.label.toString() !in ingList && category.label.toString() !in detectObjects) {
                    ingList.add(category.label.toString())
                    Log.d(TAG, ingList.toString())
                    val confidence: Int = category.score.times(100).toInt()
                    Log.d(TAG, "    Confidence: ${confidence}%")
                }
            }
        }
    }


    private fun setViewAndDetect(bitmap: Bitmap) {
        lifecycleScope.launch(Dispatchers.Default) { runObjectDetection(bitmap) }
    }


    private fun getCapturedImage(): Bitmap {
        val targetW: Int = inputImageView.width
        val targetH: Int = inputImageView.height

        val bmOptions = BitmapFactory.Options().apply {
            inJustDecodeBounds = true

            BitmapFactory.decodeFile(currentPhotoPath, this)

            val photoW: Int = outWidth
            val photoH: Int = outHeight
            val scaleFactor: Int = max(1, min(photoW / targetW, photoH / targetH))

            inJustDecodeBounds = false
            inSampleSize = scaleFactor
            inMutable = true
        }
        val exifInterface = ExifInterface(currentPhotoPath)
        val orientation = exifInterface.getAttributeInt(
            ExifInterface.TAG_ORIENTATION,
            ExifInterface.ORIENTATION_UNDEFINED
        )

        val bitmap = BitmapFactory.decodeFile(currentPhotoPath, bmOptions)
        return when (orientation) {
            ExifInterface.ORIENTATION_ROTATE_90 -> {
                rotateImage(bitmap, 90f)
            }
            ExifInterface.ORIENTATION_ROTATE_180 -> {
                rotateImage(bitmap, 180f)
            }
            ExifInterface.ORIENTATION_ROTATE_270 -> {
                rotateImage(bitmap, 270f)
            }
            else -> {
                bitmap
            }
        }
    }


    private fun rotateImage(source: Bitmap, angle: Float): Bitmap {
        val matrix = Matrix()
        matrix.postRotate(angle)
        return Bitmap.createBitmap(
            source, 0, 0, source.width, source.height,
            matrix, true
        )
    }


    @SuppressLint("SimpleDateFormat")
    @Throws(IOException::class)
    private fun createImageFile(): File {
        // Create an image file name
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val storageDir: File? = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(
            "JPEG_${timeStamp}_",
            ".jpg",
            storageDir
        ).apply {
            currentPhotoPath = absolutePath
        }
    }

    private fun dispatchTakePictureIntent() { // открытие камеры
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
            takePictureIntent.resolveActivity(packageManager)?.also {
                val photoFile: File? = try {
                    createImageFile()
                } catch (e: IOException) {
                    Log.e(TAG, e.message.toString())
                    null
                }

                photoFile?.also {
                    val photoURI: Uri = FileProvider.getUriForFile(
                        this,
                        "org.tensorflow.codelabs.objectdetection.fileprovider",
                        it
                    )
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                    startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
                }
            }
        }
    }

    private fun drawDetectionResult(
        bitmap: Bitmap,
        detectionResults: List<DetectionResult>
    ): Bitmap {
        val outputBitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true)
        val canvas = Canvas(outputBitmap)
        val pen = Paint()
        pen.textAlign = Paint.Align.LEFT

        detectionResults.forEach {
            // draw bounding box
            pen.color = Color.RED
            pen.strokeWidth = 8F
            pen.style = Paint.Style.STROKE
            val box = it.boundingBox
            canvas.drawRect(box, pen)

            val tagSize = Rect(0, 0, 0, 0)

            // calculate the right font size
            pen.style = Paint.Style.FILL_AND_STROKE
            pen.color = Color.YELLOW
            pen.strokeWidth = 2F

            pen.textSize = MAX_FONT_SIZE
            pen.getTextBounds(it.text, 0, it.text.length, tagSize)
            val fontSize: Float = pen.textSize * box.width() / tagSize.width()

            if (fontSize < pen.textSize) pen.textSize = fontSize

            var margin = (box.width() - tagSize.width()) / 2.0F
            if (margin < 0F) margin = 0F
            canvas.drawText(
                it.text, box.left + margin,
                box.top + tagSize.height().times(1F), pen
            )
        }
        return outputBitmap
    }

    //Использование SP для сохранения темы
    private fun saveState(state: Boolean) {
        val sharedPreferences: SharedPreferences = getSharedPreferences("Theme", MODE_PRIVATE)
        val editor: SharedPreferences.Editor = sharedPreferences.edit()
        editor.putBoolean("NightMode", state)
        editor.apply()
    }

    //Использование SP для загрузки темы
    private fun loadState(): Boolean {
        val sharedPreferences: SharedPreferences = getSharedPreferences("Theme", MODE_PRIVATE)
        return sharedPreferences.getBoolean("NightMode", false)

    }
}

data class DetectionResult(val boundingBox: RectF, val text: String)
