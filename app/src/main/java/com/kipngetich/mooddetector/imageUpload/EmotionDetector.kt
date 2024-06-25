package com.kipngetich.mooddetector.imageUpload

import android.content.Context
import android.widget.Toast
import com.kipngetich.mooddetector.BuildConfig
import com.kipngetich.mooddetector.emotionDetection.EmotionDetectionRequest
import com.kipngetich.mooddetector.emotionDetection.EmotionDetectionResponse
import com.kipngetich.mooddetector.emotionDetection.EmotionDetectionService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

fun sendEmotionDetectionRequest(
    imageUrl: String,
    context: Context,
    onProcessStateChanged: (Boolean) -> Unit,
    onEmotionDetected: (String) -> Unit
) {
    onProcessStateChanged(true)
    val retrofit = Retrofit.Builder()
        .baseUrl("your detection url")
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(EmotionDetectionService::class.java)
    val emotionDetectionRequest = EmotionDetectionRequest(imageUrl)

    val call = retrofit.detectEmotion(emotionDetectionRequest)
    call.enqueue(object : Callback<List<EmotionDetectionResponse>> {
        override fun onResponse(
            call: Call<List<EmotionDetectionResponse>>,
            response: Response<List<EmotionDetectionResponse>>
        ) {
            onProcessStateChanged(false)
            if (response.isSuccessful) {
                val responseList = response.body()
                if (!responseList.isNullOrEmpty()) {
                    val detectedEmotion = responseList[0].emotion.value
                    Toast.makeText(context, "Image Processing Complete", Toast.LENGTH_SHORT).show()
                    onEmotionDetected(detectedEmotion)
                } else {
                    Toast.makeText(context, "No emotions detected", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(context, "Emotion detection failed", Toast.LENGTH_SHORT).show()
            }
        }

        override fun onFailure(call: Call<List<EmotionDetectionResponse>>, t: Throwable) {
            onProcessStateChanged(false)
            Toast.makeText(context, "Emotion detection failed", Toast.LENGTH_SHORT).show()
        }

    })
}