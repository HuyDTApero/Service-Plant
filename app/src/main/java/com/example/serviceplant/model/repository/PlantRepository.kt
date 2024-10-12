package com.example.serviceplant.model.repository

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import com.example.serviceplant.R
import com.example.serviceplant.model.api.PlantIdentificationApi
import com.example.serviceplant.model.data.remote.PlantResponse
import okhttp3.MultipartBody
import retrofit2.Call
import java.security.KeyFactory
import java.security.spec.X509EncodedKeySpec
import java.util.Base64
import javax.crypto.Cipher
import javax.inject.Inject

class PlantRepository @Inject constructor(
    private val api: PlantIdentificationApi,
    private val context: Context
) {
    private fun createPayloadScheme(): String {
        val timestamp = System.currentTimeMillis().toString()
        val keyId = "123456789"
        val nonce = (0..1000000).random().toString()
        return "$timestamp@@@$keyId@@@$nonce"
    }

    // Function to encrypt the payload scheme
    @RequiresApi(Build.VERSION_CODES.O)
    private fun encryptPayload(payload: String, publicKeyStr: String): String {
        val publicKeyBytes = Base64.getDecoder().decode(publicKeyStr)
        val keySpec = X509EncodedKeySpec(publicKeyBytes)
        val keyFactory = KeyFactory.getInstance("RSA")
        val publicKey = keyFactory.generatePublic(keySpec)

        val cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding")
        cipher.init(Cipher.ENCRYPT_MODE, publicKey)

        val encryptedBytes = cipher.doFinal(payload.toByteArray())

        return Base64.getEncoder().encodeToString(encryptedBytes)
    }

    private fun loadPublicKey(context: Context): String {
        val inputStream = context.resources.openRawResource(R.raw.public_key)
        val publicKey = inputStream.bufferedReader().use { it.readText() }
        inputStream.close()
        return publicKey
    }

    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun identifyPlant2(
        file: MultipartBody.Part,
        bundleId: String,
        apiToken: String,
        appName: String
    ): Call<PlantResponse> {
        val payloadScheme = createPayloadScheme()
        val publicKey = loadPublicKey(context)
        val encryptedPayload = encryptPayload(payloadScheme, publicKey)

        val timestamp = payloadScheme.split("@@@")[0]

        return api.identifyPlant(
            file = file,
            apiSignature = encryptedPayload,
            bundleId = bundleId,
            timestamp = timestamp,
            apiToken = apiToken,
            appName = appName
        )
    }
}
