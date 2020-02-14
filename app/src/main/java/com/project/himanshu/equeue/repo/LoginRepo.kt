package com.project.himanshu.equeue.repo

import android.R.attr
import com.google.firebase.database.*
import com.project.himanshu.equeue.data.Error
import com.project.himanshu.equeue.data.User
import himanshu.project.mydoc.services.network.InternetConnection


class LoginRepo {

    var myRef: DatabaseReference? = null
    var database: FirebaseDatabase? = null


    suspend fun loginValidation(userName: String, password: String): User {
        println("aaaaaaaaaaaaaa 11 :")
        lateinit var resultUser: User

        if(userName==null){
            println("aaaaaaaaaaaaaa 22 :")
        }else if(password == null){
            println("aaaaaaaaaaaaaa 23 :")
        }else if(!InternetConnection.checkInternetConnection()){
            println("aaaaaaaaaaaaaa 24 :")
        }else{
            println("aaaaaaaaaaaaaa 25 :")

        }





/*
        when {
            userName==null -> {
                println("aaaaaaaaaaaaaa 22 :")
                resultUser.ststus = false
                resultUser.error = Error("","Please enter your User Name.")
            }
            password == null -> {

                println("aaaaaaaaaaaaaa 23 :")
                resultUser.ststus = false
                resultUser.error = Error("","Please enter your Password.")
            }!InternetConnection.checkInternetConnection() -> {
                println("aaaaaaaaaaaaaa 44 :")
                resultUser.ststus = false
                resultUser.error = Error("","Please check your internet connection")
            }
            else -> {
                println("aaaaaaaaaaaaaa faruebase start :")
                myRef = database?.getReference("User")
                val query: Query = myRef!!.orderByChild("username_password").equalTo(userName+password)
                query.addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {

                        val iterator: Iterator<DataSnapshot> = dataSnapshot.children.iterator()
                        while (iterator.hasNext()) {
                            val next = iterator.next() as DataSnapshot
                            println("aaaaaaaaaaaaaa Iterator :"+next.child("password").value as String)
                        }

                    }
                    override fun onCancelled(databaseError: DatabaseError) {

                    }
                })


            }
        }
*/

        return resultUser!!

    }

}