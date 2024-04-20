package com.ifs21020.lf.presentation.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.ifs21020.lf.presentation.ViewModelFactory
import com.ifs21020.lf.data.pref.UserModel
import com.ifs21020.lf.data.remote.MyResult
import com.ifs21020.lf.data.remote.response.DelcomObjectsResponse
import com.ifs21020.lf.data.remote.response.DelcomResponse
import com.ifs21020.lf.data.repository.AuthRepository
import com.ifs21020.lf.data.repository.ObjectRepository
import kotlinx.coroutines.launch

class MainViewModel(
    private val authRepository: AuthRepository,
    private val objectRepository: ObjectRepository
) : ViewModel() {
    fun getSession(): LiveData<UserModel> {
        return authRepository.getSession().asLiveData()
    }
    fun logout() {
        viewModelScope.launch {
            authRepository.logout()
        }
    }
    fun getObjects(): LiveData<MyResult<DelcomObjectsResponse>> {
        return objectRepository.getObjects(null).asLiveData()
    }
    fun putObject(
        objectId: Int,
        title: String,
        description: String,
        isFinished: Boolean,
    ): LiveData<MyResult<DelcomResponse>> {
        return objectRepository.putObject(
            objectId,
            title,
            description,
            isFinished,
        ).asLiveData()
    }
    companion object {
        @Volatile
        private var INSTANCE: MainViewModel? = null
        fun getInstance(
            authRepository: AuthRepository,
            objectRepository: ObjectRepository
        ): MainViewModel {
            synchronized(ViewModelFactory::class.java) {
                INSTANCE = MainViewModel(
                    authRepository,
                    objectRepository
                )
            }
            return INSTANCE as MainViewModel
        }
    }
}