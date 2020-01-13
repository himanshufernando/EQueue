package com.project.himanshu.equeue

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.common.api.CommonStatusCodes
import com.google.android.gms.vision.barcode.Barcode
import com.project.himanshu.equeue.utilities.barcode.BarcodeCaptureActivity


class MainActivity : AppCompatActivity() {



    companion object {
        private val LOG_TAG = MainActivity::class.java.simpleName
        private val BARCODE_READER_REQUEST_CODE = 1
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        //val intent = Intent(applicationContext, BarcodeCaptureActivity::class.java)
        //startActivityForResult(intent, BARCODE_READER_REQUEST_CODE)



    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == BARCODE_READER_REQUEST_CODE) {
            if (resultCode == CommonStatusCodes.SUCCESS) {
                if (data != null) {
                    val barcode = data.getParcelableExtra<Barcode>(BarcodeCaptureActivity.BarcodeObject)
                    val p = barcode.cornerPoints

                  /*  println("xxxxxxxxxxxxxxx displayValue : "+barcode.displayValue)
                    println("xxxxxxxxxxxxxxx contactInfo : "+barcode.contactInfo)
                    println("xxxxxxxxxxxxxxx driverLicense : "+barcode.driverLicense)
                    println("xxxxxxxxxxxxxxx format : "+barcode.format)
                    println("xxxxxxxxxxxxxxx geoPoint : "+barcode.geoPoint)
                    println("xxxxxxxxxxxxxxx rawBytes : "+barcode.rawBytes)
                    println("xxxxxxxxxxxxxxx sms : "+barcode.sms)
                    println("xxxxxxxxxxxxxxx url : "+barcode.url)
                    println("xxxxxxxxxxxxxxx rawValue : "+barcode.rawValue)

                    println("xxxxxxxxxxxxxxx valueFormat : "+barcode.valueFormat)
                    println("xxxxxxxxxxxxxxx wifi : "+barcode.wifi)
                    println("xxxxxxxxxxxxxxx phone : "+p.get(0).)*/







                    Toast.makeText(
                        this,
                        barcode.displayValue,
                        Toast.LENGTH_SHORT
                    ).show()

                } else
                    Toast.makeText(
                        this,
                        R.string.no_barcode_captured,
                        Toast.LENGTH_SHORT
                    ).show()

            } else
                Log.e(LOG_TAG, String.format(getString(R.string.barcode_error_format),
                    CommonStatusCodes.getStatusCodeString(resultCode)))
        } else
            super.onActivityResult(requestCode, resultCode, data)
    }
    override fun onResume() {
        super.onResume()




    }

    override fun onPause() {
        super.onPause()

    }


    private fun getQRReding() {
    }


}
