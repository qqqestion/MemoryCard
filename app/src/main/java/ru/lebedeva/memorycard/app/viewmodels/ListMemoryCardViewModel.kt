package ru.lebedeva.memorycard.app.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import ru.lebedeva.memorycard.domain.MainRepository
import ru.lebedeva.memorycard.domain.MemoryCard
import ru.lebedeva.memorycard.domain.Resource
import timber.log.Timber

class ListMemoryCardViewModel(
    private val repository: MainRepository
) : ViewModel() {

    private val _cards = MutableLiveData<Resource<List<MemoryCard>>>()
    val cards: LiveData<Resource<List<MemoryCard>>> = _cards

    private val _signOutStatus = MutableLiveData<Resource<Unit>>()
    val signOutStatus: LiveData<Resource<Unit>> = _signOutStatus

    fun getUserCards() = viewModelScope.launch {
        _cards.postValue(Resource.Loading())
        val response = repository.getAllMemoryCardForCurrentUser()
        Timber.d("${response.data}")
        _cards.postValue(response)
    }

    fun signOut() = viewModelScope.launch {
        _signOutStatus.postValue(repository.signOut())
    }
}