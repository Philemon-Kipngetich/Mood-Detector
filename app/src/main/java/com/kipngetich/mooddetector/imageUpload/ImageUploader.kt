package com.kipngetich.mooddetector.imageUpload

import android.content.Context
import android.graphics.Bitmap
import android.widget.Toast
import com.kipngetich.mooddetector.BuildConfig
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.ByteArrayOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

fun uploadImageToServer(
    imageBitmap: Bitmap,
    context: Context,
    onProcessChanged: (Boolean) -> Unit,
    onImageUrlReceived: (String) -> Unit
) {
    onProcessChanged(true)
    val retrofit = Retrofit.Builder()
        .baseUrl("your base url")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val apiService = retrofit.create(ApiService::class.java)

    val stream = ByteArrayOutputStream()
    imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)
    val byteArray = stream.toByteArray()

    val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
    val randomValue = (0..1000).random()
    val imageName = "image_${timeStamp}_$randomValue.jpg"

    val requestFile = byteArray.toRequestBody("image/*".toMediaTypeOrNull())
    val imagePart = MultipartBody.Part.createFormData("image", imageName, requestFile)
    val descriptionRequestBody = "json".toRequestBody("text/plain".toMediaTypeOrNull())

    val call: Call<ImageUploadResponse> = apiService.uploadImage(descriptionRequestBody, imagePart)

    call.enqueue(object : Callback<ImageUploadResponse> {
        override fun onResponse(
            call: Call<ImageUploadResponse>,
            response: Response<ImageUploadResponse>
        ) {
            onProcessChanged(false)
            if (response.isSuccessful) {
                val uploadResponse: ImageUploadResponse? = response.body()
                if (uploadResponse != null && !uploadResponse.error) {
                    val imageUrl = uploadResponse.image
                    onImageUrlReceived(imageUrl)
                    Toast.makeText(context, "Image is being processed. Hang tight.", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(context, "Failed to process the image.", Toast.LENGTH_SHORT).show()
            }
        }

        override fun onFailure(call: Call<ImageUploadResponse>, t: Throwable) {
            onProcessChanged(false)
            Toast.makeText(context, "Network error. Please try again.", Toast.LENGTH_SHORT).show()
        }
    })
}