package mx.tecnm.tepic.ladm_u4_ejercicio1_smspermisos

import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
data class Sms (val telefono: String?=null, val mensaje: String?=null,val fecha: String?=null) {
}