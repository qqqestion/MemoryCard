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

class CreateMemoryCardViewModel(
    private val repository: MainRepository
) : ViewModel() {
    private val _uploadCardStatus = MutableLiveData<Resource<Unit>>()
    val uploadCardStatus: LiveData<Resource<Unit>> = _uploadCardStatus


    fun createMemoryCard(card: MemoryCard) = viewModelScope.launch {
        _uploadCardStatus.postValue(Resource.Loading())
        val response = repository.createMemoryCard(card)
        Timber.d("${response.data}")
        _uploadCardStatus.postValue(response)
    }
}