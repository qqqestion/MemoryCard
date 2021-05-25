package ru.lebedeva.memorycard.app.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import ru.lebedeva.memorycard.app.NetworkHandler
import ru.lebedeva.memorycard.domain.MainRepository

class ViewModelProviderFactory(
    private val repository: MainRepository
) : ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return modelClass.getConstructor(MainRepository::class.java).newInstance(repository)
    }
}