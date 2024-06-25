package com.kipngetich.mooddetector.emotionDetection

import com.kipngetich.mooddetector.BuildConfig
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface EmotionDetectionService {
    @Headers(
        "Content-Type: application/json",
        "X-Rapidapi-Key: your api key",
        "X-Rapidapi-Host: your host"
    )
    @POST("/emotion-detection")
    fun detectEmotion(@Body request: EmotionDetectionRequest): Call<List<EmotionDetectionResponse>>
}
