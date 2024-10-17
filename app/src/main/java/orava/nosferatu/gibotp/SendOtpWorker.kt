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
        const val SERVER_URL: String = "http://10.0.2.2:3000/receive_otp"
        val MEDIA_TYPE_JSON = "application/json; charset=utf-8".toMediaType()
    }

    override fun doWork(): Result {
        val otp = inputData.getString("otp") ?: return Result.failure()
        val json = JSONObject().apply {
            put("otp", otp)
        }

        val requestBody = json.toString().toRequestBody(MEDIA_TYPE_JSON)
        val request = Request.Builder()
            .url(SERVER_URL)
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
