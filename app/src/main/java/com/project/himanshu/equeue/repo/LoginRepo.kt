package com.project.himanshu.equeue.repo

import android.app.ActivityOptions
import android.content.Intent
import com.google.firebase.database.*
import com.project.himanshu.equeue.data.User

class LoginRepo {

    var myRef: DatabaseReference? = null
    var database: FirebaseDatabase? = null


    suspend fun loginValidation(userName: String, password: String): User {
        var resultUser: User? = null

        myRef = database?.getReference("User")
        val query: Query = myRef!!.orderByChild("username_password").equalTo(userName+password)
        query.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val snapshotIterator = dataSnapshot.children
            }
            override fun onCancelled(databaseError: DatabaseError) {

            }
        })


        return resultUser!!

    }

}