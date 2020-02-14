package com.project.himanshu.equeue.ui.splash

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.view.WindowManager
import androidx.fragment.app.FragmentActivity
import com.google.firebase.FirebaseApp
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.project.himanshu.equeue.R
import java.security.AccessController.getContext

class SplashActivity : FragmentActivity() {


    var myRef: DatabaseReference? = null
    var database : FirebaseDatabase ? = null


    @SuppressLint("HardwareIds")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        trasperat()
        setContentView(R.layout.activity_splash)
        FirebaseApp.initializeApp(this)

        database = FirebaseDatabase.getInstance()

      //  addTCCat()

      // addUsers()

      // var android_id = Settings.Secure.getString(this.contentResolver, Settings.Secure.ANDROID_ID)

       // println("qqqqqqqqqqqqqqq : $android_id")

    }

    private fun addUsers(){

        myRef = database?.getReference("User")


        val newUser = myRef!!.push()

        newUser.child("name").setValue("Himanshu")
        newUser.child("userName").setValue("himanshu")
        newUser.child("password").setValue("oba@hima321")
        newUser.child("username_password").setValue("himanshuoba@hima321")
        newUser.child("device_id").setValue("")


        val newUser1 = myRef!!.push()

        newUser1.child("name").setValue("Shimantha")
        newUser1.child("userName").setValue("shimantha")
        newUser1.child("password").setValue("oba@simbad123")
        newUser1.child("username_password").setValue("shimanthaoba@simbad123")
        newUser.child("device_id").setValue("")


        val newUser2 = myRef!!.push()

        newUser2.child("name").setValue("Dilran")
        newUser2.child("userName").setValue("dilran")
        newUser2.child("password").setValue("oba@bada789")
        newUser2.child("username_password").setValue("dilranoba@bada789")
        newUser.child("device_id").setValue("")



        val newUser3 = myRef!!.push()

        newUser3.child("name").setValue("Dinnuke")
        newUser3.child("userName").setValue("dinnuke")
        newUser3.child("password").setValue("oba@dinnuke321")
        newUser3.child("username_password").setValue("dinnukeoba@dinnuke321")
        newUser.child("device_id").setValue("")






    }

    private fun trasperat() {
        if (Build.VERSION.SDK_INT in 19..20) {
            setWindowFlag(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, true)
        }
        if (Build.VERSION.SDK_INT >= 19) {
            window.decorView.systemUiVisibility =
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        }
        if (Build.VERSION.SDK_INT >= 21) {
            setWindowFlag(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, false)
            window.statusBarColor = Color.TRANSPARENT
        }
    }

    private fun setWindowFlag(bits: Int, on: Boolean) {
        val win = window
        val winParams = win.attributes
        if (on) {
            winParams.flags = winParams.flags or bits
        } else {
            winParams.flags = winParams.flags and bits.inv()
        }
        win.attributes = winParams
    }
}
