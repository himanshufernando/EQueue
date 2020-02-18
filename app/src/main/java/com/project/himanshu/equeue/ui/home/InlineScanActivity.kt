package com.project.himanshu.equeue.ui.home

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri

import android.os.Build

import android.os.Bundle
import java.util.Calendar
import android.os.VibrationEffect
import android.os.Vibrator
import android.view.View
import android.view.Window
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.observe
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.google.firebase.FirebaseError
import com.google.firebase.database.*
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.google.gson.stream.JsonReader
import com.google.zxing.ResultPoint
import com.journeyapps.barcodescanner.BarcodeCallback
import com.journeyapps.barcodescanner.BarcodeResult
import com.journeyapps.barcodescanner.CaptureManager
import com.project.himanshu.equeue.Equeue
import com.project.himanshu.equeue.R
import com.project.himanshu.equeue.data.QrCode
import com.project.himanshu.equeue.data.QrCodeReadRespons
import com.project.himanshu.equeue.data.QrCodeWrite
import com.project.himanshu.equeue.data.UserRespons
import com.project.himanshu.equeue.databinding.ActivityHomeBinding
import com.project.himanshu.equeue.services.network.AppPrefs
import com.project.himanshu.equeue.services.network.MediaplayerHandler
import com.project.himanshu.equeue.viewmodels.HomeViewmodels
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.dialog_chart.*
import java.sql.Date
import java.text.SimpleDateFormat
import java.time.LocalDateTime


class InlineScanActivity : AppCompatActivity() {
    lateinit var captureManager: CaptureManager
    var torchState: Boolean = false
    var readedCode = ""

    val sdk = Build.VERSION.SDK_INT
    lateinit var dialogDublicte: AlertDialog
    lateinit var dialogError: AlertDialog

    lateinit var dialogDetails: Dialog

    var myRef: DatabaseReference? = null
    var database: FirebaseDatabase? = null
    var userID: String = ""

    val DATE_FORMAT_1 = "hh:mm a";

    lateinit var dateFormat: SimpleDateFormat

    lateinit var binding: ActivityHomeBinding

    lateinit var textAnim: Animation

    companion object {
        const val QR1000 = "qr_1000.json"
        const val QR1500 = "qr_1500.json"
        const val QR2500 = "qr_2500.json"
        const val QR5000 = "qr_5000.json"
        const val QR10000 = "qr_10000.json"
    }


    private val viewmodel: HomeViewmodels by viewModels { HomeViewmodels.LiveDataVMFactory }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_home)
        binding.home = viewmodel
        captureManager = CaptureManager(this, barcodeView)
        captureManager.initializeFromIntent(intent, savedInstanceState)



        textAnim = AnimationUtils.loadAnimation(this, R.anim.text)
        userID = AppPrefs.getUserID(this)
        dateFormat = SimpleDateFormat(DATE_FORMAT_1)

        progressbar.visibility = View.INVISIBLE
        progressbar_floating.visibility = View.INVISIBLE

        database = FirebaseDatabase.getInstance()

        barcodeView.decodeContinuous(object : BarcodeCallback {
            override fun barcodeResult(result: BarcodeResult?) {
                result?.let {
                    /* txtResult.text = it.text*/
                    if (readedCode != it.text) {

                        if (::dialogError.isInitialized) {
                            if (dialogError != null) {
                                dialogError.dismiss()
                            }
                        }


                        if (::dialogDublicte.isInitialized) {
                            if (dialogDublicte != null) {
                                dialogDublicte.dismiss()
                            }
                        }


                        validateQR(it.text)

                        try {
                            val defaultSoundUri = Uri.parse(
                                "android.resource://" + application.packageName + "/" + R.raw.editted_beep
                            )
                            val mediaplayerHandler =
                                MediaplayerHandler.getInstance(application.applicationContext)
                            mediaplayerHandler.playSound(defaultSoundUri, false)
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }


                        val vib: Vibrator = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator

                        if (vib.hasVibrator()) {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                // void vibrate (VibrationEffect vibe)
                                vib.vibrate(
                                    VibrationEffect.createOneShot(
                                        1,
                                        VibrationEffect.DEFAULT_AMPLITUDE
                                    )
                                )
                            } else {
                                // This method was deprecated in API level 26
                                vib.vibrate(100)
                            }
                        }
                    }
                    readedCode = it.text

                }
            }

            override fun possibleResultPoints(resultPoints: MutableList<ResultPoint>?) {
            }


        })


        btnTorch.setOnClickListener {
            if (torchState) {
                torchState = false
                barcodeView.setTorchOff()
            } else {
                torchState = true
                barcodeView.setTorchOn()
            }
        }



        floatingActionButton.setOnClickListener {

            progressbar_floating.visibility = View.VISIBLE
            floatingActionButton.visibility = View.INVISIBLE

            Toast.makeText(this, "Data loading,Please wait !!!", Toast.LENGTH_SHORT)
                .show()

            pie1000()


        }


    }

    override fun onPause() {
        super.onPause()
        captureManager.onPause()
    }

    override fun onResume() {
        super.onResume()
        captureManager.onResume()
    }

    override fun onDestroy() {
        super.onDestroy()
        captureManager.onDestroy()
    }


    private fun message(tital: String, message: String) {

        val builder = android.app.AlertDialog.Builder(this)
        builder.setTitle(tital)
        builder.setMessage(message)
        builder.setCancelable(false)
        builder.setPositiveButton("OK") { _, _ ->
            return@setPositiveButton
        }
        val dialog: android.app.AlertDialog = builder.create()
        dialog.show()
    }


    fun validateQR(code: String) {


        var qrJson = ""
        var readRespons: QrCodeReadRespons = QrCodeReadRespons(code, false, "", "")
        if (code == "") {
            message("QR Reading", "QR Reading error ,Please try again")
            return
        }


        var ticketPrice = getTicketPrice(code)
        val ticketCategory = code[2]

        var qrCode = QrCode(code)



        if (ticketCategory == 'A' && ticketPrice == "10000") {
            myRef = database?.getReference("10000")
            qrJson = QR10000
        } else if ((ticketCategory == 'B') && (ticketPrice == "5000")) {
            myRef = database?.getReference("5000")
            qrJson = QR5000
        } else if ((ticketCategory == 'C') && (ticketPrice == "2500")) {
            myRef = database?.getReference("2500")
            qrJson = QR2500
        } else if ((ticketCategory == 'D') && (ticketPrice == "1500")) {
            myRef = database?.getReference("1500")
            qrJson = QR1500
        } else if ((ticketCategory == 'E') && (ticketPrice == "1000")) {
            myRef = database?.getReference("1000")
            qrJson = QR1000
        }

        readRespons.code_id = code

        this.assets.open(qrJson).use { inputStream ->
            JsonReader(inputStream.reader()).use { jsonReader ->
                val codes = object : TypeToken<List<QrCode>>() {}.type
                val codeList: List<QrCode> = Gson().fromJson(jsonReader, codes)

                return if (codeList.contains(qrCode)) {
                    QRValidateFromFireBase(
                        code,
                        ticketCategory.toString(),
                        ticketPrice,
                        ticketCategory.toString(),
                        myRef!!
                    )
                } else {
                    readRespons.code_reading_status = false
                    readRespons.ticket_price = ticketPrice
                    readRespons.ticket_category = ""
                    setErrorMessage()
                }

            }
        }


    }

    fun getTicketPrice(code: String): String {
        var ticketCategory = code.subSequence(3, 8)
        var output = ticketCategory.replace("B".toRegex(), "")
        return output.trim().reversed()

    }


    private fun QRValidateFromFireBase(
        code: String,
        cat: String,
        tPrice: String,
        tCat: String,
        ref: DatabaseReference
    ) {

        progressbar.visibility = View.VISIBLE

        var readRespons: QrCodeReadRespons = QrCodeReadRespons(code, false, "", "")
        val query: Query = myRef!!.orderByChild("qrcode").equalTo(code)

        query.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.children.count() == 0) {
                    readRespons.code_reading_status = true
                    readRespons.ticket_price = tPrice
                    readRespons.ticket_category = tCat
                    progressbar.visibility = View.INVISIBLE
                    setBackgroundToLayout(readRespons)
                    addQRcodeToFirebase(code, ref)
                } else {
                    progressbar.visibility = View.INVISIBLE
                    setErrorMessageForDuplicate()
                    addDuplicateQRcodeToFirebase(code, tPrice, tCat)
                }

            }

            override fun onCancelled(error: DatabaseError) {
                binding.progressbar.visibility = View.INVISIBLE
                message("Firebase Error", error.toString())
            }
        })


    }


    private fun addQRcodeToFirebase(qrcode: String, ref: DatabaseReference) {
        var newRef = ref.push()
        var cq = QrCodeWrite("", qrcode, getCurentTime(), userID)
        newRef.setValue(cq)
    }


    private fun addDuplicateQRcodeToFirebase(
        qrcode: String,
        ticketPrice: String,
        ticketCategory: String
    ) {

        if (ticketCategory == "A" && ticketPrice == "10000") {
            myRef = database?.getReference("10000Duplicate")
        } else if ((ticketCategory == "B") && (ticketPrice == "5000")) {
            myRef = database?.getReference("5000Duplicate")
        } else if ((ticketCategory == "C") && (ticketPrice == "2500")) {
            myRef = database?.getReference("2500Duplicate")
        } else if ((ticketCategory == "D") && (ticketPrice == "1500")) {
            myRef = database?.getReference("1500Duplicate")
        } else if ((ticketCategory == "E") && (ticketPrice == "1000")) {
            myRef = database?.getReference("1000Duplicate")
        }


        var newRef = myRef?.push()
        var cq = QrCodeWrite("", qrcode, getCurentTime(), userID)
        newRef?.setValue(cq)
    }


    private fun setBackgroundToLayout(respond: QrCodeReadRespons) {
        var ticDrawable = R.drawable.bg_round

        textView_tickets_price.text = respond.ticket_price

        textAnim.reset()

        textView_tickets_price.clearAnimation()
        textView_tickets_price.startAnimation(textAnim)


        when {
            respond.ticket_category.equals("A") -> {
                ticDrawable = R.drawable.bg_ticket_10000
            }
            respond.ticket_category.equals("B") -> {
                ticDrawable = R.drawable.bg_ticket_5000
            }
            respond.ticket_category.equals("C") -> {
                ticDrawable = R.drawable.bg_ticket_2500
            }
            respond.ticket_category.equals("D") -> {
                ticDrawable = R.drawable.bg_ticket_1500
            }
            respond.ticket_category.equals("E") -> {
                ticDrawable = R.drawable.bg_ticket_1000
            }
        }
        if (sdk < Build.VERSION_CODES.JELLY_BEAN) {
            relativeLayout_ticket.setBackgroundDrawable(
                ContextCompat.getDrawable(
                    applicationContext,
                    ticDrawable
                )
            )
        } else {
            relativeLayout_ticket.background =
                ContextCompat.getDrawable(applicationContext, ticDrawable)
        }

    }

    fun setErrorMessage() {
        if (::dialogError.isInitialized) {
            if (dialogError != null) {
                dialogError.dismiss()
            }
        }

        val builder = AlertDialog.Builder(this)
        builder.setTitle("Error")
        builder.setMessage("Not valid ticket ,Please recheck")
        builder.setCancelable(false)
        builder.setPositiveButton(android.R.string.yes) { dialog, which ->
            readedCode = ""
        }

        dialogError = builder.create()
        dialogError.show()

    }


    fun setErrorMessageForDuplicate() {


        if (::dialogDublicte.isInitialized) {
            if (dialogDublicte != null) {
                dialogDublicte.dismiss()
            }
        }


        val builder = AlertDialog.Builder(this)
        builder.setTitle("Error")
        builder.setMessage("This ticket is already check, Please recheck the Ticket")
        builder.setCancelable(false)
        builder.setPositiveButton(android.R.string.yes) { dialog, which ->

        }
        dialogDublicte = builder.create()
        dialogDublicte.window!!.setBackgroundDrawable(ColorDrawable(Color.RED))

        dialogDublicte.show()

    }


    private fun getCurentTime(): String {
        return Calendar.getInstance().time.toString()
    }


    private fun showPieChartDialog(
        value1000: Float,
        value1500: Float,
        value2500: Float,
        value5000: Float,
        value10000: Float
    ) {

        if (::dialogDetails.isInitialized) {
            if (dialogDetails != null) {
                dialogDetails.dismiss()
            }
        }


        dialogDetails = Dialog(this)

        dialogDetails.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialogDetails.window!!.setBackgroundDrawableResource(android.R.color.transparent)
        dialogDetails.setContentView(R.layout.dialog_chart)
        dialogDetails.setCancelable(true)

        var pie: PieChart = dialogDetails.findViewById(R.id.pieChart_2500)


        val yVals = ArrayList<PieEntry>()
        yVals.add(PieEntry(value1000, ""))
        yVals.add(PieEntry(value1500, ""))
        yVals.add(PieEntry(value2500, ""))
        yVals.add(PieEntry(value5000, ""))
        yVals.add(PieEntry(value10000, ""))


        val dataSet = PieDataSet(yVals, "")
        dataSet.valueTextSize = 17f
        dataSet.valueTextColor = Color.WHITE

        val colors = java.util.ArrayList<Int>()


        colors.add(resources.getColor(R.color.colorTicket1000))
        colors.add(resources.getColor(R.color.colorTicket1500))
        colors.add(resources.getColor(R.color.colorTicket2500))
        colors.add(resources.getColor(R.color.colorTicket5000))
        colors.add(resources.getColor(R.color.colorTicket10000))


        dataSet.colors = colors
        val data = PieData(dataSet)
        pie.data = data
        pie.isDrawHoleEnabled = false
        pie.legend.isEnabled = false
        pie.description.isEnabled = false


        progressbar_floating.visibility = View.INVISIBLE
        floatingActionButton.visibility = View.VISIBLE


        dialogDetails.show()

    }


    private fun pie1000() {

        var thousand = 0f

        var ref = database?.getReference("1000")
        ref?.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                thousand = dataSnapshot.children.count().toFloat()
                pie1500(thousand)

            }

            override fun onCancelled(error: DatabaseError) {

            }
        })


    }


    private fun pie1500(value1000: Float) {

        var thousandfive = 0f

        var ref = database?.getReference("1500")
        ref?.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                thousandfive = dataSnapshot.children.count().toFloat()
                pie2500(value1000, thousandfive)
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })


    }


    private fun pie2500(value1000: Float, value1500: Float) {

        var ref = database?.getReference("2500")
        ref?.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                var twothousandfive = 0f
                twothousandfive = dataSnapshot.children.count().toFloat()
                pie5000(value1000, value1500, twothousandfive)

            }

            override fun onCancelled(error: DatabaseError) {

            }
        })


    }


    private fun pie5000(value1000: Float, value1500: Float, value2500: Float) {

        var fivethousand = 0f

        var ref = database?.getReference("5000")
        ref?.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                fivethousand = dataSnapshot.children.count().toFloat()
                pie10000(value1000, value1500, value2500, fivethousand)
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })


    }


    private fun pie10000(value1000: Float, value1500: Float, value2500: Float, value5000: Float) {
        var tenyhousand = 0f
        var ref = database?.getReference("10000")
        ref?.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                tenyhousand = dataSnapshot.children.count().toFloat()
                showPieChartDialog(value1000, value1500, value2500, value5000, tenyhousand)
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })


    }


}

