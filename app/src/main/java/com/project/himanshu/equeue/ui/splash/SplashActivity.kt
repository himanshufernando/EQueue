package com.project.himanshu.equeue.ui.splash

import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import androidx.fragment.app.FragmentActivity
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.project.himanshu.equeue.R

class SplashActivity : FragmentActivity() {


    var myRef: DatabaseReference? = null
    var database : FirebaseDatabase ? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        trasperat()
        setContentView(R.layout.activity_splash)


        database = FirebaseDatabase.getInstance()

      //  addTCCat()

       // addUsers()



    }

    private fun addCode(){



    }


    private fun addTCCat(){

        myRef = database?.getReference("Ticket Category")

        val cat1 = myRef!!.push()

        cat1.child("price").setValue(10000)
        cat1.child("category_code").setValue("A")
        cat1.child("number_of_tickets").setValue(500)
        cat1.child("category_name").setValue("10000 VVIP")

        val cat2 = myRef!!.push()

        cat2.child("price").setValue(5000)
        cat2.child("category_code").setValue("B")
        cat2.child("number_of_tickets").setValue(1000)
        cat2.child("category_name").setValue("5000 VIP")



        val cat3 = myRef!!.push()

        cat3.child("price").setValue(2500)
        cat3.child("category_code").setValue("C")
        cat3.child("number_of_tickets").setValue(2000)
        cat3.child("category_name").setValue("2500")


        val cat4 = myRef!!.push()

        cat4.child("price").setValue(1500)
        cat4.child("category_code").setValue("D")
        cat4.child("number_of_tickets").setValue(4000)
        cat4.child("category_name").setValue("1500")


        val cat5 = myRef!!.push()

        cat5.child("price").setValue(1000)
        cat5.child("category_code").setValue("E")
        cat5.child("number_of_tickets").setValue(3000)
        cat5.child("category_name").setValue("Student")


    }



    private fun addUsers(){

        myRef = database?.getReference("User")


        val newUser = myRef!!.push()

        newUser.child("name").setValue("Nayomi")
        newUser.child("userName").setValue("Nayomi@OBA321")
        newUser.child("password").setValue("oba@nayomi321")
        newUser.child("username_password").setValue("Nayomi@OBA321oba@nayomi321")
        newUser.child("type").setValue("O")


        val newUser1 = myRef!!.push()

        newUser1.child("name").setValue("Grand Negombo")
        newUser1.child("userName").setValue("GrandN@OBA123")
        newUser1.child("password").setValue("oba@grandn123")
        newUser.child("username_password").setValue("GrandN@OBA123oba@grandn123")
        newUser1.child("type").setValue("O")


        val newUser2 = myRef!!.push()

        newUser2.child("name").setValue("Grandeeza")
        newUser2.child("userName").setValue("Grandeeza@OBA789")
        newUser2.child("password").setValue("oba@grandeeza789")
        newUser.child("username_password").setValue("Grandeeza@OBA789oba@grandeeza789")
        newUser2.child("type").setValue("O")



        val newUser3 = myRef!!.push()

        newUser3.child("name").setValue("Grand Wenappuwa")
        newUser3.child("userName").setValue("GrandW@OBA321")
        newUser3.child("password").setValue("oba@grandw321")
        newUser.child("username_password").setValue("GrandW@OBA321oba@grandw321")
        newUser3.child("type").setValue("O")



        val newUser4 = myRef!!.push()

        newUser4.child("name").setValue("Minal Studio")
        newUser4.child("userName").setValue("Minal@OBA456")
        newUser4.child("password").setValue("oba@minal456")
        newUser.child("username_password").setValue("Minal@OBA456oba@minal456")
        newUser4.child("type").setValue("O")


        val newUser5 = myRef!!.push()

        newUser5.child("name").setValue("Molly")
        newUser5.child("userName").setValue("Molly@OBA456")
        newUser5.child("password").setValue("oba@molly456")
        newUser.child("username_password").setValue("Molly@OBA456oba@molly456")
        newUser5.child("type").setValue("O")


        val newUser6 = myRef!!.push()

        newUser6.child("name").setValue("Dilan Fernando")
        newUser6.child("userName").setValue("DilanF@OBA456")
        newUser6.child("password").setValue("oba@dilan456")
        newUser.child("username_password").setValue("DilanF@OBA456oba@dilan456")
        newUser6.child("type").setValue("M")


        val newUser7 = myRef!!.push()

        newUser7.child("name").setValue("Dinnuke Jayawardene")
        newUser7.child("userName").setValue("DinnukeJ@OBA456")
        newUser7.child("password").setValue("oba@dinnuke456")
        newUser.child("username_password").setValue("DinnukeJ@OBA456oba@dinnuke456")
        newUser7.child("type").setValue("M")


        val newUser8 = myRef!!.push()

        newUser8.child("name").setValue("Himanshu Fernando")
        newUser8.child("userName").setValue("HimanshuF@OBA456")
        newUser8.child("password").setValue("oba@himanshu456")
        newUser.child("username_password").setValue("HimanshuF@OBA456oba@himanshu456")
        newUser8.child("type").setValue("M")


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
