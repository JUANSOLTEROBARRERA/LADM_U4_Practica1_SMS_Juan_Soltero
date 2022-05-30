package mx.tecnm.tepic.ladm_u4_ejercicio1_smspermisos

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.telephony.SmsMessage
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat.startActivityForResult
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import java.time.LocalDateTime


/*
    RECEIVER = evento u oyente de android que permite la lectura de eventos del
    sistema operativo.
 */

class SmsReceiver : BroadcastReceiver() {

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onReceive(context: Context, intent: Intent) {
        val extras = intent.extras

        if(extras != null){
            var sms = extras.get("pdus") as Array<Any>//TIENE QUE SER EXACTO

            for(indice in sms.indices){
                var formato = extras.getString("format")

                var smsMensaje = if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                    SmsMessage.createFromPdu(sms[indice] as ByteArray, formato)
                } else{
                    SmsMessage.createFromPdu(sms[indice] as ByteArray)
                }

                var celularOrigen = smsMensaje.originatingAddress
                var contenidoSMS = smsMensaje.messageBody.toString()




                //GUARDAR EN TABLA REALTIME-----------------------

                val consulta = FirebaseDatabase.getInstance().getReference().child("smsregistro")

                var basedatos = Firebase.database.reference

                val current = LocalDateTime.now()
                val asist2 = current.toString().split("T")

                val smsrecibido = Sms(celularOrigen,contenidoSMS,asist2.get(0)+" "+asist2.get(1))//equivalente a hashmapof


                basedatos.child("smsregistro")
                    .push().setValue(smsrecibido)
                    .addOnSuccessListener {
                        Toast.makeText(context, "Se agregó un registro.", Toast.LENGTH_LONG)
                            .show()
                    }
                    .addOnFailureListener {
                        Toast.makeText(context, "HUBO UN ERROR", Toast.LENGTH_LONG)
                            .show()
                    }

                //------------------------------------------------


            }
        }
    }

}