package com.kipngetich.mooddetector.affirmation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import com.kipngetich.mooddetector.affirmation.Affirm
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class AffirmationViewModel : ViewModel() {
    private val db = Firebase.firestore
    private val affirmationCollection = db.collection("affirmation")
    private val _affirmations = MutableStateFlow<List<Affirm>>(emptyList())

    val affirmations = _affirmations

    fun fetchAffirmations(emotion: String) {
        viewModelScope.launch {
            try {
                val querySnapshot = affirmationCollection.whereEqualTo("emotion", emotion).get().await()
                val affirmationsList = mutableListOf<Affirm>()

                for (document in querySnapshot.documents) {
                    val affirmationText = document.getString("affirmationText") ?: ""

                    affirmationsList.add(Affirm(affirmationText))
                }

                _affirmations.value = affirmationsList
            } catch (e: Exception) {
                affirmations.value = emptyList()
            }
        }
    }
}
