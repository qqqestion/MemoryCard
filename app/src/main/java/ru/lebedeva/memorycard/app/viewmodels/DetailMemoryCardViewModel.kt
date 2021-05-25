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

class DetailMemoryCardViewModel(
    private val repository: MainRepository
) : ViewModel() {
    private val _card = MutableLiveData<Resource<MemoryCard>>()
    val card: LiveData<Resource<MemoryCard>> = _card


    fun getMemoryCardById(cardId: String) = viewModelScope.launch {
        _card.postValue(Resource.Loading())
        val response = repository.getMemoryCardById(cardId)
        Timber.d("${response.data}")
        _card.postValue(response)
    }
}