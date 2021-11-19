package com.example.aiart

import android.content.ContentValues
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.net.Uri
import android.os.AsyncTask
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.aiart.ml.Tfhubinceptionint8predict
import com.example.aiart.ml.Tfhubinceptionint8transfer
import glimpse.core.Glimpse
import glimpse.core.crop
import glimpse.core.findCenter
import org.tensorflow.lite.DataType
import org.tensorflow.lite.support.image.TensorImage
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer
import java.io.*
import java.lang.ref.WeakReference
import java.nio.ByteBuffer


class SecondActivity: AppCompatActivity() {
    lateinit var bitmap: Bitmap
    lateinit var styleImage: ImageView
    lateinit var finalStyle: Bitmap
    lateinit var contentBitmap: Bitmap
    lateinit var byteBuffer: ByteBuffer
    lateinit var contentImg: TensorImage
    lateinit var styleBottleneckInstance: TensorBuffer
    lateinit var loadBar:ProgressBar
    lateinit var model_p:Tfhubinceptionint8predict
    lateinit var model:Tfhubinceptionint8transfer

    var isSelected = false
    var isGenerated = false

    companion object {
        class MyAsyncTask internal constructor(context: SecondActivity) : AsyncTask<Int, String, String?>() {

            private var resp: String? = null
            private val activityReference: WeakReference<SecondActivity> = WeakReference(context)

            override fun onPreExecute() {
                val activity = activityReference.get()
                if (activity == null || activity.isFinishing) return
                activity.loadBar.visibility = View.VISIBLE
            }

            override fun doInBackground(vararg params: Int?): String? {
                val activity = activityReference.get()
                if (activity == null || activity.isFinishing) return "something"
                if (activity.isSelected) {

//                setProgressBarIndeterminateVisibility(true);

                    activity.model = Tfhubinceptionint8transfer.newInstance(activity.applicationContext)


// Creates inputs for reference.


// Runs model inference and gets result.
                    val outputs = activity.model.process(activity.contentImg, activity.styleBottleneckInstance)
                    val styledImage = outputs.styledImageAsTensorImage
                    val styledImageBitmap = styledImage.bitmap

                    activity.finalStyle = styledImageBitmap

// Releases model resources if no longer used.
                    activity.model.close()
                    activity.isGenerated = true
                } else {
                    Toast.makeText(activity, "First Select a Style!", Toast.LENGTH_SHORT)
                        .show()
                }
                return  "Executed"
            }


            override fun onPostExecute(result: String?) {

                val activity = activityReference.get()
                if (activity == null || activity.isFinishing) return
                activity.loadBar.visibility = View.GONE
                activity.styleImage.setImageBitmap(activity.finalStyle)

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
        setContentView(R.layout.second_activity)

//        var contentBitmap = BitmapFactory.decodeByteArray(
//            intent.getByteArrayExtra("byteArray"), 0, intent.getByteArrayExtra("byteArray")!!.size
//        )
        model_p = Tfhubinceptionint8predict.newInstance(this)
        model = Tfhubinceptionint8transfer.newInstance(this)
        styleImage = findViewById(R.id.style_image)
        loadBar = findViewById(R.id.loadBar)


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

        styleImage = findViewById(R.id.style_image)
        var s0: ImageView = findViewById(R.id.sl0)

        var s2: ImageView = findViewById(R.id.sl2)

        var s4: ImageView = findViewById(R.id.sl4)
        var s5: ImageView = findViewById(R.id.sl5)
        var s6: ImageView = findViewById(R.id.sl6)
        var s7: ImageView = findViewById(R.id.sl7)
        var s8: ImageView = findViewById(R.id.sl8)
        var s9: ImageView = findViewById(R.id.sl9)
        var s10: ImageView = findViewById(R.id.sl10)
        var s11: ImageView = findViewById(R.id.sl11)
        var s12: ImageView = findViewById(R.id.sl12)
        var s13: ImageView = findViewById(R.id.sl13)
        var s14: ImageView = findViewById(R.id.sl14)
        var s15: ImageView = findViewById(R.id.sl15)
        var s16: ImageView = findViewById(R.id.sl16)

        var s18: ImageView = findViewById(R.id.sl18)
        var s19: ImageView = findViewById(R.id.sl19)
        var s20: ImageView = findViewById(R.id.sl20)

        var s22: ImageView = findViewById(R.id.sl22)
        var s23: ImageView = findViewById(R.id.sl23)
        var s24: ImageView = findViewById(R.id.sl24)
        var s25: ImageView = findViewById(R.id.sl25)

        var msgText: TextView = findViewById(R.id.msg)

        s0.setOnClickListener() {


            msgText.setVisibility(View.INVISIBLE)
            isSelected = true
            bitmap = BitmapFactory.decodeResource(
                this.resources,
                R.drawable.style0
            )



            var styleImageTensor = TensorImage.fromBitmap(bitmap)

            val outputs_1 = model_p.process(styleImageTensor)

            var styleBottleneck = outputs_1.styleBottleneckAsTensorBuffer

            byteBuffer = styleBottleneck.buffer
            contentImg = TensorImage.fromBitmap(contentBitmap)
            styleBottleneckInstance =
                TensorBuffer.createFixedSize(intArrayOf(1, 1, 1, 100), DataType.FLOAT32)

            styleBottleneckInstance.loadBuffer(byteBuffer)


            styleImage.setImageResource(R.drawable.style0)
            isGenerated = false
            Toast.makeText(applicationContext,"Now press the generate button !", Toast.LENGTH_SHORT).show()
        }

        s2.setOnClickListener {
            isSelected = true
            msgText.setVisibility(View.INVISIBLE)

            bitmap = BitmapFactory.decodeResource(
                this.resources,
                R.drawable.style2
            )



            var styleImageTensor = TensorImage.fromBitmap(bitmap)

            val outputs_1 = model_p.process(styleImageTensor)

            var styleBottleneck = outputs_1.styleBottleneckAsTensorBuffer

            byteBuffer = styleBottleneck.buffer
            byteBuffer = styleBottleneck.buffer
            contentImg = TensorImage.fromBitmap(contentBitmap)
            styleBottleneckInstance =
                TensorBuffer.createFixedSize(intArrayOf(1, 1, 1, 100), DataType.FLOAT32)

            styleBottleneckInstance.loadBuffer(byteBuffer)
            styleImage.setImageResource(R.drawable.style2)
            isGenerated = false
            Toast.makeText(applicationContext,"Now press the generate button !", Toast.LENGTH_SHORT).show()
        }

        s4.setOnClickListener {
            isSelected = true
            msgText.setVisibility(View.INVISIBLE)

            bitmap = BitmapFactory.decodeResource(
                this.resources,
                R.drawable.style4
            )



            var styleImageTensor = TensorImage.fromBitmap(bitmap)

            val outputs_1 = model_p.process(styleImageTensor)

            var styleBottleneck = outputs_1.styleBottleneckAsTensorBuffer

            byteBuffer = styleBottleneck.buffer
            byteBuffer = styleBottleneck.buffer
            contentImg = TensorImage.fromBitmap(contentBitmap)
            styleBottleneckInstance =
                TensorBuffer.createFixedSize(intArrayOf(1, 1, 1, 100), DataType.FLOAT32)

            styleBottleneckInstance.loadBuffer(byteBuffer)
            styleImage.setImageResource(R.drawable.style4)
            isGenerated = false
            Toast.makeText(applicationContext,"Now press the generate button !", Toast.LENGTH_SHORT).show()
        }
        s5.setOnClickListener {
            isSelected = true
            msgText.setVisibility(View.INVISIBLE)

            bitmap = BitmapFactory.decodeResource(
                this.resources,
                R.drawable.style5
            )



            var styleImageTensor = TensorImage.fromBitmap(bitmap)

            val outputs_1 = model_p.process(styleImageTensor)

            var styleBottleneck = outputs_1.styleBottleneckAsTensorBuffer

            byteBuffer = styleBottleneck.buffer
            byteBuffer = styleBottleneck.buffer
            contentImg = TensorImage.fromBitmap(contentBitmap)
            styleBottleneckInstance =
                TensorBuffer.createFixedSize(intArrayOf(1, 1, 1, 100), DataType.FLOAT32)

            styleBottleneckInstance.loadBuffer(byteBuffer)
            styleImage.setImageResource(R.drawable.style5)
            isGenerated = false
            Toast.makeText(applicationContext,"Now press the generate button !", Toast.LENGTH_SHORT).show()
        }
        s6.setOnClickListener {
            isSelected = true
            msgText.setVisibility(View.INVISIBLE)

            bitmap = BitmapFactory.decodeResource(
                this.resources,
                R.drawable.style6
            )



            var styleImageTensor = TensorImage.fromBitmap(bitmap)

            val outputs_1 = model_p.process(styleImageTensor)

            var styleBottleneck = outputs_1.styleBottleneckAsTensorBuffer

            byteBuffer = styleBottleneck.buffer
            byteBuffer = styleBottleneck.buffer
            contentImg = TensorImage.fromBitmap(contentBitmap)
            styleBottleneckInstance =
                TensorBuffer.createFixedSize(intArrayOf(1, 1, 1, 100), DataType.FLOAT32)

            styleBottleneckInstance.loadBuffer(byteBuffer)
            styleImage.setImageResource(R.drawable.style6)
            isGenerated = false
            Toast.makeText(applicationContext,"Now press the generate button !", Toast.LENGTH_SHORT).show()
        }
        s7.setOnClickListener {
            isSelected = true
            msgText.setVisibility(View.INVISIBLE)

            bitmap = BitmapFactory.decodeResource(
                this.resources,
                R.drawable.style7
            )



            var styleImageTensor = TensorImage.fromBitmap(bitmap)

            val outputs_1 = model_p.process(styleImageTensor)

            var styleBottleneck = outputs_1.styleBottleneckAsTensorBuffer

            byteBuffer = styleBottleneck.buffer
            byteBuffer = styleBottleneck.buffer
            contentImg = TensorImage.fromBitmap(contentBitmap)
            styleBottleneckInstance =
                TensorBuffer.createFixedSize(intArrayOf(1, 1, 1, 100), DataType.FLOAT32)

            styleBottleneckInstance.loadBuffer(byteBuffer)
            styleImage.setImageResource(R.drawable.style7)
            isGenerated = false
            Toast.makeText(applicationContext,"Now press the generate button !", Toast.LENGTH_SHORT).show()
        }
        s8.setOnClickListener {
            isSelected = true
            msgText.setVisibility(View.INVISIBLE)

            bitmap = BitmapFactory.decodeResource(
                this.resources,
                R.drawable.style8
            )



            var styleImageTensor = TensorImage.fromBitmap(bitmap)

            val outputs_1 = model_p.process(styleImageTensor)

            var styleBottleneck = outputs_1.styleBottleneckAsTensorBuffer

            byteBuffer = styleBottleneck.buffer
            byteBuffer = styleBottleneck.buffer
            contentImg = TensorImage.fromBitmap(contentBitmap)
            styleBottleneckInstance =
                TensorBuffer.createFixedSize(intArrayOf(1, 1, 1, 100), DataType.FLOAT32)

            styleBottleneckInstance.loadBuffer(byteBuffer)
            styleImage.setImageResource(R.drawable.style8)
            isGenerated = false
            Toast.makeText(applicationContext,"Now press the generate button !", Toast.LENGTH_SHORT).show()
        }
        s9.setOnClickListener {
            isSelected = true
            msgText.setVisibility(View.INVISIBLE)

            bitmap = BitmapFactory.decodeResource(
                this.resources,
                R.drawable.style9
            )



            var styleImageTensor = TensorImage.fromBitmap(bitmap)

            val outputs_1 = model_p.process(styleImageTensor)

            var styleBottleneck = outputs_1.styleBottleneckAsTensorBuffer

            byteBuffer = styleBottleneck.buffer
            byteBuffer = styleBottleneck.buffer
            contentImg = TensorImage.fromBitmap(contentBitmap)
            styleBottleneckInstance =
                TensorBuffer.createFixedSize(intArrayOf(1, 1, 1, 100), DataType.FLOAT32)

            styleBottleneckInstance.loadBuffer(byteBuffer)
            styleImage.setImageResource(R.drawable.style9)
            isGenerated = false
            Toast.makeText(applicationContext,"Now press the generate button !", Toast.LENGTH_SHORT).show()
        }
        s10.setOnClickListener {
            isSelected = true
            msgText.setVisibility(View.INVISIBLE)

            bitmap = BitmapFactory.decodeResource(
                this.resources,
                R.drawable.style10
            )



            var styleImageTensor = TensorImage.fromBitmap(bitmap)

            val outputs_1 = model_p.process(styleImageTensor)

            var styleBottleneck = outputs_1.styleBottleneckAsTensorBuffer

            byteBuffer = styleBottleneck.buffer
            byteBuffer = styleBottleneck.buffer
            contentImg = TensorImage.fromBitmap(contentBitmap)
            styleBottleneckInstance =
                TensorBuffer.createFixedSize(intArrayOf(1, 1, 1, 100), DataType.FLOAT32)

            styleBottleneckInstance.loadBuffer(byteBuffer)
            styleImage.setImageResource(R.drawable.style10)
            isGenerated = false
            Toast.makeText(applicationContext,"Now press the generate button !", Toast.LENGTH_SHORT).show()
        }
        s11.setOnClickListener {
            isSelected = true
            msgText.setVisibility(View.INVISIBLE)

            bitmap = BitmapFactory.decodeResource(
                this.resources,
                R.drawable.style11
            )



            var styleImageTensor = TensorImage.fromBitmap(bitmap)

            val outputs_1 = model_p.process(styleImageTensor)

            var styleBottleneck = outputs_1.styleBottleneckAsTensorBuffer

            byteBuffer = styleBottleneck.buffer
            byteBuffer = styleBottleneck.buffer
            contentImg = TensorImage.fromBitmap(contentBitmap)
            styleBottleneckInstance =
                TensorBuffer.createFixedSize(intArrayOf(1, 1, 1, 100), DataType.FLOAT32)

            styleBottleneckInstance.loadBuffer(byteBuffer)
            styleImage.setImageResource(R.drawable.style11)
            isGenerated = false
            Toast.makeText(applicationContext,"Now press the generate button !", Toast.LENGTH_SHORT).show()
        }
        s12.setOnClickListener {
            isSelected = true
            msgText.setVisibility(View.INVISIBLE)

            bitmap = BitmapFactory.decodeResource(
                this.resources,
                R.drawable.style12
            )



            var styleImageTensor = TensorImage.fromBitmap(bitmap)

            val outputs_1 = model_p.process(styleImageTensor)

            var styleBottleneck = outputs_1.styleBottleneckAsTensorBuffer

            byteBuffer = styleBottleneck.buffer
            byteBuffer = styleBottleneck.buffer
            contentImg = TensorImage.fromBitmap(contentBitmap)
            styleBottleneckInstance =
                TensorBuffer.createFixedSize(intArrayOf(1, 1, 1, 100), DataType.FLOAT32)

            styleBottleneckInstance.loadBuffer(byteBuffer)
            styleImage.setImageResource(R.drawable.style12)
            isGenerated = false
            Toast.makeText(applicationContext,"Now press the generate button !", Toast.LENGTH_SHORT).show()
        }
        s13.setOnClickListener {
            isSelected = true
            msgText.setVisibility(View.INVISIBLE)

            bitmap = BitmapFactory.decodeResource(
                this.resources,
                R.drawable.style13
            )



            var styleImageTensor = TensorImage.fromBitmap(bitmap)

            val outputs_1 = model_p.process(styleImageTensor)

            var styleBottleneck = outputs_1.styleBottleneckAsTensorBuffer

            byteBuffer = styleBottleneck.buffer
            byteBuffer = styleBottleneck.buffer
            contentImg = TensorImage.fromBitmap(contentBitmap)
            styleBottleneckInstance =
                TensorBuffer.createFixedSize(intArrayOf(1, 1, 1, 100), DataType.FLOAT32)

            styleBottleneckInstance.loadBuffer(byteBuffer)
            styleImage.setImageResource(R.drawable.style13)
            isGenerated = false
            Toast.makeText(applicationContext,"Now press the generate button !", Toast.LENGTH_SHORT).show()
        }
        s14.setOnClickListener {
            isSelected = true
            msgText.setVisibility(View.INVISIBLE)

            bitmap = BitmapFactory.decodeResource(
                this.resources,
                R.drawable.style14
            )



            var styleImageTensor = TensorImage.fromBitmap(bitmap)

            val outputs_1 = model_p.process(styleImageTensor)

            var styleBottleneck = outputs_1.styleBottleneckAsTensorBuffer

            byteBuffer = styleBottleneck.buffer
            byteBuffer = styleBottleneck.buffer
            contentImg = TensorImage.fromBitmap(contentBitmap)
            styleBottleneckInstance =
                TensorBuffer.createFixedSize(intArrayOf(1, 1, 1, 100), DataType.FLOAT32)

            styleBottleneckInstance.loadBuffer(byteBuffer)
            styleImage.setImageResource(R.drawable.style14)
            isGenerated = false
            Toast.makeText(applicationContext,"Now press the generate button !", Toast.LENGTH_SHORT).show()
        }
        s15.setOnClickListener {
            isSelected = true
            msgText.setVisibility(View.INVISIBLE)

            bitmap = BitmapFactory.decodeResource(
                this.resources,
                R.drawable.style15
            )



            var styleImageTensor = TensorImage.fromBitmap(bitmap)

            val outputs_1 = model_p.process(styleImageTensor)

            var styleBottleneck = outputs_1.styleBottleneckAsTensorBuffer

            byteBuffer = styleBottleneck.buffer
            byteBuffer = styleBottleneck.buffer
            contentImg = TensorImage.fromBitmap(contentBitmap)
            styleBottleneckInstance =
                TensorBuffer.createFixedSize(intArrayOf(1, 1, 1, 100), DataType.FLOAT32)

            styleBottleneckInstance.loadBuffer(byteBuffer)
            styleImage.setImageResource(R.drawable.style15)
            isGenerated = false
            Toast.makeText(applicationContext,"Now press the generate button !", Toast.LENGTH_SHORT).show()
        }
        s16.setOnClickListener {
            isSelected = true
            msgText.setVisibility(View.INVISIBLE)

            bitmap = BitmapFactory.decodeResource(
                this.resources,
                R.drawable.style16
            )



            var styleImageTensor = TensorImage.fromBitmap(bitmap)

            val outputs_1 = model_p.process(styleImageTensor)

            var styleBottleneck = outputs_1.styleBottleneckAsTensorBuffer

            byteBuffer = styleBottleneck.buffer
            byteBuffer = styleBottleneck.buffer
            contentImg = TensorImage.fromBitmap(contentBitmap)
            styleBottleneckInstance =
                TensorBuffer.createFixedSize(intArrayOf(1, 1, 1, 100), DataType.FLOAT32)

            styleBottleneckInstance.loadBuffer(byteBuffer)
            styleImage.setImageResource(R.drawable.style16)
            isGenerated = false
            Toast.makeText(applicationContext,"Now press the generate button !", Toast.LENGTH_SHORT).show()
        }

        s18.setOnClickListener {
            isSelected = true
            msgText.setVisibility(View.INVISIBLE)

            bitmap = BitmapFactory.decodeResource(
                this.resources,
                R.drawable.style18
            )



            var styleImageTensor = TensorImage.fromBitmap(bitmap)

            val outputs_1 = model_p.process(styleImageTensor)

            var styleBottleneck = outputs_1.styleBottleneckAsTensorBuffer

            byteBuffer = styleBottleneck.buffer
            byteBuffer = styleBottleneck.buffer
            contentImg = TensorImage.fromBitmap(contentBitmap)
            styleBottleneckInstance =
                TensorBuffer.createFixedSize(intArrayOf(1, 1, 1, 100), DataType.FLOAT32)

            styleBottleneckInstance.loadBuffer(byteBuffer)
            styleImage.setImageResource(R.drawable.style18)
            isGenerated = false
            Toast.makeText(applicationContext,"Now press the generate button !", Toast.LENGTH_SHORT).show()
        }
        s19.setOnClickListener {
            isSelected = true
            msgText.setVisibility(View.INVISIBLE)

            bitmap = BitmapFactory.decodeResource(
                this.resources,
                R.drawable.style19
            )



            var styleImageTensor = TensorImage.fromBitmap(bitmap)

            val outputs_1 = model_p.process(styleImageTensor)

            var styleBottleneck = outputs_1.styleBottleneckAsTensorBuffer

            byteBuffer = styleBottleneck.buffer
            byteBuffer = styleBottleneck.buffer
            contentImg = TensorImage.fromBitmap(contentBitmap)
            styleBottleneckInstance =
                TensorBuffer.createFixedSize(intArrayOf(1, 1, 1, 100), DataType.FLOAT32)

            styleBottleneckInstance.loadBuffer(byteBuffer)
            styleImage.setImageResource(R.drawable.style19)
            isGenerated = false
            Toast.makeText(applicationContext,"Now press the generate button !", Toast.LENGTH_SHORT).show()
        }
        s20.setOnClickListener {
            isSelected = true
            msgText.setVisibility(View.INVISIBLE)

            bitmap = BitmapFactory.decodeResource(
                this.resources,
                R.drawable.style20
            )



            var styleImageTensor = TensorImage.fromBitmap(bitmap)

            val outputs_1 = model_p.process(styleImageTensor)

            var styleBottleneck = outputs_1.styleBottleneckAsTensorBuffer

            byteBuffer = styleBottleneck.buffer
            byteBuffer = styleBottleneck.buffer
            contentImg = TensorImage.fromBitmap(contentBitmap)
            styleBottleneckInstance =
                TensorBuffer.createFixedSize(intArrayOf(1, 1, 1, 100), DataType.FLOAT32)

            styleBottleneckInstance.loadBuffer(byteBuffer)
            styleImage.setImageResource(R.drawable.style20)
            isGenerated = false
            Toast.makeText(applicationContext,"Now press the generate button !", Toast.LENGTH_SHORT).show()
        }

        s22.setOnClickListener {
            isSelected = true
            msgText.setVisibility(View.INVISIBLE)

            bitmap = BitmapFactory.decodeResource(
                this.resources,
                R.drawable.style22
            )



            var styleImageTensor = TensorImage.fromBitmap(bitmap)

            val outputs_1 = model_p.process(styleImageTensor)

            var styleBottleneck = outputs_1.styleBottleneckAsTensorBuffer

            byteBuffer = styleBottleneck.buffer
            byteBuffer = styleBottleneck.buffer
            contentImg = TensorImage.fromBitmap(contentBitmap)
            styleBottleneckInstance =
                TensorBuffer.createFixedSize(intArrayOf(1, 1, 1, 100), DataType.FLOAT32)

            styleBottleneckInstance.loadBuffer(byteBuffer)
            styleImage.setImageResource(R.drawable.style22)
            isGenerated = false
            Toast.makeText(applicationContext,"Now press the generate button !", Toast.LENGTH_SHORT).show()
        }
        s23.setOnClickListener {
            isSelected = true
            msgText.setVisibility(View.INVISIBLE)

            bitmap = BitmapFactory.decodeResource(
                this.resources,
                R.drawable.style23
            )



            var styleImageTensor = TensorImage.fromBitmap(bitmap)

            val outputs_1 = model_p.process(styleImageTensor)

            var styleBottleneck = outputs_1.styleBottleneckAsTensorBuffer

            byteBuffer = styleBottleneck.buffer
            byteBuffer = styleBottleneck.buffer
            contentImg = TensorImage.fromBitmap(contentBitmap)
            styleBottleneckInstance =
                TensorBuffer.createFixedSize(intArrayOf(1, 1, 1, 100), DataType.FLOAT32)

            styleBottleneckInstance.loadBuffer(byteBuffer)
            styleImage.setImageResource(R.drawable.style23)
            isGenerated = false
            Toast.makeText(applicationContext,"Now press the generate button !", Toast.LENGTH_SHORT).show()
        }
        s24.setOnClickListener {
            isSelected = true
            msgText.setVisibility(View.INVISIBLE)

            bitmap = BitmapFactory.decodeResource(
                this.resources,
                R.drawable.style24
            )



            var styleImageTensor = TensorImage.fromBitmap(bitmap)

            val outputs_1 = model_p.process(styleImageTensor)

            var styleBottleneck = outputs_1.styleBottleneckAsTensorBuffer

            byteBuffer = styleBottleneck.buffer
            byteBuffer = styleBottleneck.buffer
            contentImg = TensorImage.fromBitmap(contentBitmap)
            styleBottleneckInstance =
                TensorBuffer.createFixedSize(intArrayOf(1, 1, 1, 100), DataType.FLOAT32)

            styleBottleneckInstance.loadBuffer(byteBuffer)
            styleImage.setImageResource(R.drawable.style24)
            isGenerated = false
            Toast.makeText(applicationContext,"Now press the generate button !", Toast.LENGTH_SHORT).show()
        }
        s25.setOnClickListener {
            isSelected = true
            msgText.setVisibility(View.INVISIBLE)

            bitmap = BitmapFactory.decodeResource(
                this.resources,
                R.drawable.style25
            )



            var styleImageTensor = TensorImage.fromBitmap(bitmap)

            val outputs_1 = model_p.process(styleImageTensor)

            var styleBottleneck = outputs_1.styleBottleneckAsTensorBuffer

            byteBuffer = styleBottleneck.buffer
            byteBuffer = styleBottleneck.buffer
            contentImg = TensorImage.fromBitmap(contentBitmap)
            styleBottleneckInstance =
                TensorBuffer.createFixedSize(intArrayOf(1, 1, 1, 100), DataType.FLOAT32)

            styleBottleneckInstance.loadBuffer(byteBuffer)
            styleImage.setImageResource(R.drawable.style25)
            isGenerated = false

            Toast.makeText(applicationContext,"Now press the generate button !", Toast.LENGTH_SHORT).show()

        }


        var generate: Button = findViewById(R.id.gen)

        generate.setOnClickListener {
            MyAsyncTask(this).execute()
//            if (isSelected) {
//
////                setProgressBarIndeterminateVisibility(true);
//
//                val model = Tfhubinceptionint8transfer.newInstance(applicationContext)
//
//
//// Creates inputs for reference.
//
//
//// Runs model inference and gets result.
//                val outputs = model.process(contentImg, styleBottleneckInstance)
//                val styledImage = outputs.styledImageAsTensorImage
//                val styledImageBitmap = styledImage.bitmap
//
//                finalStyle = styledImageBitmap
//                styleImage.setImageBitmap(styledImageBitmap)
//// Releases model resources if no longer used.
//                model.close()
//                isGenerated = true
//            } else {
//                Toast.makeText(applicationContext, "First Select a Style!", Toast.LENGTH_SHORT)
//                    .show()
//            }
//            loadBar.visibility = View.INVISIBLE
        }
        var saveBtn: Button = findViewById(R.id.save)

        saveBtn.setOnClickListener {
            if (isGenerated) {
//                val savedImageURL = MediaStore.Images.Media.insertImage(
//                    contentResolver,
//                    finalStyle,
//                    "${UUID.randomUUID()}",
//                    "Image of $title"
//
//                )
//                Toast.makeText(
//                    applicationContext,
//                    "Saved at ${Uri.parse(savedImageURL)}",
//                    Toast.LENGTH_SHORT
//                ).show()
                saveMediaToStorage(finalStyle, this)

            }else{
                Toast.makeText(this, "First Generate Image !", Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }

}

fun saveMediaToStorage(bitmap: Bitmap, context: SecondActivity) {
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


