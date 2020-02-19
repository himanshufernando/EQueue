package com.project.himanshu.equeue.repo

import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.project.himanshu.equeue.data.QrCodeWrite
import com.project.himanshu.equeue.data.db.OriginalDao
import com.project.himanshu.equeue.data.db.OriginalTickets
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentMap
import javax.inject.Inject


class HomeRepo @Inject constructor(val originalDao: OriginalDao, var database: FirebaseDatabase) {

    var myRef: DatabaseReference? = null


    companion object {
        const val QR1000 = "qr_1000.json"
        const val QR1500 = "qr_1500.json"
        const val QR2500 = "qr_2500.json"
        const val QR5000 = "qr_5000.json"
        const val QR10000 = "qr_10000.json"
    }


    private var cachedTasks: ConcurrentMap<String, OriginalTickets>? = null

    suspend fun originalInsert(originaltickets: OriginalTickets) {
        cacheAndPerform(originaltickets) {
            coroutineScope {
                launch { originalDao.insertOriginal(it) }
            }
        }

    }


    suspend fun originalInsertFirebase(originaltickets: OriginalTickets) {
        cacheAndPerform(originaltickets) {
            coroutineScope {
                launch {

                    var ticketPrice = getTicketPrice(originaltickets.qrcode)
                    val ticketCategory = originaltickets.qrcode[2]

                    if (ticketCategory == 'A' && ticketPrice == "10000") {
                        myRef = database?.getReference("10000")
                    } else if ((ticketCategory == 'B') && (ticketPrice == "5000")) {
                        myRef = database?.getReference("5000")
                    } else if ((ticketCategory == 'C') && (ticketPrice == "2500")) {
                        myRef = database?.getReference("2500")
                    } else if ((ticketCategory == 'D') && (ticketPrice == "1500")) {
                        myRef = database?.getReference("1500")
                    } else if ((ticketCategory == 'E') && (ticketPrice == "1000")) {
                        myRef = database?.getReference("1000")
                    }

                    var newRef = myRef?.push()
                    var cq = QrCodeWrite(
                        originaltickets.qrcode,
                        originaltickets.qrcode,
                        originaltickets.time,
                        originaltickets.user
                    )




                    newRef?.setValue(
                        cq,
                        DatabaseReference.CompletionListener { databaseError, databaseReference ->
                            if (databaseError != null) {
                            } else {
                            }
                        })
                }
            }
        }

    }

    suspend fun qrUpdateStstus(qrcode: String) {
        coroutineScope {
            launch {
                originalDao.updateQR(qrcode)
            }
        }
    }


    fun getFriend() = originalDao.getFriendsList()

    private fun cacheTask(originaltickets: OriginalTickets): OriginalTickets {
        val cachedTask = OriginalTickets(
            originaltickets.id,
            originaltickets.code_id,
            originaltickets.qrcode,
            originaltickets.tcat,
            originaltickets.time,
            originaltickets.user,
            originaltickets.isupdate
        )
        if (cachedTasks == null) {
            cachedTasks = ConcurrentHashMap()
        }
        cachedTasks?.put(cachedTask.code_id, cachedTask)
        return cachedTask
    }

    private inline fun cacheAndPerform(
        originaltickets: OriginalTickets,
        perform: (OriginalTickets) -> Unit
    ) {
        val cachedTask = cacheTask(originaltickets)
        perform(cachedTask)
    }

    fun getTicketPrice(code: String): String {
        var ticketCategory = code.subSequence(3, 8)
        var output = ticketCategory.replace("B".toRegex(), "")
        return output.trim().reversed()

    }

}

