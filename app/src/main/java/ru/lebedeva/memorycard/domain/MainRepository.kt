package ru.lebedeva.memorycard.domain

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import ru.lebedeva.memorycard.app.FirebaseApi
import ru.lebedeva.memorycard.app.NetworkHandler
import timber.log.Timber

class MainRepository(
    private val firebase: FirebaseApi,
    private val networkHandler: NetworkHandler
) {

    suspend fun signUp(email: String, password: String): Resource<Unit> =
        withContext(Dispatchers.IO) {
            return@withContext checkIfNetworkAvailable {
                firebase.signUp(email, password)
                Timber.d("Sign in success")
                Resource.Success(Unit)
            }
        }

    suspend fun signIn(email: String, password: String): Resource<Unit> =
        withContext(Dispatchers.IO) {
            return@withContext checkIfNetworkAvailable {
                firebase.signIn(email, password)
                Timber.d("Sign in success")
                Resource.Success(Unit)
            }
        }

    suspend fun createMemoryCard(card: MemoryCard): Resource<Unit> =
        withContext(Dispatchers.IO) {
            return@withContext checkIfNetworkAvailable {
                card.userId = firebase.currentUserId
                if (card.imageUri != null) {
                    card.imageUri = firebase.uploadImage(card.imageUri!!)
                }
                firebase.createMemoryCard(card)
                Resource.Success(Unit)
            }
        }

    suspend fun getAllMemoryCardForCurrentUser(): Resource<List<MemoryCard>> =
        withContext(Dispatchers.IO) {
            return@withContext checkIfNetworkAvailable {
                Resource.Success(firebase.getAllMemoryCardsForUser(firebase.currentUserId))
            }
        }

    suspend fun isLoggedIn(): Resource<Boolean> = withContext(Dispatchers.IO) {
        return@withContext checkIfNetworkAvailable {
            Resource.Success(firebase.isLoggedIn)
        }
    }

    private suspend fun <T> checkIfNetworkAvailable(action: suspend () -> Resource<T>): Resource<T> {
        return when (networkHandler.isNetworkAvailable()) {
            true -> {
                request(action)
            }
            false -> {
                Resource.Error(msg = "Ошибка подключения к интернету")
            }
        }
    }

    private suspend fun <T> request(action: suspend () -> Resource<T>): Resource<T> {
        return try {
            action()
        } catch (e: Throwable) {
            Timber.d(e, "Error during processing request")
            Resource.Error(msg = "Ошибка сервера. Попробуйте еще раз")
        }
    }
}