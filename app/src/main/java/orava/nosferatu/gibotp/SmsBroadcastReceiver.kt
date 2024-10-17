package orava.nosferatu.gibotp

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.provider.Telephony
import android.telephony.SmsMessage
import android.util.Log
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import java.util.regex.Pattern
import androidx.work.Data
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager

class SmsBroadcastReceiver: BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent?.action.equals(Telephony.Sms.Intents.SMS_RECEIVED_ACTION)) {
            val bundle = intent?.extras
            if (bundle != null) {
                val protocolDataUnits = bundle.get("pdus") as Array<*>
                for (protocolDataUnit in protocolDataUnits) {
                    val format = bundle.getString("format")
                    val smsMessage = SmsMessage.createFromPdu(protocolDataUnit as ByteArray, format)
                    val messageBody = smsMessage.messageBody
                    val otp = extractOtp(messageBody)
                    if (otp.isNotEmpty()) {
                        Log.d("SmsReceiver", "OTP extracted: $otp")

                        val data = Data.Builder()
                            .putString("otp", otp)
                            .build()

                        val sendOtpWork = OneTimeWorkRequestBuilder<SendOtpWorker>()
                            .setInputData(data)
                            .build()

                        context?.let {
                            WorkManager.getInstance(it).enqueue(sendOtpWork)
                        }

                        val localIntent = Intent("otp_received")
                        localIntent.putExtra("otp", otp)
                        context?.let {
                            LocalBroadcastManager.getInstance(it).sendBroadcast(localIntent)
                        }
                    }
                }
            }
        }
    }

    private fun extractOtp(message: String): String {
        val pattern = Pattern.compile("(\\d{4,6})")  // Assuming OTP is 4-6 digits
        val matcher = pattern.matcher(message)
        return if (matcher.find()) matcher.group(0) ?: "" else ""
    }
}