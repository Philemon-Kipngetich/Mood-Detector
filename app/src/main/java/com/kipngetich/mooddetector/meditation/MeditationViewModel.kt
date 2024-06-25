package com.kipngetich.mooddetector.meditation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class MeditationViewModel : ViewModel(){
    private val db = Firebase.firestore
    private val musicCollection = db.collection("meditations")

    private val _videoList = MutableStateFlow<List<VideoItem>>(emptyList())
    val videoList = _videoList

    fun fetchMeditationRecommendations(emotion: String) {
        viewModelScope.launch {
            try {
                val querySnapshot = musicCollection.whereEqualTo("emotion", emotion).get().await()
                val videoList = mutableListOf<VideoItem>()

                for (document in querySnapshot.documents) {
                    val title = document.getString("title") ?: ""
                    val videoURL = document.getString("videoURL") ?: ""
                    val imageURL = document.getString("imageURL") ?: ""

                    videoList.add(VideoItem(title, videoURL, imageURL))
                }

                _videoList.value = videoList
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}
