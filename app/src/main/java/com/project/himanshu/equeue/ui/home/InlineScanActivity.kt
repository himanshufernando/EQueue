package com.project.himanshu.equeue.ui.home

import android.content.Context

import android.os.Build

import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.observe
import com.google.zxing.ResultPoint
import com.journeyapps.barcodescanner.BarcodeCallback
import com.journeyapps.barcodescanner.BarcodeResult
import com.journeyapps.barcodescanner.CaptureManager
import com.project.himanshu.equeue.R
import com.project.himanshu.equeue.data.QrCodeReadRespons
import com.project.himanshu.equeue.databinding.ActivityHomeBinding
import com.project.himanshu.equeue.viewmodels.HomeViewmodels
import kotlinx.android.synthetic.main.activity_home.*


class InlineScanActivity : AppCompatActivity() {
    lateinit var captureManager: CaptureManager
    var torchState: Boolean = false
    var readedCode = ""

    val sdk = Build.VERSION.SDK_INT

    lateinit var binding: ActivityHomeBinding

    private val viewmodel: HomeViewmodels by viewModels { HomeViewmodels.LiveDataVMFactory }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_home)
        binding.home = viewmodel
        captureManager = CaptureManager(this, barcodeView)
        captureManager.initializeFromIntent(intent, savedInstanceState)




        barcodeView.decodeContinuous(object : BarcodeCallback {
            override fun barcodeResult(result: BarcodeResult?) {
                result?.let {
                    /* txtResult.text = it.text*/
                    if (readedCode != it.text) {
                        viewmodel.validateQR(it.text)
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


        viewmodel.qrReadRespons.observe(this) { news ->
            news.onSuccess {it
                if(it.code_reading_status!!){
                    setBackgroundToLayout(it)
                }else{
                    if(!it.ticket_price.equals("")){ setErrorMessage() }
                }
            }
            news.onFailure { it
                setErrorMessage()
            }
        }


        btnTorch.setOnClickListener {
            if (torchState) {
                torchState = false
                barcodeView.setTorchOff()
            } else {
                torchState = true
                barcodeView.setTorchOn()
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


    private fun setBackgroundToLayout(respond: QrCodeReadRespons) {
        var ticDrawable = R.drawable.bg_round
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
    fun setErrorMessage(){
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Error")
        builder.setMessage("Not valid ticket ,Please recheck")
        builder.setCancelable(false)
        builder.setPositiveButton(android.R.string.yes) { dialog, which ->
            readedCode = ""
        }
        builder.show()

    }


}
