package com.project.himanshu.equeue

import android.content.Context
import androidx.multidex.MultiDexApplication
import com.google.firebase.FirebaseApp

class Equeue : MultiDexApplication(){

    companion object {
        private var instance: Equeue? = null
        fun applicationContext(): Context {
            return instance!!.applicationContext
        }
    }

    init {
        instance = this
    }

    override fun onCreate() {
        super.onCreate()
        val context: Context = Equeue.applicationContext()

    }


}