package ru.lebedeva.memorycard.app.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.Timestamp
import com.google.firebase.firestore.GeoPoint
import kotlinx.coroutines.launch
import ru.lebedeva.memorycard.domain.MainRepository
import ru.lebedeva.memorycard.domain.MemoryCard
import ru.lebedeva.memorycard.domain.Resource
import timber.log.Timber
import java.util.*

class LoginViewModel(
    private val repository: MainRepository
) : ViewModel() {

    private val _signInStatus = MutableLiveData<Resource<Unit>>()
    val signInStatus: LiveData<Resource<Unit>> = _signInStatus

    fun signIn(email: String, password: String) = viewModelScope.launch {
        Timber.d("Sign in")
        if (_signInStatus.value is Resource.Loading) {
            Timber.d("Value is loading")
            return@launch
        }
        _signInStatus.postValue(Resource.Loading())
        val response = repository.signIn(email, password)
        _signInStatus.postValue(response)
    }
}