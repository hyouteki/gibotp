package orava.nosferatu.gibotp

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.io.IOException

class SendOtpWorker(context: Context, params: WorkerParameters) : Worker(context, params) {

    companion object {
        val MEDIA_TYPE_JSON = "application/json; charset=utf-8".toMediaType()
    }

    override fun doWork(): Result {
        val sharedPreferences = applicationContext.getSharedPreferences("Settings@gibotp", Context.MODE_PRIVATE)

        val otp = inputData.getString("otp") ?: return Result.failure()
        val json = JSONObject().apply {
            put("otp", otp)
        }

        val ip = sharedPreferences.getString("remote_ip", "10.0.2.2")
        val port = sharedPreferences.getString("remote_port", "3000")
        val endpoint = sharedPreferences.getString("remote_endpoint", "receive_otp")
        val serverUrl = "http://$ip:$port/$endpoint"

        val requestBody = json.toString().toRequestBody(MEDIA_TYPE_JSON)
        val request = Request.Builder()
            .url(serverUrl)
            .post(requestBody)
            .build()

        val client = OkHttpClient()
        return try {
            val response = client.newCall(request).execute()
            if (response.isSuccessful) {
                Result.success()
            } else {
                Result.retry()
            }
        } catch (e: IOException) {
            Result.retry()
        }
    }
}
