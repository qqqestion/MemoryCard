package ru.lebedeva.memorycard.app

import android.net.Uri
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.tasks.await
import ru.lebedeva.memorycard.domain.MemoryCard
import java.util.*


const val CARDS_COLLECTION = "cards"

class FirebaseApi {

    private val auth = FirebaseAuth.getInstance()

    val currentUserId: String
        get() = auth.uid!!

    private val cardsCollection = Firebase.firestore.collection(CARDS_COLLECTION)

    suspend fun signUp(email: String, password: String) {
        auth.createUserWithEmailAndPassword(email, password).await()
    }

    suspend fun signIn(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password).await()
    }

    suspend fun createMemoryCard(card: MemoryCard) {
        cardsCollection.add(card).await()
    }

    suspend fun uploadImage(imageUri: String): String {
        val res = FirebaseStorage.getInstance()
            .reference
            .child(UUID.randomUUID().toString())
            .putFile(Uri.parse(imageUri))
            .await()
        return res.metadata?.reference?.downloadUrl?.await().toString()
    }

    suspend fun getAllMemoryCardsForUser(userId: String): List<MemoryCard> {
        return cardsCollection
            .whereEqualTo("userId", userId)
            .get()
            .await()
            .toObjects(MemoryCard::class.java)
    }
}