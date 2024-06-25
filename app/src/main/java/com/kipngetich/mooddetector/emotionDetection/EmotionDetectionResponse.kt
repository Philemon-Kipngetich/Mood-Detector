package com.kipngetich.mooddetector.emotionDetection

data class EmotionDetectionResponse(
    val probability: Double,
    val rectangle: Rectangle,
    val emotion: Emotion
)

data class Rectangle(
    val left: Double,
    val top: Double,
    val right: Double,
    val bottom: Double
)

data class Emotion(
    val value: String,
    val probability: Double,
    val sentiments: Sentiments
)

data class Sentiments(
    val angry: Double,
    val disgust: Double,
    val fear: Double,
    val happy: Double,
    val sad: Double,
    val surprise: Double,
    val neutral: Double
)
