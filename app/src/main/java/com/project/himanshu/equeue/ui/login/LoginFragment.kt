package com.project.himanshu.equeue.ui.login


import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.lifecycle.observe

import com.project.himanshu.equeue.R
import com.project.himanshu.equeue.databinding.FragmentLoginBinding
import com.project.himanshu.equeue.ui.home.InlineScanActivity
import com.project.himanshu.equeue.viewmodels.LoginViewmodels
import kotlinx.android.synthetic.main.fragment_login.view.*

/**
 * A simple [Fragment] subclass.
 */
class LoginFragment : Fragment() {


    private val viewmodelLoging: LoginViewmodels by viewModels { LoginViewmodels.LiveDataVMFactory }
    lateinit var binding:FragmentLoginBinding


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_login, container,false)
        binding.loginviewmodel = viewmodelLoging



        viewmodelLoging.newsList.observe(viewLifecycleOwner){news ->
            news.onSuccess {it

            }
            news.onFailure {it

            }
        }

       /* var root = inflater.inflate(R.layout.fragment_login, container, false)

        root.button.setOnClickListener {

            val i = Intent(activity, InlineScanActivity::class.java)
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(i)
            activity?.finish()
        }*/
        return binding.root
    }


}
