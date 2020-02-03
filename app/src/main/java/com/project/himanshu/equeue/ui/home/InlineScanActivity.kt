package com.project.himanshu.equeue.ui.home

import android.content.Context
import android.graphics.Color
import android.os.Build

import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.view.View
import android.view.WindowManager
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.viewModels
import androidx.lifecycle.observe
import com.google.zxing.ResultPoint
import com.journeyapps.barcodescanner.BarcodeCallback
import com.journeyapps.barcodescanner.BarcodeResult
import com.journeyapps.barcodescanner.CaptureManager
import com.project.himanshu.equeue.R
import com.project.himanshu.equeue.viewmodels.HomeViewmodels
import kotlinx.android.synthetic.main.activity_home.*


class InlineScanActivity : AppCompatActivity() {
    lateinit var captureManager: CaptureManager
    var torchState: Boolean = false
    var readedCode = ""

    private val viewmodel: HomeViewmodels by viewModels { HomeViewmodels.LiveDataVMFactory }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        captureManager = CaptureManager(this, barcodeView)
        captureManager.initializeFromIntent(intent, savedInstanceState)




        barcodeView.decodeContinuous(object: BarcodeCallback{
            override fun barcodeResult(result: BarcodeResult?) {
                result?.let {
                   /* txtResult.text = it.text*/
                    if(readedCode != it.text){
                        viewmodel.validateQR(it.text)
                        val vib: Vibrator = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator

                        if(vib.hasVibrator()){
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
                                // void vibrate (VibrationEffect vibe)
                                vib.vibrate(VibrationEffect.createOneShot(1, VibrationEffect.DEFAULT_AMPLITUDE))
                            }else{
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


        viewmodel.qrReadRespons.observe(this){news ->
            news.onSuccess {it

            }
            news.onFailure {it

            }
        }


        btnTorch.setOnClickListener {
            if(torchState){
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


    fun callQRReaderCallBack(){


    }


}
