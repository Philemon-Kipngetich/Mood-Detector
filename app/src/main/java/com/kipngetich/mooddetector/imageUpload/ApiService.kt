package com.kipngetich.mooddetector.imageUpload

import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface ApiService {
    @Multipart
    @POST("your api end point")
    fun uploadImage(
        @Part("desc") description: RequestBody,
        @Part image: MultipartBody.Part
    ): Call<ImageUploadResponse>
}
