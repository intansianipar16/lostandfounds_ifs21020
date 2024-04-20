package com.ifs21020.lf.presentation

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.ifs21020.lf.data.repository.AuthRepository
import com.ifs21020.lf.data.repository.ObjectRepository
import com.ifs21020.lf.data.repository.UserRepository
import com.ifs21020.lf.di.Injection
import com.ifs21020.lf.presentation.login.LoginViewModel
import com.ifs21020.lf.presentation.lf.ObjectViewModel
import com.ifs21020.lf.presentation.main.MainViewModel
import com.ifs21020.lf.presentation.profile.ProfileViewModel
import com.ifs21020.lf.presentation.register.RegisterViewModel

class ViewModelFactory(
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository,
    private val objectRepository: ObjectRepository
) : ViewModelProvider.NewInstanceFactory() {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(RegisterViewModel::class.java) -> {
                RegisterViewModel
                    .getInstance(authRepository) as T
            }
            modelClass.isAssignableFrom(LoginViewModel::class.java) -> {
                LoginViewModel
                    .getInstance(authRepository) as T
            }
            modelClass.isAssignableFrom(MainViewModel::class.java) -> {
                MainViewModel
                    .getInstance(authRepository, objectRepository) as T
            }
            modelClass.isAssignableFrom(ProfileViewModel::class.java) -> {
                ProfileViewModel
                    .getInstance(authRepository, userRepository) as T
            }
            modelClass.isAssignableFrom(ObjectViewModel::class.java) -> {
                ObjectViewModel
                    .getInstance(objectRepository) as T
            }
            else -> throw IllegalArgumentException(
                "Unknown ViewModel class: " + modelClass.name
            )
        }
    }
    companion object {
        @Volatile
        private var INSTANCE: ViewModelFactory? = null
        @JvmStatic
        fun getInstance(context: Context): ViewModelFactory {
            synchronized(ViewModelFactory::class.java) {
                INSTANCE = ViewModelFactory(
                    Injection.provideAuthRepository(context),
                    Injection.provideUserRepository(context),
                    Injection.provideObjectRepository(context)
                )
            }
            return INSTANCE as ViewModelFactory
        }
    }
}