package com.example.dmelnyk.ft_hangouts

import android.content.Intent
import android.graphics.Bitmap
import android.media.MediaScannerConnection
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.support.v7.app.AlertDialog
import android.util.Log
import android.view.View
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_add_contact.*
import kotlinx.android.synthetic.main.activity_edit_contact.*
import kotlinx.android.synthetic.main.contact_data.*
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.util.*

class EditContactActivity : AppCompatActivity() {

    private var GALLERY = 1
    private var CAMERA = 2
    private var contact = Contact()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_contact)
//        supportActionBar?.setTitle("Edit contact")
        getInfo()
        edit_saveBtn.setOnClickListener {
            editContact(loadContact())
        }

    }

    fun getInfo() {
        contact = intent.getSerializableExtra("contact") as Contact

        edit_first_name.setText(contact.first_name)
        edit_last_name.setText(contact.last_name)
        edit_phone_number.setText(contact.phone_number)
        edit_email.setText(contact.email)
    }

    fun loadContact() : Contact {

        contact.first_name = edit_first_name.text.toString()
        contact.last_name = edit_last_name.text.toString()
        contact.phone_number = edit_phone_number.text.toString()
        contact.email = edit_email.text.toString()

        return contact
    }

    fun editContact(contact: Contact) {
        val db = DBHandler(this)
        db.updateContact(contact)
        Toast.makeText(this, "Updating", Toast.LENGTH_SHORT).show()
        val intent = Intent(this, ContactActivity::class.java)
        intent.putExtra("contact", contact)
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
                    Toast.makeText(this@EditContactActivity, "Image Saved!", Toast.LENGTH_SHORT).show()
                    photo!!.setImageBitmap(bitmap)

                }
                catch (e: IOException) {
                    e.printStackTrace()
                    Toast.makeText(this@EditContactActivity, "Failed!", Toast.LENGTH_SHORT).show()
                }

            }

        }
        else if (requestCode == CAMERA)
        {
            val thumbnail = data!!.extras!!.get("data") as Bitmap
            photo!!.setImageBitmap(thumbnail)
            saveImage(thumbnail)
            Toast.makeText(this@EditContactActivity, "Image Saved!", Toast.LENGTH_SHORT).show()
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
        private val IMAGE_DIRECTORY = "/ft_hangouts_image"
    }
}
