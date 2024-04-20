package com.ifs21020.lf.presentation.lf

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.ifs21020.lf.presentation.ViewModelFactory
import com.ifs21020.lf.data.remote.MyResult
import com.ifs21020.lf.data.remote.response.DataAddObjectResponse
import com.ifs21020.lf.data.remote.response.DelcomObjectResponse
import com.ifs21020.lf.data.remote.response.DelcomResponse
import com.ifs21020.lf.data.repository.ObjectRepository

class ObjectViewModel(
    private val objectRepository: ObjectRepository
) : ViewModel() {
    fun getObject(lostfoundId: Int): LiveData<MyResult<DelcomObjectResponse>>{
        return objectRepository.getObject(lostfoundId).asLiveData()
    }
    fun postObject(
        title: String,
        description: String,
        status: String
    ): LiveData<MyResult<DataAddObjectResponse>>{
        return objectRepository.postObject(
            title,
            description,
            status
        ).asLiveData()
    }
    fun putObject(
        lostfoundId: Int,
        title: String,
        description: String,
        isCompleted: Boolean,
    ): LiveData<MyResult<DelcomResponse>> {
        return objectRepository.putObject(
            lostfoundId,
            title,
            description,
            isCompleted,
        ).asLiveData()
    }
    fun deleteObject(lostfoundId: Int): LiveData<MyResult<DelcomResponse>> {
        return objectRepository.deleteObject(lostfoundId).asLiveData()
    }
    companion object {
        @Volatile
        private var INSTANCE: ObjectViewModel? = null
        fun getInstance(
            objectRepository: ObjectRepository
        ): ObjectViewModel {
            synchronized(ViewModelFactory::class.java) {
                INSTANCE = ObjectViewModel(
                    objectRepository
                )
            }
            return INSTANCE as ObjectViewModel
        }
    }
}