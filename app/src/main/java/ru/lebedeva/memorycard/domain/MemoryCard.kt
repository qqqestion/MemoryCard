package ru.lebedeva.memorycard.domain

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.GeoPoint

data class MemoryCard(
    @DocumentId
    val id: String? = null,
    var userId: String? = null,
    var location: GeoPoint? = null,
    var title: String? = null,
    var date: Timestamp? = null,
    var description: String? = "",
    var imageUri: String? = null
)