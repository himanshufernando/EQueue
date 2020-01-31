package com.project.himanshu.equeue.viewmodels


import android.content.Context
import com.project.himanshu.equeue.repo.HomeRepo
import kotlinx.coroutines.Dispatchers
import androidx.lifecycle.*
import com.project.himanshu.equeue.Equeue

class HomeViewmodels(contect : Context) : ViewModel(){

    var repo = HomeRepo()

    private val _qrCode = MutableLiveData<String>()
    val qrCode: LiveData<String> = _qrCode


    init {
        _qrCode.value = ""
    }


    val qrReadRespons = qrCode.switchMap { code ->
        liveData(context = viewModelScope.coroutineContext + Dispatchers.IO) {
            try {
                emit(Result.success(repo.validateQR(code,contect)))
            } catch(ioException: Throwable) {
                emit(Result.failure(ioException))
            }

        }
    }

    fun validateQR(code : String){
        _qrCode.value = code
    }



    object LiveDataVMFactory : ViewModelProvider.Factory {
        var app : Context = Equeue.applicationContext()
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            @Suppress("UNCHECKED_CAST")
            return HomeViewmodels(app) as T

        }
    }

}