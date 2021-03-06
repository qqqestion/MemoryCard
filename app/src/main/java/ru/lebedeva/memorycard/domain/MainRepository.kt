package ru.lebedeva.memorycard.domain

import android.graphics.Bitmap
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import ru.lebedeva.memorycard.app.FirebaseApi
import ru.lebedeva.memorycard.app.NetworkHandler
import timber.log.Timber
import java.io.ByteArrayOutputStream

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

    suspend fun signOut(): Resource<Unit> =
        withContext(Dispatchers.IO) {
            return@withContext checkIfNetworkAvailable {
                firebase.signOut()
                Timber.d("Sign out success")
                Resource.Success(Unit)
            }
        }

    suspend fun createMemoryCard(card: MemoryCard, bitmap: Bitmap?): Resource<Unit> =
        withContext(Dispatchers.IO) {
            return@withContext checkIfNetworkAvailable {
                card.userId = firebase.currentUserId
                if (card.imageUri != null) {
                    card.imageUri = firebase.uploadImage(card.imageUri!!)
                } else if (bitmap != null) {
                    val stream = ByteArrayOutputStream()
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
                    val byteArray: ByteArray = stream.toByteArray()
                    bitmap.recycle()
                    card.imageUri = firebase.uploadImage(byteArray)
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

    suspend fun getMemoryCardById(cardId: String): Resource<MemoryCard> =
        withContext(Dispatchers.IO) {
            return@withContext checkIfNetworkAvailable {
                Resource.Success(firebase.getMemoryCardById(cardId))
            }
        }

    suspend fun deleteCardById(cardId: String): Resource<Unit> = withContext(Dispatchers.IO) {
        return@withContext checkIfNetworkAvailable {
            firebase.deleteMemoryCardById(cardId)
            Timber.d("Sign out success")
            Resource.Success(Unit)
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
                Resource.Error(msg = "???????????? ?????????????????????? ?? ??????????????????")
            }
        }
    }

    private suspend fun <T> request(action: suspend () -> Resource<T>): Resource<T> {
        return try {
            action()
        } catch (e: Throwable) {
            Timber.d(e, "Error during processing request")
            Resource.Error(msg = "???????????? ??????????????", error = e)
        }
    }
}