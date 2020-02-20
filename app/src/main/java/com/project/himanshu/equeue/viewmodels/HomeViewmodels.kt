package com.project.himanshu.equeue.viewmodels


import android.content.Context
import androidx.databinding.ObservableField
import com.project.himanshu.equeue.repo.HomeRepo
import kotlinx.coroutines.Dispatchers
import androidx.lifecycle.*
import com.google.firebase.database.FirebaseDatabase
import com.project.himanshu.equeue.Equeue
import com.project.himanshu.equeue.data.QrCodeReadRespons
import com.project.himanshu.equeue.R
import com.project.himanshu.equeue.data.db.AppDatabase
import com.project.himanshu.equeue.data.db.OriginalDao
import com.project.himanshu.equeue.data.db.OriginalTickets

class HomeViewmodels(contect: Context,val dataDao : OriginalDao): ViewModel() {

    var database: FirebaseDatabase = FirebaseDatabase.getInstance()

    var repo = HomeRepo(dataDao,database)

    private val _qrCode = MutableLiveData<String>()
    val qrCode: LiveData<String> = _qrCode


    var textTicketsPrice = ObservableField<String>()


    private val _originalQR = MutableLiveData<OriginalTickets>()
    val originalQR: LiveData<OriginalTickets> = _originalQR


    private val _originalQRUpdateToFirebase = MutableLiveData<OriginalTickets>()
    val originalQRUpdateToFirebase: LiveData<OriginalTickets> = _originalQRUpdateToFirebase


    private val _originalQRStatusUpdate = MutableLiveData<String>()
    val originalQRStatusUpdate: LiveData<String> = _originalQRStatusUpdate



    val newsList = originalQR.switchMap { id ->
        liveData(context = viewModelScope.coroutineContext + Dispatchers.IO) {
            try {
                emit(Result.success(repo.originalInsert(id)))
            } catch(ioException: Throwable) {
                emit(Result.failure(ioException))
            }
        }
    }


    val qrFirebase = originalQRUpdateToFirebase.switchMap { id ->
        liveData(context = viewModelScope.coroutineContext + Dispatchers.IO) {
            try {
                emit(Result.success(repo.originalInsertFirebase(id)))
            } catch(ioException: Throwable) {
                emit(Result.failure(ioException))
            }
        }
    }



    val updateQRstatus = originalQRStatusUpdate.switchMap { id ->
        liveData(context = viewModelScope.coroutineContext + Dispatchers.IO) {
            try {
                emit(Result.success(repo.qrUpdateStstus(id)))
            } catch(ioException: Throwable) {
                emit(Result.failure(ioException))
            }
        }
    }


    fun addQR(originaltickets : OriginalTickets) {
        _originalQR.value = originaltickets
        _originalQRUpdateToFirebase.value = originaltickets
    }


    fun update(code : String) {
        _originalQRStatusUpdate.value  = code
    }


    fun getAllToolsBorrowedList():  LiveData<List<OriginalTickets>> {
        return repo.getFriend()
    }






    val value1000: MutableLiveData<Float> by lazy {
        MutableLiveData<Float>()
    }



    val value1500: MutableLiveData<Float> by lazy {
        MutableLiveData<Float>()
    }


    val value2500: MutableLiveData<Float> by lazy {
        MutableLiveData<Float>()
    }


    val value5000: MutableLiveData<Float> by lazy {
        MutableLiveData<Float>()
    }


    val value10000: MutableLiveData<Float> by lazy {
        MutableLiveData<Float>()
    }


    val value1000Dup: MutableLiveData<Float> by lazy {
        MutableLiveData<Float>()
    }



    val value1500Dup: MutableLiveData<Float> by lazy {
        MutableLiveData<Float>()
    }


    val value2500Dup: MutableLiveData<Float> by lazy {
        MutableLiveData<Float>()
    }


    val value5000Dup: MutableLiveData<Float> by lazy {
        MutableLiveData<Float>()
    }


    val value10000Dup: MutableLiveData<Float> by lazy {
        MutableLiveData<Float>()
    }






    object LiveDataVMFactory : ViewModelProvider.Factory {
        var app: Context = Equeue.applicationContext()
        private val dataDAO = AppDatabase.getInstance(app).originalDao()
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            @Suppress("UNCHECKED_CAST")
            return HomeViewmodels(app,dataDAO) as T

        }
    }

}