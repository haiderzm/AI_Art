package com.example.aiart

import android.content.ContentValues
import android.graphics.Bitmap
import android.graphics.Matrix
import android.net.Uri
import android.os.*
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.aiart.ml.LiteModelCartoonganInt81
import glimpse.core.Glimpse
import glimpse.core.crop
import glimpse.core.findCenter
import org.tensorflow.lite.support.image.TensorImage
import java.io.*
import java.lang.ref.WeakReference

class CartoonActivity:AppCompatActivity() {

    lateinit var contentBitmap: Bitmap
    lateinit var styleImage: ImageView
    lateinit var model: LiteModelCartoonganInt81
    lateinit var progressBar: ProgressBar
    lateinit var msg: TextView
    lateinit var finalStyle: Bitmap
    var isGenerated = false

    companion object {
        class MyAsyncTask internal constructor(context: CartoonActivity) : AsyncTask<Int, String, String?>() {

            private var resp: String? = null
            private val activityReference: WeakReference<CartoonActivity> = WeakReference(context)

            override fun onPreExecute() {
                val activity = activityReference.get()
                if (activity == null || activity.isFinishing) return
                activity.progressBar.visibility = View.VISIBLE
                activity.msg.visibility = View.INVISIBLE
            }

            override fun doInBackground(vararg params: Int?): String? {
                val activity = activityReference.get()
                if (activity == null || activity.isFinishing) return "Executed"
                val sourceImage = TensorImage.fromBitmap(activity.contentBitmap)

// Runs model inference and gets result.
                val outputs = activity.model.process(sourceImage)
                val cartoonizedImage = outputs.cartoonizedImageAsTensorImage
                val cartoonizedImageBitmap = cartoonizedImage.bitmap

                activity.finalStyle = cartoonizedImageBitmap
// Releases model resources if no longer used.
                activity.model.close()
                return "Executed"
            }


            override fun onPostExecute(result: String?) {

                val activity = activityReference.get()
                if (activity == null || activity.isFinishing) return
                activity.progressBar.visibility = View.GONE
                activity.styleImage.setImageBitmap(activity.finalStyle)
                activity.isGenerated = true
            }

            override fun onProgressUpdate(vararg text: String?) {

                val activity = activityReference.get()
                if (activity == null || activity.isFinishing) return

                Toast.makeText(activity, text.firstOrNull(), Toast.LENGTH_SHORT).show()

            }
        }
    }




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Glimpse.init(application)
        setContentView(R.layout.cartoon_activity)
        val bd = intent.extras
        val uri = bd!!.getParcelable<Uri>("uri")
        val rCount = bd.getInt("rCount")
        Log.e("URI", uri.toString())
        try {
            contentBitmap = MediaStore.Images.Media.getBitmap(this.contentResolver, uri)
            val (x, y) = contentBitmap.findCenter()
            contentBitmap = contentBitmap.crop(x, y, 384, 384)
            val mat = Matrix()

            mat.postRotate(Integer.parseInt((90*rCount).toString()).toFloat())
            contentBitmap = Bitmap.createBitmap(contentBitmap, 0, 0, contentBitmap.width,
                contentBitmap.height, mat, true);
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }

        progressBar = findViewById(R.id.progressBar)
        msg = findViewById(R.id.msg)
        model = LiteModelCartoonganInt81.newInstance(this)
        styleImage = findViewById(R.id.imageView)


        val task = MyAsyncTask(this)
        task.execute(10)


        var saveBtn: Button = findViewById(R.id.saveBtn)
        saveBtn.setOnClickListener {
            if (isGenerated) {

                saveMediaToStorage(finalStyle, this)

            }else{
                Toast.makeText(this, "Wait Image is generating !", Toast.LENGTH_SHORT)
                    .show()
            }

        }
    }
}

fun saveMediaToStorage(bitmap: Bitmap, context: CartoonActivity) {
    //Generating a file name
    val filename = "${System.currentTimeMillis()}.jpg"

    //Output stream
    var fos: OutputStream? = null

    //For devices running android >= Q

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        //getting the contentResolver
        context?.contentResolver?.also { resolver ->

            //Content resolver will process the contentvalues
            val contentValues = ContentValues().apply {

                //putting file information in content values
                put(MediaStore.MediaColumns.DISPLAY_NAME, filename)
                put(MediaStore.MediaColumns.MIME_TYPE, "image/jpg")
                put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES)
            }

            //Inserting the contentValues to contentResolver and getting the Uri
            val imageUri: Uri? =
                resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)

            //Opening an outputstream with the Uri that we got
            fos = imageUri?.let { resolver.openOutputStream(it) }
        }
    } else {
        //These for devices running on android < Q
        //So I don't think an explanation is needed here
//        Toast.makeText(context, "Here !", Toast.LENGTH_SHORT).show()
//        val imagesDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
        var imagesDir = Environment.getExternalStorageDirectory()
        var dir = File(imagesDir.getAbsolutePath(), "/Pictures")
        val image = File(dir, filename)
        fos = FileOutputStream(image)
    }

    fos?.use {
        //Finally writing the bitmap to the output stream that we opened
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, it)
        Toast.makeText(context, "Saved to photos !", Toast.LENGTH_SHORT).show()
    }
}