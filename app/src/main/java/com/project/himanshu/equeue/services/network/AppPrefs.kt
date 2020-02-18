package com.project.himanshu.equeue.services.network

import android.content.Context


object AppPrefs {

    const val PREFS_NAME = "rooozdriver"

    const val KEY_USERID = "id"


   public fun saveUserID(context: Context, id: String) {
        var prefs = context.getSharedPreferences(AppPrefs.PREFS_NAME, Context.MODE_PRIVATE)
        var editor = prefs.edit()
        editor.putString(KEY_USERID, id)
        editor.apply()
    }


    public fun getUserID(context: Context): String {
        var prefs = context.getSharedPreferences(AppPrefs.PREFS_NAME, Context.MODE_PRIVATE)
        return prefs.getString(KEY_USERID, "").toString()
    }

}