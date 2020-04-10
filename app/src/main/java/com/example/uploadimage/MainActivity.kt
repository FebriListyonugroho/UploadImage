package com.example.uploadimage

import android.app.Activity
import android.app.DownloadManager
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.annotation.NonNull
import android.support.v4.app.ActivityCompat
import android.util.Base64
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import com.android.volley.AuthFailureError
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import kotlinx.android.synthetic.main.activity_main.*
import java.io.ByteArrayOutputStream
import java.io.InputStream
import java.util.*
import kotlin.collections.HashMap
import kotlin.collections.Map as Map1


class MainActivity : AppCompatActivity() {

    val CODE_GALLERY_REQUEST = 999
    lateinit var filePath : Uri
    lateinit var inputStream : InputStream
    lateinit var bitmap: Bitmap
    var urlUpload : String = "https://febriln.000webhostapp.com/upload.php"
    var imageBytes = byteArrayOf()
    lateinit var encodedImage : String
    lateinit var imageData : String
    lateinit var requestQueue: RequestQueue

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val btnChoose = findViewById<Button>(R.id.btnChoose)
        val btnUpload = findViewById<Button>(R.id.btnUpload)
        val imageUpload = findViewById<ImageView>(R.id.imageUpload)

        btnChoose.setOnClickListener { v ->
            ActivityCompat.requestPermissions(
                this@MainActivity,
                arrayOf<String>(android.Manifest.permission.READ_EXTERNAL_STORAGE),
                CODE_GALLERY_REQUEST
            )
        }

        btnUpload.setOnClickListener { v ->
            val stringRequest = object : StringRequest(Request.Method.POST, urlUpload,
                Response.Listener<String> { response ->
                    Toast.makeText(applicationContext, response, Toast.LENGTH_LONG).show()
                }, Response.ErrorListener { error->
                Toast.makeText(applicationContext, "Error: ${error.toString()}", Toast.LENGTH_LONG).show()
            }){
               override fun getParams(): HashMap<String, String> {
                    val params = HashMap<String, String>()
                    val imageData = imageToString(bitmap)
                    params.put("image", imageData)
                    return params
                }
            }

            requestQueue = Volley.newRequestQueue(this)
            requestQueue.add(stringRequest)

        }

    }



    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {

        if (requestCode == CODE_GALLERY_REQUEST){

            if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){

                val intent = Intent(Intent.ACTION_PICK)
                intent.setType("image/*")
                startActivityForResult(Intent.createChooser(intent, "Select Image"), CODE_GALLERY_REQUEST)

            }else{
                Toast.makeText(applicationContext, "You don't have permission to access gallery", Toast.LENGTH_LONG).show()
            }
            return
        }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        if (requestCode == CODE_GALLERY_REQUEST && resultCode == Activity.RESULT_OK && data != null){

            filePath = data.data
            inputStream = contentResolver.openInputStream(filePath)
            bitmap = BitmapFactory.decodeStream(inputStream)
            imageUpload.setImageBitmap(bitmap)
        }

        super.onActivityResult(requestCode, resultCode, data)
    }

    private fun imageToString(bitmap: Bitmap):String {

        val outputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
        imageBytes = outputStream.toByteArray()

        encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT)

        return encodedImage
    }

}
