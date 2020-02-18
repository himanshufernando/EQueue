package com.project.himanshu.equeue.ui.login


import android.app.AlertDialog
import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.observe
import com.google.firebase.database.*

import com.project.himanshu.equeue.R
import com.project.himanshu.equeue.data.Error
import com.project.himanshu.equeue.data.User
import com.project.himanshu.equeue.data.UserRespons
import com.project.himanshu.equeue.databinding.FragmentLoginBinding
import com.project.himanshu.equeue.services.network.AppPrefs
import com.project.himanshu.equeue.ui.home.InlineScanActivity
import com.project.himanshu.equeue.viewmodels.LoginViewmodels
import himanshu.project.mydoc.services.network.InternetConnection
import kotlinx.android.synthetic.main.fragment_login.view.*

/**
 * A simple [Fragment] subclass.
 */
class LoginFragment : Fragment() {


    private val viewmodelLoging: LoginViewmodels by viewModels { LoginViewmodels.LiveDataVMFactory }
    lateinit var binding: FragmentLoginBinding


    var myRef: DatabaseReference? = null
    var database: FirebaseDatabase? = null
    var android_id = ""


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_login, container, false)
        binding.loginviewmodel = viewmodelLoging

        binding.progressbar.visibility = View.INVISIBLE




       if(AppPrefs.getUserID(this.requireContext()) != ""){
            val i = Intent(activity, InlineScanActivity::class.java)
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(i)
            activity?.finish()

        }

        binding.button.setOnClickListener {
            loginValidation(
                binding.editTextUsername.text.toString(),
                binding.editTextPassword.text.toString()
            )
        }


        return binding.root
    }


    private fun message(tital: String, message: String) {

        val builder = AlertDialog.Builder(activity)
        builder.setTitle(tital)
        builder.setMessage(message)
        builder.setCancelable(false)
        builder.setPositiveButton("OK") { _, _ ->
            return@setPositiveButton
        }
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }

    fun loginValidation(userName: String, password: String) {
        database = FirebaseDatabase.getInstance()
        when {
            userName.isNullOrEmpty() -> {
                message("User Name", "Please enter your User Name")
            }
            password.isNullOrEmpty() -> {
                message("Password", "Please enter your Password")
            }
            !InternetConnection.checkInternetConnection() -> {
                message("Internet", "Please check your internet connection")
            }
            else -> {

                binding.progressbar.visibility = View.VISIBLE



                android_id =
                    Settings.Secure.getString(activity?.contentResolver, Settings.Secure.ANDROID_ID)

                myRef = database?.getReference("User")
                val query: Query = myRef!!.orderByChild("username_password")
                    .equalTo(userName + password)



                query.addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        if (dataSnapshot.children.count() == 0) {
                            binding.progressbar.visibility = View.INVISIBLE
                            message("Invalid", "Invalid username or password")
                        } else {
                            for (postSnapshot in dataSnapshot.children) {
                                if (postSnapshot.child("device_id").value.toString().isBlank()) {


                                    AppPrefs.saveUserID(requireContext(),postSnapshot.child("id").value.toString())

                                    Toast.makeText(activity, "You are logged!", Toast.LENGTH_LONG)
                                        .show()
                                    update(android_id,postSnapshot.child("name").value.toString(),
                                        postSnapshot.child("password").value.toString(),
                                        postSnapshot.child("userName").value.toString(),
                                        postSnapshot.child("username_password").value.toString(),
                                        postSnapshot.key.toString())

                                } else {
                                    if (postSnapshot.child("device_id").value.toString() == android_id) {
                                        Toast.makeText(
                                            activity,
                                            "You are logged!",
                                            Toast.LENGTH_LONG
                                        ).show()

                                        AppPrefs.saveUserID(requireContext(),postSnapshot.child("id").value.toString())

                                        val i = Intent(activity, InlineScanActivity::class.java)
                                        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                                            .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                                            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                        startActivity(i)
                                        activity?.finish()

                                    } else {
                                        binding.progressbar.visibility = View.INVISIBLE
                                        message("Devices", "You are logged in another devices")
                                    }

                                }

                            }
                        }

                    }

                    override fun onCancelled(error: DatabaseError) {
                        binding.progressbar.visibility = View.INVISIBLE
                        message("Firebase Error", error.toString())
                    }
                })
            }
        }

    }

    private fun update(device_id : String,name : String,password : String,userName : String,
                       username_password : String,id : String){

        var updateRef: DatabaseReference = database?.getReference("User")!!.child(id)

        var user = UserRespons(device_id,id,name,password,userName,username_password)

        updateRef.setValue(user)


        val i = Intent(activity, InlineScanActivity::class.java)
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(i)
        activity?.finish()

    }

}
