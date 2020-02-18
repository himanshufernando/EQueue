package com.project.himanshu.equeue.viewmodels

import android.content.Context
import androidx.databinding.ObservableField
import androidx.lifecycle.*
import com.project.himanshu.equeue.Equeue
import com.project.himanshu.equeue.data.User
import com.project.himanshu.equeue.repo.LoginRepo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import androidx.lifecycle.MutableLiveData
import android.icu.lang.UCharacter.GraphemeClusterBreak.T


class LoginViewmodels(context: Context) : ViewModel() {

    var con = context

    var repo = LoginRepo(con)

    var username = MutableLiveData<String>()
    var password = MutableLiveData<String>()


    private val _userLoginAction = MutableLiveData<Boolean>()
    val userLoginAction: LiveData<Boolean> = _userLoginAction


    val newsList = userLoginAction.switchMap { id ->
        liveData(context = viewModelScope.coroutineContext + Dispatchers.IO) {
            try {
                if (id) {
                    emit(Result.success(repo.loginValidation(username, password)))
                }
            } catch (ioException: Throwable) {
                emit(Result.failure(ioException))
            }
        }
    }


    fun loginValidation() {
        _userLoginAction.value = true
    }

    object LiveDataVMFactory : ViewModelProvider.Factory {
        var app: Context = Equeue.applicationContext()
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            @Suppress("UNCHECKED_CAST")
            return LoginViewmodels(app) as T

        }
    }


}