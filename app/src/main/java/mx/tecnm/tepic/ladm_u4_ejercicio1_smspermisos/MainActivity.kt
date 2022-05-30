package mx.tecnm.tepic.ladm_u4_ejercicio1_smspermisos

import android.R
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION
import android.widget.ArrayAdapter
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.view.size
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.getValue
import mx.tecnm.tepic.ladm_u4_ejercicio1_smspermisos.databinding.ActivityMainBinding
import java.io.File
import java.io.InputStreamReader
import java.io.OutputStreamWriter


class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding
    val siPermiso = 1
    val siPermisoReceiver = 2
    var listaIDs = ArrayList<String>()
    val PICK_PDF_FILE = 2
    //val xlWb = XSSFWorkbook()
    //val xlWs = xlWb.createSheet()



    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)


        if(ActivityCompat.checkSelfPermission(this,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,
                arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE),2)
        }
        if(ActivityCompat.checkSelfPermission(this,
                android.Manifest.permission.MANAGE_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,
                arrayOf(android.Manifest.permission.MANAGE_EXTERNAL_STORAGE),2)
        }
        if(ActivityCompat.checkSelfPermission(this,
                android.Manifest.permission.READ_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,
                arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE),2)
        }


        //---------------------------
        val consulta = FirebaseDatabase.getInstance().getReference().child("smsregistro")

        val postListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                var datos = ArrayList<String>()
                listaIDs.clear()

                for(data in snapshot.children!!){
                    val id = data.key
                    listaIDs.add(id!!)
                    val tel = data.getValue<Sms>()!!.telefono
                    val men = data.getValue<Sms>()!!.mensaje
                    val fec = data.getValue<Sms>()!!.fecha
                    datos.add("Telefono: ${tel}\n Mensaje: ${men}\n" +
                            " Fecha: ${fec}")
                }
                mostrarLista(datos)
            }

            override fun onCancelled(error: DatabaseError) {

            }

        }

        consulta.addValueEventListener(postListener)
        //---------------------------


        if(ActivityCompat.checkSelfPermission(this,
            android.Manifest.permission.RECEIVE_SMS)!= PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,
            arrayOf(android.Manifest.permission.RECEIVE_SMS),siPermisoReceiver)
        }


        binding.button.setOnClickListener {
            guardarEnArchivo()
            guardarinterno()
        }
        binding.leerexcel.setOnClickListener {
            //leerDesdeArchivo()
            abrirexcel()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int,permissions: Array<out String>,grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(requestCode == siPermiso){
            //AQUI VA EL CODIGO
        }
        if(requestCode == siPermisoReceiver){
            mensajeRecibir()
        }

    }

    private fun mensajeRecibir() {
        AlertDialog.Builder(this)
            .setMessage("SE OTORGO RECIBIR")
            .show()
    }

    fun mostrarLista(datos:ArrayList<String>){
        binding.lista.adapter = ArrayAdapter<String>(this, R.layout.simple_list_item_1,datos)
    }

    fun guardarEnArchivo() {
        try{
            val archivo = OutputStreamWriter(openFileOutput("archivo.csv", 0))
            var cadena = ""
            (0.. binding.lista.size-1).forEach {
                cadena = cadena + binding.lista.getItemAtPosition(it).toString()+",\n"
            }
            cadena.replace("\n","")
            archivo.write(cadena)
            archivo.flush()
            archivo.close()
            android.app.AlertDialog.Builder(this)
                .setMessage("Se guardo correctamente.").show()
        }catch (e: Exception){
            android.app.AlertDialog.Builder(this)
                .setMessage(e.message).show()
        }
    }
    fun guardarinterno(){



        val path = this.getExternalFilesDir(null)

        val letDirectory = File(path, "RegistrosSMS")
        letDirectory.mkdirs()

        val file = File(letDirectory, "registros.csv")

        if(file.exists()){
            file.delete()
        }else{
            File(letDirectory, "registros.csv")
        }

        var cadena = ""
        (0.. binding.lista.size-1).forEach {
            cadena = cadena + binding.lista.getItemAtPosition(it).toString()+",\n"
        }
        cadena.replace("\n","")

        file.appendText(cadena)
    }
    fun leerDesdeArchivo() {
        try{
            val archivo = InputStreamReader(openFileInput("archivo.csv"))
            var listaContenido = archivo.readLines()


            var cadena = ""
            (0.. listaContenido.size-1).forEach {
                cadena = cadena + listaContenido.get(it)
            }


            AlertDialog.Builder(this)
                .setMessage(cadena)
                .show()
        }catch (e:Exception){
            android.app.AlertDialog.Builder(this)
                .setMessage(e.message).show()
        }
    }
    fun abrirexcel(){

        AlertDialog.Builder(this)
            .setMessage("EL ARCHIVO .csv SE ENCUENTRA EN EL ALMACENAMIENTO INTERNO DEL TELEFONO," +
                    "PARA INGRESAR ES NECESARIO ABRIR EL STORAGE Y DESDE LAS OPCIONES EN LA PARTE" +
                    "SUPERIOR DERECHA SELECCIONAR 'SHOW INTERNAL STORAGE', AL HACER ESTO NOS APARECERA" +
                    "ADEMAS DE LA TARJETA SD EL ALMACENAMIENTO INTERNO DEL TELEFONO, DESDE AQUI SEGUIREMOS LA" +
                    "SIGUIENTE RUTA: Android/data/mx.tecm.tepic.ladm_u4_practica1_sms_juan_soltero")
            .show()

    }


}