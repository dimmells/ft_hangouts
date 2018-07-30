package com.example.dmelnyk.ft_hangouts

import android.content.Intent
import android.graphics.Bitmap
import android.media.MediaScannerConnection
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_add_contact.*
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.util.*

class AddContactActivity : AppCompatActivity() {

    private var GALLERY = 1
    private var CAMERA = 2
    private var IMAGE_DIRECTORY = "/ft_hangouts_image"
    private var newContact = Contact()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_contact)
        supportActionBar?.setTitle("Add contact")
    }

    fun saveContact(view: View) {
        val context = this
        newContact.first_name = first_name.text.toString()
        newContact.last_name = last_name.text.toString()
        newContact.phone_number = phone_number.text.toString()
        newContact.email = email.text.toString()

        var db = DBHandler(context)
        db.addContactToDb(newContact)
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

    fun addPhoto(view: View) {
        val dialog = AlertDialog.Builder(this)
        val dialogView = layoutInflater.inflate(R.layout.photo_source_dialog, null)
        dialog.setView(dialogView)
        dialog.show()
    }

    fun fromCamera(view: View) {
        Toast.makeText(applicationContext, "Camera", Toast.LENGTH_SHORT).show()
        val camera = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(camera, CAMERA)
    }

    fun fromGallery(view: View) {
        Toast.makeText(applicationContext, "Gallery", Toast.LENGTH_SHORT).show()
        val galleryIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(galleryIntent, GALLERY)
    }

    public override fun onActivityResult(requestCode:Int, resultCode:Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == GALLERY)
        {
            if (data != null)
            {
                val contentURI = data!!.data
                try
                {
                    val bitmap = MediaStore.Images.Media.getBitmap(this.contentResolver, contentURI)
                    val path = saveImage(bitmap)
                    Toast.makeText(this@AddContactActivity, "Image Saved!", Toast.LENGTH_SHORT).show()
                    photo!!.setImageBitmap(bitmap)

                }
                catch (e: IOException) {
                    e.printStackTrace()
                    Toast.makeText(this@AddContactActivity, "Failed!", Toast.LENGTH_SHORT).show()
                }

            }

        }
        else if (requestCode == CAMERA)
        {
            val thumbnail = data!!.extras!!.get("data") as Bitmap
            photo!!.setImageBitmap(thumbnail)
            saveImage(thumbnail)
            Toast.makeText(this@AddContactActivity, "Image Saved!", Toast.LENGTH_SHORT).show()
        }
    }

    fun saveImage(myBitmap: Bitmap):String {
        var pathname = (Environment.getExternalStorageDirectory()).toString() + IMAGE_DIRECTORY
        val bytes = ByteArrayOutputStream()
        myBitmap.compress(Bitmap.CompressFormat.JPEG, 90, bytes)
        val wallpaperDirectory = File(pathname)
        if (!wallpaperDirectory.exists())
            wallpaperDirectory.mkdirs()
        try
        {
            val filename = (Calendar.getInstance().getTimeInMillis()).toString() + ".jpg"
            val f = File(wallpaperDirectory, (filename))
            f.createNewFile()
            val fo = FileOutputStream(f)
            fo.write(bytes.toByteArray())
            MediaScannerConnection.scanFile(this,
                    arrayOf(f.getPath()),
                    arrayOf("image/jpeg"), null)
            fo.close()
            newContact.photo = filename
            return f.getAbsolutePath()
        }
        catch (e1: IOException) {
            e1.printStackTrace()
        }

        return ""
    }
}
