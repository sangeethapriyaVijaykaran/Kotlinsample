package com.example.myapplication

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.media.MediaScannerConnection
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AlertDialog
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import kotlinx.android.synthetic.main.names_item.view.*
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.util.*

class AnotherActivity : AppCompatActivity() {


    private var btn: Button? = null
    private var imageview: ImageView? = null
    private var recycle: RecyclerView? = null
    lateinit var context: Context
    private val GALLERY = 1
    private val CAMERA = 2
    private val CAMERA_REQUEST_CODE = 100
    lateinit var myadapter: Nameadapter
    val names: ArrayList<String> = ArrayList()




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_another)
        context = this

        btn = findViewById(R.id.btn) as Button
        imageview = findViewById(R.id.iv) as ImageView
        recycle = findViewById(R.id.recycle) as RecyclerView
        recycle!!.layoutManager = LinearLayoutManager(this)
        recycle!!.layoutManager = GridLayoutManager(this, 2)



        addnames()


        /*myadapter = Nameadapter(names,context)
        recycle!!.adapter =myadapter

       // recycle!!.adapter = Nameadapter(names, context)

*/

        val adapter = Nameadapter(names,context)
        recycle!!.adapter = adapter









        adapter.setOnItemClickListener(object : Nameadapter.ClickListener {
            override fun onClick(pos: Int, aView: View) {


                Toast.makeText(this@AnotherActivity, pos, Toast.LENGTH_SHORT).show()


            }
        })





        btn!!.setOnClickListener{
            showpicturedialaog()
        }

    }



    private fun addnames() {

        names.add("Apple")
        names.add("Ball")
        names.add("Cat")
        names.add("Dog")

    }




    private fun showpicturedialaog() {

        val pictureDialog = AlertDialog.Builder(this)
        pictureDialog.setTitle("Select Action")
        val pictureDialogItems = arrayOf("Select photo from gallery", "Capture photo from camera")
        pictureDialog.setItems(pictureDialogItems
        ) { dialog, which ->
            when (which) {
                0 -> choosePhotoFromGallary()
                1 -> setupPermissions()
            }
        }
        pictureDialog.show()

    }

    private fun setupPermissions() {
        val permission = ContextCompat.checkSelfPermission(this,
            Manifest.permission.CAMERA)

        if (permission != PackageManager.PERMISSION_GRANTED) {
            Log.i("Status", "Permission to open camera denied")

            makeRequest()
        }else{
            takePhotoFromCamera()
        }
    }

    private fun makeRequest() {

        ActivityCompat.requestPermissions(this,
            arrayOf(Manifest.permission.CAMERA),
            CAMERA_REQUEST_CODE)
    }

    private fun takePhotoFromCamera() {

        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(intent, CAMERA)
    }

    
    private fun choosePhotoFromGallary() {
        val galleryIntent = Intent(Intent.ACTION_PICK,
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI)

        startActivityForResult(galleryIntent, GALLERY)    }






     override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)


        when(requestCode) {
            CAMERA_REQUEST_CODE ->{
                if(grantResults.isEmpty() || grantResults[0] != PackageManager.PERMISSION_GRANTED){

                }else{
                    takePhotoFromCamera()
                }
            }
        }
    }



    public override fun onActivityResult(requestCode:Int, resultCode:Int, data: Intent?) {

        super.onActivityResult(requestCode, resultCode, data)
        /* if (resultCode == this.RESULT_CANCELED)
         {
         return
         }*/
        if (requestCode == GALLERY)
        {
            if (data != null)
            {
                val contentURI = data!!.data
                try
                {
                    val bitmap = MediaStore.Images.Media.getBitmap(this.contentResolver, contentURI)
                    val path = saveImage(bitmap)
                    Toast.makeText(this@AnotherActivity, "Image Saved!", Toast.LENGTH_SHORT).show()
                    imageview!!.setImageBitmap(bitmap)

                }
                catch (e: IOException) {
                    e.printStackTrace()
                    Toast.makeText(this@AnotherActivity, "Failed!", Toast.LENGTH_SHORT).show()
                }

            }

        }
        else if (requestCode == CAMERA)
        {
            val thumbnail = data!!.extras!!.get("data") as Bitmap
            imageview!!.setImageBitmap(thumbnail)
            saveImage(thumbnail)
            Toast.makeText(this@AnotherActivity, "Image Saved!", Toast.LENGTH_SHORT).show()
        }
    }

    fun saveImage(myBitmap: Bitmap):String {
        val bytes = ByteArrayOutputStream()
        myBitmap.compress(Bitmap.CompressFormat.JPEG, 90, bytes)
        val wallpaperDirectory = File(
            (Environment.getExternalStorageDirectory()).toString() + IMAGE_DIRECTORY)
        // have the object build the directory structure, if needed.
        Log.d("fee",wallpaperDirectory.toString())
        if (!wallpaperDirectory.exists())
        {

            wallpaperDirectory.mkdirs()
        }

        try
        {
            Log.d("heel",wallpaperDirectory.toString())
            val f = File(wallpaperDirectory, ((Calendar.getInstance()
                .getTimeInMillis()).toString() + ".jpg"))
            f.createNewFile()
            val fo = FileOutputStream(f)
            fo.write(bytes.toByteArray())
            MediaScannerConnection.scanFile(this,
                arrayOf(f.getPath()),
                arrayOf("image/jpeg"), null)
            fo.close()
            Log.d("TAG", "File Saved::--->" + f.getAbsolutePath())

            return f.getAbsolutePath()
        }
        catch (e1: IOException) {
            e1.printStackTrace()
        }

        return ""
    }

    companion object {
        private val IMAGE_DIRECTORY = "/demonuts"
    }



    class Nameadapter(val names: ArrayList<String>, val context: Context) : RecyclerView.Adapter<Nameadapter.ViewHolder>() {

        lateinit var mClickListener: ClickListener


        override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ViewHolder {
            return ViewHolder(LayoutInflater.from(context).inflate(R.layout.names_item, parent, false))
        }

        override fun getItemCount(): Int {
            return names.size
        }

        override fun onBindViewHolder(holder: ViewHolder?, position: Int) {
            holder?.tvAnimalType?.text = names.get(position)
        }

        interface ClickListener {
            fun onClick(pos: Int, aView: View)
        }


        fun setOnItemClickListener(aClickListener: ClickListener) {
            mClickListener = aClickListener
        }


        class ViewHolder (view: View) : RecyclerView.ViewHolder(view) {
            val tvAnimalType = view.names_a as TextView
        }


    }


}



