package ru.lebedeva.memorycard.app.viewmodels

import android.util.Patterns
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import kotlinx.coroutines.launch
import ru.lebedeva.memorycard.domain.MainRepository
import ru.lebedeva.memorycard.domain.Resource

class RegisterViewModel(
    private val repository: MainRepository
) : ViewModel() {

    private val _signUpStatus = MutableLiveData<Resource<Unit>>()
    val signUpStatus: LiveData<Resource<Unit>> = _signUpStatus

    fun signUp(email: String, password: String, passwordConfirm: String) = viewModelScope.launch {
        if (_signUpStatus.value is Resource.Loading) {
            return@launch
        }
        _signUpStatus.postValue(Resource.Loading())
        if (email.isEmpty()) {
            _signUpStatus.postValue(Resource.Error(msg = "Введите электронную почту"))
            return@launch
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            _signUpStatus.postValue(Resource.Error(msg = "Введите корректную электронную почту"))
            return@launch
        }
        if (password.isEmpty()) {
            _signUpStatus.postValue(Resource.Error(msg = "Введите пароль"))
            return@launch
        }
        if (password != passwordConfirm) {
            _signUpStatus.postValue(Resource.Error(msg = "Пароли не совпадают"))
            return@launch
        }
        val response = repository.signUp(email, password)
        if (response is Resource.Error) {
            if (response.error is FirebaseAuthWeakPasswordException) {
                response.msg = "Введеный пароль слишком простой"
            }
        }
        _signUpStatus.postValue(response)
    }
}