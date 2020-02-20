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
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
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
import com.project.himanshu.equeue.data.db.OriginalTickets
import com.project.himanshu.equeue.databinding.ActivityHomeBinding
import com.project.himanshu.equeue.services.network.AppPrefs
import com.project.himanshu.equeue.services.network.MediaplayerHandler
import com.project.himanshu.equeue.viewmodels.HomeViewmodels
import com.tuyenmonkey.mkloader.MKLoader
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

    lateinit var originalTickets: OriginalTickets

    companion object {
        const val QR1000 = "qr_1000.json"
        const val QR1500 = "qr_1500.json"
        const val QR2500 = "qr_2500.json"
        const val QR5000 = "qr_5000.json"
        const val QR10000 = "qr_10000.json"
    }


    var val1000 = 0.0f
    var val1500 = 0.0f
    var val2500 = 0.0f
    var val5000 = 0.0f
    var val10000 = 0.0f

    var val1000Dup = 0.0f
    var val1500Dup = 0.0f
    var val2500Dup = 0.0f
    var val5000Dup = 0.0f
    var val10000Dup = 0.0f


    lateinit var textview_dub_1000: TextView
    lateinit var textview_dub_1500: TextView
    lateinit var textview_dub_2500: TextView
    lateinit var textview_dub_5000: TextView
    lateinit var textview_dub_10000: TextView

    lateinit var progressbar_dublicate : MKLoader




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

                    try {
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

                    }catch ( ex : Exception){

                        Toast.makeText(applicationContext, ex.toString(), Toast.LENGTH_SHORT).show()

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


            try {
                pie1000()
            }catch (ex : Exception){
                Toast.makeText(applicationContext, ex.toString(), Toast.LENGTH_SHORT).show()
            }



        }



        viewmodel.value1000.observe(this) { value ->
            val1000 = value
        }

        viewmodel.value1500.observe(this) { value ->
            val1500 = value
        }

        viewmodel.value2500.observe(this) { value ->
            val2500 = value
        }

        viewmodel.value5000.observe(this) { value ->
            val5000 = value
        }

        viewmodel.value10000.observe(this) { value ->
            val10000 = value
        }


        viewmodel.value1000Dup.observe(this) { value ->
            val1000Dup = value

            if (::dialogDetails.isInitialized) {
                if ((dialogDetails != null) && (dialogDetails.isShowing)) {
                    textview_dub_1000.text= "1000 Tickets - $value"
                }
            }


        }

        viewmodel.value1500Dup.observe(this) { value ->
            val1500Dup = value
            if (::dialogDetails.isInitialized) {
                if ((dialogDetails != null) && (dialogDetails.isShowing)) {
                    textview_dub_1500.text= "1500 Tickets - $value"
                }
            }
        }

        viewmodel.value2500Dup.observe(this) { value ->
            val2500Dup = value
            if (::dialogDetails.isInitialized) {
                if ((dialogDetails != null) && (dialogDetails.isShowing)) {
                    textview_dub_2500.text= "2500 Tickets - $value"
                }
            }
        }

        viewmodel.value5000Dup.observe(this) { value ->
            val5000Dup = value

            if (::dialogDetails.isInitialized) {
                if ((dialogDetails != null) && (dialogDetails.isShowing)) {
                    textview_dub_5000.text= "5000 Tickets - $value"
                }
            }
        }

        viewmodel.value10000Dup.observe(this) { value ->
            val10000Dup = value





            if (::dialogDetails.isInitialized) {
                if ((dialogDetails != null) && (dialogDetails.isShowing)) {
                    textview_dub_10000.text= "10000 Tickets - $value"

                    progressbar_dublicate.visibility = View.INVISIBLE
                }
            }
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
                    addQRcodeToFirebase(code, tPrice, tCat)
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


    private fun addQRcodeToFirebase(qrcode: String, tp: String, tc: String) {

        originalTickets = OriginalTickets(0, qrcode, qrcode, tc, getCurentTime(), userID, false)
        viewmodel.addQR(originalTickets)

        viewmodel.newsList.observe(this) { news ->
            news.onSuccess {
                it

            }
            news.onFailure {
                it

            }
        }


        viewmodel.qrFirebase.observe(this) { news ->
            news.onSuccess {
                it

            }
            news.onFailure {
                it

            }
        }


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


    private fun showPieChartDialog() {

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

        var pie: PieChart = dialogDetails.findViewById(R.id.pieChart)
        var pie1000: PieChart = dialogDetails.findViewById(R.id.pieChart_1000)
        var pie1500: PieChart = dialogDetails.findViewById(R.id.pieChart_1500)
        var pie2500: PieChart = dialogDetails.findViewById(R.id.pieChart_2500)
        var pie5000: PieChart = dialogDetails.findViewById(R.id.pieChart_5000)
        var pie10000: PieChart = dialogDetails.findViewById(R.id.pieChart_10000)



        textview_dub_1000 = dialogDetails.findViewById(R.id.textview_dub_1000)
        textview_dub_1500 = dialogDetails.findViewById(R.id.textview_dub_1500)
        textview_dub_2500 = dialogDetails.findViewById(R.id.textview_dub_2500)
        textview_dub_5000 = dialogDetails.findViewById(R.id.textview_dub_5000)
        textview_dub_10000 = dialogDetails.findViewById(R.id.textview_dub_10000)

        progressbar_dublicate= dialogDetails.findViewById(R.id.progressbar_dublicate)


        val yVals = ArrayList<PieEntry>()
        if (val1000 > 0.0) {
            yVals.add(PieEntry(val1000, ""))
        }

        if (val1500 > 0.0) {
            yVals.add(PieEntry(val1500, ""))
        }
        if (val2500 > 0.0) {
            yVals.add(PieEntry(val2500, ""))
        }
        if (val5000 > 0.0) {
            yVals.add(PieEntry(val5000, ""))
        }
        if (val10000 > 0.0) {
            yVals.add(PieEntry(val10000, ""))
        }


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


        //1000

        val yVals_1000 = ArrayList<PieEntry>()
        yVals_1000.add(PieEntry(val1000, ""))
        yVals_1000.add(PieEntry(4000f, ""))


        val dataSet_1000 = PieDataSet(yVals_1000, "")
        dataSet_1000.valueTextSize = 17f
        dataSet_1000.valueTextColor = Color.WHITE

        val colors_1000 = java.util.ArrayList<Int>()
        colors_1000.add(resources.getColor(R.color.colorTicket1000))
        colors_1000.add(resources.getColor(R.color.colorBlack))

        dataSet_1000.colors = colors_1000
        val data_1000 = PieData(dataSet_1000)
        pie1000.data = data_1000
        pie1000.isDrawHoleEnabled = false
        pie1000.legend.isEnabled = false
        pie1000.description.isEnabled = false


        //1500

        val yVals_1500 = ArrayList<PieEntry>()
        yVals_1500.add(PieEntry(val1500, ""))
        yVals_1500.add(PieEntry(8000f, ""))


        val dataSet_1500 = PieDataSet(yVals_1500, "")
        dataSet_1500.valueTextSize = 17f
        dataSet_1500.valueTextColor = Color.WHITE

        val colors_1500 = java.util.ArrayList<Int>()
        colors_1500.add(resources.getColor(R.color.colorTicket1500))
        colors_1500.add(resources.getColor(R.color.colorBlack))

        dataSet_1500.colors = colors_1500
        val data_1500 = PieData(dataSet_1500)
        pie1500.data = data_1500
        pie1500.isDrawHoleEnabled = false
        pie1500.legend.isEnabled = false
        pie1500.description.isEnabled = false


        //2500

        val yVals_2500 = ArrayList<PieEntry>()
        yVals_2500.add(PieEntry(val2500, ""))
        yVals_2500.add(PieEntry(3500f, ""))


        val dataSet_2500 = PieDataSet(yVals_2500, "")
        dataSet_2500.valueTextSize = 17f
        dataSet_2500.valueTextColor = Color.WHITE

        val colors_2500 = java.util.ArrayList<Int>()
        colors_2500.add(resources.getColor(R.color.colorTicket2500))
        colors_2500.add(resources.getColor(R.color.colorBlack))

        dataSet_2500.colors = colors_2500
        val data_2500 = PieData(dataSet_2500)
        pie2500.data = data_2500
        pie2500.isDrawHoleEnabled = false
        pie2500.legend.isEnabled = false
        pie2500.description.isEnabled = false


        //5000

        val yVals_5000 = ArrayList<PieEntry>()
        yVals_5000.add(PieEntry(val5000, ""))
        yVals_5000.add(PieEntry(1000f, ""))


        val dataSet_5000 = PieDataSet(yVals_5000, "")
        dataSet_5000.valueTextSize = 17f
        dataSet_5000.valueTextColor = Color.WHITE

        val colors_5000 = java.util.ArrayList<Int>()
        colors_5000.add(resources.getColor(R.color.colorTicket5000))
        colors_5000.add(resources.getColor(R.color.colorBlack))

        dataSet_5000.colors = colors_5000
        val data_5000 = PieData(dataSet_5000)
        pie5000.data = data_5000
        pie5000.isDrawHoleEnabled = false
        pie5000.legend.isEnabled = false
        pie5000.description.isEnabled = false


        //10000

        val yVals_10000 = ArrayList<PieEntry>()
        yVals_10000.add(PieEntry(val10000, ""))
        yVals_10000.add(PieEntry(500f, ""))


        val dataSet_10000 = PieDataSet(yVals_10000, "")
        dataSet_10000.valueTextSize = 17f
        dataSet_10000.valueTextColor = Color.WHITE

        val colors_10000 = java.util.ArrayList<Int>()
        colors_10000.add(resources.getColor(R.color.colorTicket10000))
        colors_10000.add(resources.getColor(R.color.colorBlack))

        dataSet_10000.colors = colors_10000
        val data_10000 = PieData(dataSet_10000)
        pie10000.data = data_10000
        pie10000.isDrawHoleEnabled = false
        pie10000.legend.isEnabled = false
        pie10000.description.isEnabled = false





        dialogDetails.show()

    }


    private fun pie1000() {

        var ref = database?.getReference("1000")
        ref?.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                viewmodel.value1000.value = dataSnapshot.children.count().toFloat()
                pie1500()

            }

            override fun onCancelled(error: DatabaseError) {

            }
        })


    }




    private fun pieDublicate() {


        var ref1 = database?.getReference("1000Duplicate")
        ref1?.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                viewmodel.value1000Dup.value = dataSnapshot.children.count().toFloat()

            }

            override fun onCancelled(error: DatabaseError) {

            }
        })



        var ref2 = database?.getReference("1500Duplicate")
        ref2?.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                viewmodel.value1500Dup.value = dataSnapshot.children.count().toFloat()

            }

            override fun onCancelled(error: DatabaseError) {

            }
        })


        var ref3 = database?.getReference("2500Duplicate")
        ref3?.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                viewmodel.value2500Dup.value = dataSnapshot.children.count().toFloat()

            }

            override fun onCancelled(error: DatabaseError) {

            }
        })


        var ref4 = database?.getReference("5000Duplicate")
        ref4?.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                viewmodel.value5000Dup.value = dataSnapshot.children.count().toFloat()

            }

            override fun onCancelled(error: DatabaseError) {

            }
        })



        var ref5 = database?.getReference("10000Duplicate")
        ref5?.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                viewmodel.value10000Dup.value = dataSnapshot.children.count().toFloat()

            }
            override fun onCancelled(error: DatabaseError) {

            }
        })

    }



    private fun pie1500() {
        var ref = database?.getReference("1500")
        ref?.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                viewmodel.value1500.value = dataSnapshot.children.count().toFloat()
                pie2500()
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })


    }


    private fun pie2500() {

        var ref = database?.getReference("2500")
        ref?.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                viewmodel.value2500.value = dataSnapshot.children.count().toFloat()
                pie5000()

            }

            override fun onCancelled(error: DatabaseError) {

            }
        })


    }


    private fun pie5000() {

        var ref = database?.getReference("5000")
        ref?.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                viewmodel.value5000.value = dataSnapshot.children.count().toFloat()
                pie10000()
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })


    }


    private fun pie10000() {

        var ref = database?.getReference("10000")
        ref?.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                viewmodel.value2500.value = dataSnapshot.children.count().toFloat()
                pieDublicate()
                showPieChartDialog()
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })


    }


}

