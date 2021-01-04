package com.example.happyplaces.activities

import android.app.Activity
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.provider.Settings
import android.util.Log
import android.view.View
import android.widget.Toast
import com.example.happyplaces.R
import com.example.happyplaces.database.DataBaseHandler
import com.example.happyplaces.models.HappyPlaceModel
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import kotlinx.android.synthetic.main.activity_add_happy_place.*
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream
import java.net.URI
import java.text.SimpleDateFormat
import java.util.*

class AddHappyPlaceActivity : AppCompatActivity(), View.OnClickListener {

    private var cal= Calendar.getInstance()
    private  lateinit var dateSetListener: DatePickerDialog.OnDateSetListener
    private var saveImageToInternalStorage:Uri?=null
    private var mLatitude:Double=0.0
    private var mLongitude:Double=0.0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_happy_place)
        //CODE TO IMPLEMENT BACK BUTTON TO RETURN TO PREVIOUS ACTIVITY
        setSupportActionBar(toolbar_add_place)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar_add_place.setNavigationOnClickListener()
        {
            onBackPressed()
        }
        dateSetListener=DatePickerDialog.OnDateSetListener { view, year, month, dayOfMonth ->
            cal.set(Calendar.YEAR,year)
            cal.set(Calendar.DAY_OF_MONTH,dayOfMonth)
            cal.set(Calendar.MONTH,month)
            updateDateInView()
        }
    updateDateInView()
    et_date.setOnClickListener(this)
    tv_add_image.setOnClickListener(this)
        btn_save.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when(v!!.id)
        {
            R.id.et_date ->{
                DatePickerDialog(this@AddHappyPlaceActivity,dateSetListener,
                cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH),
                cal.get(Calendar.DAY_OF_MONTH)).show()
            }
            R.id.tv_add_image -> {
                val pictureDialog= AlertDialog.Builder(this)
                pictureDialog.setTitle("Select Title")
                val pictureDialogItems= arrayOf("Select photo from gallery","Capture from Camera")
                pictureDialog.setItems(pictureDialogItems){
                    dialog, which ->
                    when(which)
                    {
                        0->choosePhotoFromGallery()
                            1->takePhotoFromCamera()
                              //  Toast.makeText(this,"Camera selection coming soon",Toast.LENGTH_SHORT).show()
                    }
                }
                pictureDialog.show()
            }
            R.id.btn_save-> {
                when {
                    et_title.text.isNullOrEmpty()->
                    {
                        Toast.makeText(this,"please fill in all details",Toast.LENGTH_SHORT).show()
                    }
                    et_description.text.isNullOrEmpty()->
                    {
                        Toast.makeText(this,"please fill in all details",Toast.LENGTH_SHORT).show()
                    }
                    et_location.text.isNullOrEmpty()->
                    {
                        Toast.makeText(this,"please fill in all details",Toast.LENGTH_SHORT).show()
                    }
                    saveImageToInternalStorage==null ->
                    {
                        Toast.makeText(this,"please select images",Toast.LENGTH_SHORT).show()
                    }
                    else->
                    {
                      val HappyPlaceModel=HappyPlaceModel(
                           0,
                           et_title.text.toString(),
                          saveImageToInternalStorage.toString(),
                      et_description.text.toString(),
                          et_date.text.toString(),
                          et_location.text.toString(),
                          mLatitude,
                          mLongitude
                      )
                        val dbHandler=DataBaseHandler(this)
                        val addHappyPlace=dbHandler.addHappyPlaces(HappyPlaceModel)
                        val addtotablenew:Long=dbHandler.tablenewadd(1,"name")

                        if(addHappyPlace>0)
                            Toast.makeText(this,"Details are inserted successfully",Toast.LENGTH_SHORT).show()
                        if(addtotablenew>0)
                            Toast.makeText(this,"table 2 works",Toast.LENGTH_SHORT).show()
                        finish()
                    }

                }
            }
        }
    }
    private fun updateDateInView()
    {
        val myFormat="dd.MM.yyyy"
        val sdf=SimpleDateFormat(myFormat,Locale.getDefault())
        et_date.setText(sdf.format(cal.time).toString())
    }

    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?)
    {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode== Activity.RESULT_OK)
        {
            if(requestCode== GALLERY)
            {
                if(data!=null)
                {
                    val contentURI=data.data
                    try{
                        val selectedImageBitmap=MediaStore.Images.Media.getBitmap(this.contentResolver,contentURI)
                      saveImageToInternalStorage= saveImageToInternalStorage(selectedImageBitmap)
                        Log.e("Saved image","Path::$saveImageToInternalStorage")
                        iv_place_image.setImageBitmap(selectedImageBitmap)
                    }catch (e:IOException)
                    {
                        e.printStackTrace()
                        Toast.makeText(this,"Failed to load image from gallery",Toast.LENGTH_SHORT).show()
                    }
                }
            }
            else if(requestCode== CAMERA)
            {
                val thumbnail=data!!.extras!!.get("data") as Bitmap
                iv_place_image.setImageBitmap(thumbnail)
                saveImageToInternalStorage= saveImageToInternalStorage(thumbnail)
                Log.e("Saved image","Path::$saveImageToInternalStorage")
            }
        }
    }

    private fun takePhotoFromCamera()
    {
        Dexter.withContext(this).withPermissions(
            android.Manifest.permission.READ_EXTERNAL_STORAGE,
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
        android.Manifest.permission.CAMERA).withListener(object:MultiplePermissionsListener {

            override fun onPermissionsChecked(report:MultiplePermissionsReport?)
            {
                if(report!!.areAllPermissionsGranted())
                {
                    val galleryintent=Intent(MediaStore.ACTION_IMAGE_CAPTURE)

                    startActivityForResult(galleryintent,
                        CAMERA
                    )

                }}
            override fun onPermissionRationaleShouldBeShown(permissions:MutableList<PermissionRequest>,token: PermissionToken) {
                showrationalDialogForPermission()
            }
        }).onSameThread().check()
    }

    private fun choosePhotoFromGallery()
    {
        Dexter.withContext(this).withPermissions(
            android.Manifest.permission.READ_EXTERNAL_STORAGE,
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE).withListener(object:MultiplePermissionsListener {

            override fun onPermissionsChecked(report:MultiplePermissionsReport?)
            {
                if(report!!.areAllPermissionsGranted())
            {
                val galleryintent=Intent(Intent.ACTION_PICK,
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                startActivityForResult(galleryintent,
                    GALLERY
                )

            }
            }
            override fun onPermissionRationaleShouldBeShown(permissions:MutableList<PermissionRequest>,token: PermissionToken) {
                showrationalDialogForPermission()
            }
        }).onSameThread().check()
    }

    private fun showrationalDialogForPermission()
    {
        AlertDialog.Builder(this).setMessage(""+"Permission are turned off").setPositiveButton("Go to Settings")
        {_,_ ->
            try {
                val intent=Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                val uri= Uri.fromParts("package",packageName,null)
                intent.data=uri
                startActivity(intent)
            }
            catch (e:ActivityNotFoundException)
            {
                e.printStackTrace()
            }
        }.setNegativeButton("cancel"){dialog, _ -> dialog.dismiss()  }.show()

    }

    //saving the file to internal storage or a directory by compressing the image
    private fun saveImageToInternalStorage(bitmap:Bitmap):Uri
    {
        val wrapper=ContextWrapper(applicationContext)
        var file=wrapper.getDir(IMAGE_DIRECTORY,Context.MODE_PRIVATE)
        file= File(file,"${UUID.randomUUID()}.jpg")
        try{
            val stream:OutputStream=FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.JPEG,100,stream)
            stream.flush()
            stream.close()
        }catch(e:IOException)
        {
            e.printStackTrace()
        }
        return Uri.parse(file.absolutePath)
    }
    companion object{
        private const val GALLERY=1
        private const val CAMERA=2
        private const val IMAGE_DIRECTORY="HappyPlacesImages"
    }
}