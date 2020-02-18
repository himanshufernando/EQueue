package com.project.himanshu.equeue.repo

import android.R.attr
import android.content.Context
import androidx.lifecycle.MutableLiveData
import com.google.firebase.database.*
import com.project.himanshu.equeue.data.Error
import com.project.himanshu.equeue.data.User
import com.project.himanshu.equeue.data.UserRespons
import android.provider.Settings
import himanshu.project.mydoc.services.network.InternetConnection


class LoginRepo(con: Context) {

    var context = con


    var myRef: DatabaseReference? = null
    var database: FirebaseDatabase? = null
    var android_id = ""

    suspend fun loginValidation(
        userName: MutableLiveData<String>,
        password: MutableLiveData<String>
    ): User {

        database = FirebaseDatabase.getInstance()
        lateinit var resultUser: User

        when {
            userName.value.isNullOrEmpty() -> {
                resultUser = User("", "", "", "", false, Error("", "Please enter your User Name"))
            }
            password.value.isNullOrEmpty() -> {
                resultUser = User("", "", "", "", false, Error("", "Please enter your Password."))
            }
            !InternetConnection.checkInternetConnection() -> {
                resultUser = User(
                    "",
                    "",
                    "",
                    "",
                    false,
                    Error("", "Please check your internet connection\".")
                )
            }
            else -> {

                android_id =
                    Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID)

                myRef = database?.getReference("User")
                val query: Query = myRef!!.orderByChild("username_password")
                    .equalTo(userName.value.toString() + password.value.toString())


                query.addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {

                        if (dataSnapshot.children.count() == 0) {
                            resultUser = User(
                                "",
                                "",
                                "",
                                "",
                                false,
                                Error("", "Invalid username or password")
                            )
                        } else {
                            for (postSnapshot in dataSnapshot.children) {
                                if (postSnapshot.child("device_id").value.toString().isBlank()) {
                                    println("aaaaaaaaaaaaaaaaaaaaaaaaaaaaa   : "+postSnapshot.child("device_id").value.toString())


                                   // updateDeviceId(android_id, postSnapshot.key.toString())
                                    resultUser = User("", "", "", "", true, Error("", ""))
                                } else {
                                    resultUser = if (postSnapshot.child("device_id").value.toString() == android_id
                                    ) {
                                        println("aaaaaaaaaaaaaaaaaaaaaaaaaaaaa   equl: "+postSnapshot.child("device_id").value.toString())


                                        User("", "", "", "", true, Error("", ""))

                                    } else {
                                        println("aaaaaaaaaaaaaaaaaaaaaaaaaaaaa   logged: "+postSnapshot.child("device_id").value.toString())


                                        User(
                                            "",
                                            "",
                                            "",
                                            "",
                                            false,
                                            Error("", "You are logged in another devices")
                                        )
                                    }

                                }

                            }
                        }

                    }

                    override fun onCancelled(error: DatabaseError) {
                        resultUser = User("", "", "", "", false, Error("", error.message))
                    }
                })
            }
        }
        return resultUser

    }


    private fun updateDeviceId(id :String,key : String){

        myRef?.child("device_id")?.child(key)?.setValue(id)
        println("ddddddddddddddd ; "+myRef)

    }


}