package com.project.himanshu.equeue.viewmodels


import android.content.Context
import androidx.databinding.ObservableField
import com.project.himanshu.equeue.repo.HomeRepo
import kotlinx.coroutines.Dispatchers
import androidx.lifecycle.*
import com.project.himanshu.equeue.Equeue
import com.project.himanshu.equeue.data.QrCodeReadRespons
import com.project.himanshu.equeue.R

class HomeViewmodels(contect: Context) : ViewModel() {

    var repo = HomeRepo()

    private val _qrCode = MutableLiveData<String>()
    val qrCode: LiveData<String> = _qrCode


    var textTicketsPrice = ObservableField<String>()



    object LiveDataVMFactory : ViewModelProvider.Factory {
        var app: Context = Equeue.applicationContext()
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            @Suppress("UNCHECKED_CAST")
            return HomeViewmodels(app) as T

        }
    }

}