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

        var readRespons: QrCodeReadRespons = QrCodeReadRespons(code,false,"","")
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

        readRespons.code_id = code

        contect.assets.open(qrJson).use { inputStream ->
            JsonReader(inputStream.reader()).use { jsonReader ->
                val codes = object : TypeToken<List<QrCode>>() {}.type
                val codeList: List<QrCode> = Gson().fromJson(jsonReader, codes)

                return if (codeList.contains(qrCode)) {
                    readRespons.code_reading_status = true
                    readRespons.ticket_price = ticketPrice
                    readRespons.ticket_category = ticketCategory.toString()
                    readRespons

                } else {
                    readRespons.code_reading_status = false
                    readRespons.ticket_price = ticketPrice
                    readRespons.ticket_category =""
                    readRespons

                }

            }
        }


    }

    fun getTicketPrice(code: String): String {
        var ticketCategory = code.subSequence(3, 8)
        var output = ticketCategory.replace("B".toRegex(), "")
        return output.trim().reversed()

    }

}

