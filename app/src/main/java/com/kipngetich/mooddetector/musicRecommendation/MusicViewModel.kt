package com.kipngetich.mooddetector.musicRecommendation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import com.kipngetich.mooddetector.musicRecommendation.Music
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class MusicViewModel : ViewModel(){
    private val db = Firebase.firestore
    private val musicCollection = db.collection("music")

    private val _musicList = MutableStateFlow<List<Music>>(emptyList())
    val musicList = _musicList

    fun fetchMusicRecommendations(emotion: String) {
        viewModelScope.launch {
            try {
                val querySnapshot = musicCollection.whereEqualTo("emotion", emotion).get().await()
                val musicList = mutableListOf<Music>()

                for (document in querySnapshot.documents) {
                    val title = document.getString("title") ?: ""
                    val audioURL = document.getString("audioURL") ?: ""
                    val imageURL = document.getString("imageURL") ?: ""

                    musicList.add(Music(title, audioURL, imageURL))
                }

                _musicList.value = musicList
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}
