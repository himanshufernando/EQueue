package com.project.himanshu.equeue.repo

import android.app.ActivityOptions
import android.content.Context
import android.content.Intent
import com.google.gson.stream.JsonReader
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

import com.google.firebase.database.*
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.project.himanshu.equeue.Equeue
import com.project.himanshu.equeue.data.QrCode
import com.project.himanshu.equeue.data.User
import java.lang.Exception

class HomeRepo {

    var myRef: DatabaseReference? = null
    var database: FirebaseDatabase? = null

    companion object {
        const val QR1000 = "qr_1000.json"
        const val QR1500 = "qr_1500.json"
        const val QR2500 = "qr_2500.json"
        const val QR5000 = "qr_5000.json"
        const val QR10000 = "qr_10000.json"
    }


    suspend fun validateQR(code: String, contect: Context) {
        var qrJson = ""
        var ticketPrice = getTicketPrice(code)
        val ticketCategory = code[2]



        if(ticketCategory.equals("A") && ticketPrice == "10000"){
            println("qqqqq 10000 :$qrJson")
            qrJson = QR10000
        }else if((ticketCategory.equals("B")) && (ticketPrice == "5000")){
            println("qqqqq 5000 :$qrJson")
            qrJson = QR5000
        }else if((ticketCategory.equals("C")) && (ticketPrice == "2500")){
            println("qqqqq 2500 :$qrJson")
            qrJson = QR2500
        }else if((ticketCategory.equals("D")) && (ticketPrice == "1500")){
            println("qqqqq 1500 :$qrJson")
            qrJson = QR1500
        }else if((ticketCategory.equals("E")) && (ticketPrice == "1000")){
            println("qqqqq 1000 :$qrJson")
            qrJson = QR1000
        }

        println("qqqqq ticketCategory :"+ticketCategory+"P")
        println("qqqqq ticketPrice :"+ticketPrice+"P")

        println("qqqqq qrJson :$qrJson")

        contect.assets.open(qrJson).use { inputStream ->
            JsonReader(inputStream.reader()).use { jsonReader ->
                val codes = object : TypeToken<List<QrCode>>() {}.type
                val codeList: List<QrCode> = Gson().fromJson(jsonReader, codes)

                for (codeInJson in codeList) {
                    println("qqqqq codeInJson :$codeInJson")
                }


            }
        }


    }

    /* suspend fun loginValidation(userName: String, password: String): User {
         var resultUser: User? = null

         myRef = database?.getReference("User")
         val query: Query = myRef!!.orderByChild("username_password").equalTo(userName+password)
         query.addListenerForSingleValueEvent(object : ValueEventListener {
             override fun onDataChange(dataSnapshot: DataSnapshot) {
                 val snapshotIterator = dataSnapshot.children
             }
             override fun onCancelled(databaseError: DatabaseError) {

             }
         })


         return resultUser!!

     }*/



    fun getTicketPrice( code : String) : String{
        var ticketCategory = code.subSequence(3,8)
        var output = ticketCategory.replace("B".toRegex(),"")
        return output.trim().reversed()

    }

}

