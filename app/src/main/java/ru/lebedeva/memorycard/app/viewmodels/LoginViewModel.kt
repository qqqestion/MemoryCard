package ru.lebedeva.memorycard.app.viewmodels

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

    val signUpStatus = MutableLiveData<Resource<Unit>>()

    fun signUp() = viewModelScope.launch {
        val memoryCard = MemoryCard(
            location = GeoPoint(1.1313, 1.1313),
            title = "Воспоминание о",
            date = Timestamp(Date()),
            description = "Empty description"
        )
        Timber.d(if (repository.createMemoryCard(memoryCard) is Resource.Success) "Success" else "Failed")
//        val response = repository.createMemoryCard(memoryCard)
        val response = repository.getAllMemoryCardForCurrentUser()
        Timber.d("${response.data}")
//        signUpStatus.postValue(response)
    }
}