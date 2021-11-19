package com.example.aiart

import android.R.attr.*
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Matrix
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.material.floatingactionbutton.FloatingActionButton
import glimpse.core.Glimpse
import glimpse.core.crop
import glimpse.core.findCenter


class MainActivity : AppCompatActivity() {
    lateinit var contentImage: ImageView
    lateinit var contentBitmap: Bitmap
    var rCount = 0

    companion object {
        private const val STORAGE_PERMISSION_CODE = 101
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        var isSelected: Boolean = false
        lateinit var uri: Uri
        super.onCreate(savedInstanceState)
        Glimpse.init(application)
        setContentView(R.layout.activity_main)

        var selBtn: Button = findViewById(R.id.select_img)

        contentImage = findViewById(R.id.content_image)

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            checkPermission(
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                STORAGE_PERMISSION_CODE
            )
        }
        val getImage = registerForActivityResult(
            ActivityResultContracts.GetContent(),
            ActivityResultCallback {
                uri = it
//                contentImage.setImageURI(uri)
                contentBitmap = MediaStore.Images.Media.getBitmap(this.contentResolver,it)
                val (x, y) = contentBitmap.findCenter()
                contentBitmap = contentBitmap.crop(x, y, 384, 384)
                contentImage.setImageBitmap(contentBitmap)
            }
        )

        selBtn.setOnClickListener(View.OnClickListener {
            rCount = 0
            var instr: TextView = findViewById(R.id.textView)
            instr.setVisibility(View.INVISIBLE)
            getImage.launch("image/*")
            isSelected = true
        })

        var rotateBtn: FloatingActionButton = findViewById(R.id.rotateBtn)

        rotateBtn.setOnClickListener {
            if(isSelected){
                val mat = Matrix()

                mat.postRotate(Integer.parseInt(90.toString()).toFloat())
                contentBitmap = Bitmap.createBitmap(contentBitmap, 0, 0, contentBitmap.width,
                    contentBitmap.height, mat, true);
                contentImage.setImageBitmap(contentBitmap);
                rCount+=1
            }else{
                Toast.makeText(this, "Select the Image first !",Toast.LENGTH_SHORT).show()
            }
        }

        var moveBtn: Button = findViewById(R.id.move)

        moveBtn.setOnClickListener{
            if(isSelected){
                var intent = Intent(this, SecondActivity::class.java)
//                val bs = ByteArrayOutputStream()
//                contentBitmap.compress(Bitmap.CompressFormat.PNG, 50, bs)
//                intent.putExtra("byteArray",bs.toByteArray())
                intent.putExtra("uri",uri);
                intent.putExtra("rCount", rCount);
                startActivity(intent)
            }else{
                Toast.makeText(this,"First Select Image!", Toast.LENGTH_SHORT).show()
            }

        }

        var cartoonifyBtn: Button = findViewById(R.id.cartoonifyBtn)

        cartoonifyBtn.setOnClickListener {
            if(isSelected){
                var intent = Intent(this, CartoonActivity::class.java)
//                val bs = ByteArrayOutputStream()
//                contentBitmap.compress(Bitmap.CompressFormat.PNG, 50, bs)
//                intent.putExtra("byteArray",bs.toByteArray())
                intent.putExtra("uri",uri);
                intent.putExtra("rCount", rCount);
                startActivity(intent)
            }else{
                Toast.makeText(this,"First Select Image!", Toast.LENGTH_SHORT).show()
            }
        }

    }
    public fun checkPermission(permission: String, requestCode: Int) {
        if (ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_DENIED) {

            // Requesting the permission
            ActivityCompat.requestPermissions(this, arrayOf(permission), requestCode)
        } else {
//            Toast.makeText(this, "Permission already granted", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<String>,
                                            grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == STORAGE_PERMISSION_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Storage Permission Granted", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Storage Permission Denied", Toast.LENGTH_SHORT).show()
            }
        }
    }


}