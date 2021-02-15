package com.example.appcontactos

import android.app.Activity
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.ContactsContract
import android.provider.MediaStore
import android.widget.*
import androidx.core.app.ActivityCompat
import java.util.jar.Manifest

class MainActivity : AppCompatActivity() {

    private val REQUEST_CAMERA = 1001

    var foto: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        abrirCamara()

        if(ActivityCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.WRITE_CONTACTS), 1)
        }

        val btnSave = findViewById<Button>(R.id.btnSave)
        val txtNombre = findViewById<TextView>(R.id.txtNombre)
        val txtApellido = findViewById<TextView>(R.id.txtApellido)
        val txtNumero = findViewById<TextView>(R.id.txtNumero)
        val txtEmail = findViewById<TextView>(R.id.txtEmail)

        val intent = Intent(ContactsContract.Intents.Insert.ACTION).apply {
            type = ContactsContract.RawContacts.CONTENT_TYPE
        }

        btnSave.setOnClickListener {
            var nombre = txtNombre.text.toString()
            var apellido = txtApellido.text.toString()
            var numero = txtNumero.text.toString()
            var email = txtEmail.text.toString()

            intent.apply {

                putExtra(ContactsContract.Intents.Insert.EMAIL, email)

                putExtra(
                        ContactsContract.Intents.Insert.EMAIL_TYPE,
                        ContactsContract.CommonDataKinds.Email.TYPE_WORK
                )

                putExtra(ContactsContract.Intents.Insert.PHONE, numero)

                putExtra(
                        ContactsContract.Intents.Insert.PHONE_TYPE,
                        ContactsContract.CommonDataKinds.Phone.TYPE_WORK
                )

                putExtra(ContactsContract.Intents.Insert.NAME, nombre + " " + apellido)
            }

            startActivity(intent)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if(requestCode == 1) {
            Toast.makeText(this, "Permiso para agregar contactos concedido", Toast.LENGTH_LONG).show()
        }
        when (requestCode) {
            REQUEST_CAMERA -> {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED)
                    openCamera()
                else
                    Toast.makeText(applicationContext, "No puedes acceder a la camara", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun abrirCamara(){
        val botonCamara = findViewById<ImageButton>(R.id.btnCamara)
        botonCamara.setOnClickListener(){
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (checkSelfPermission(android.Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED
                        || checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
                    val permisosCamara = arrayOf(android.Manifest.permission.CAMERA, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    requestPermissions(permisosCamara, REQUEST_CAMERA)
                }else
                    openCamera()
            }else
                openCamera()
        }
    }

    private fun openCamera(){
        val value = ContentValues()
        value.put(MediaStore.Images.Media.TITLE, "Nueva imagen")
        foto = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, value)
        val camaraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        camaraIntent.putExtra(MediaStore.EXTRA_OUTPUT, foto)
        startActivityForResult(camaraIntent, REQUEST_CAMERA)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        val imgFoto = findViewById<ImageView>(R.id.imageView)

        if (resultCode == Activity.RESULT_OK && requestCode == REQUEST_CAMERA) {
            imgFoto.setImageURI(foto)
        }
    }
}