package com.project.himanshu.equeue.repo

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.google.gson.stream.JsonReader
import android.content.Context
import com.google.firebase.database.*
import com.project.himanshu.equeue.data.QrCode
import com.project.himanshu.equeue.data.QrCodeReadRespons


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


    suspend fun validateQR(code: String, contect: Context) : QrCodeReadRespons {
        var qrJson = ""
        var result: QrCodeReadRespons? = null
        var ticketPrice = getTicketPrice(code)
        val ticketCategory = code[2]

        var qrCode = QrCode(code)

        if (ticketCategory == 'A' && ticketPrice == "10000") {
            qrJson = QR10000
        } else if ((ticketCategory == 'B') && (ticketPrice == "5000")) {
            qrJson = QR5000
        } else if ((ticketCategory == 'C') && (ticketPrice == "2500")) {
            qrJson = QR2500
        } else if ((ticketCategory == 'D') && (ticketPrice == "1500")) {
            qrJson = QR1500
        } else if ((ticketCategory == 'E') && (ticketPrice == "1000")) {
            qrJson = QR1000
        }

        contect.assets.open(qrJson).use { inputStream ->
            JsonReader(inputStream.reader()).use { jsonReader ->
                val codes = object : TypeToken<List<QrCode>>() {}.type
                val codeList: List<QrCode> = Gson().fromJson(jsonReader, codes)
                result?.code_id = code
                if (codeList.contains(qrCode)) {
                    result?.code_reading_status = true
                    result?.ticket_price = ticketPrice
                } else {
                    result?.code_reading_status = false
                    result?.ticket_price = ticketPrice
                }

            }
        }

        return result!!
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


    fun getTicketPrice(code: String): String {
        var ticketCategory = code.subSequence(3, 8)
        var output = ticketCategory.replace("B".toRegex(), "")
        return output.trim().reversed()

    }

}

