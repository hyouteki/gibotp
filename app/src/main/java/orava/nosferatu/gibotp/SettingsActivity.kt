package orava.nosferatu.gibotp

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Button
import android.widget.Switch
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.textfield.TextInputEditText

class SettingsActivity : AppCompatActivity() {

    private lateinit var saveButton: Button
    private lateinit var ipEditText: TextInputEditText
    private lateinit var portEditText: TextInputEditText
    private lateinit var endpointEditText: TextInputEditText
    private lateinit var backgroundSwitch: Switch

    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var editor: SharedPreferences.Editor

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_settings)

        saveButton = findViewById(R.id.save_button)
        ipEditText = findViewById(R.id.ip_edit_text)
        portEditText = findViewById(R.id.port_edit_text)
        endpointEditText = findViewById(R.id.endpoint_edit_text)
        backgroundSwitch = findViewById(R.id.background_switch)

        sharedPreferences = this.getSharedPreferences("Settings@gibotp", Context.MODE_PRIVATE)
        editor = sharedPreferences.edit()

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        ipEditText.setText(sharedPreferences.getString("remote_ip", "10.0.2.2"))
        portEditText.setText(sharedPreferences.getString("remote_port", "3000"))
        endpointEditText.setText(sharedPreferences.getString("remote_endpoint", "receive_otp"))
        backgroundSwitch.isChecked = sharedPreferences.getBoolean("background_service", false)

        saveButton.setOnClickListener {
            if (ipEditText.text.toString().isEmpty()) {
                Toast.makeText(this, "Remote Server IP cannot be empty", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (portEditText.text.toString().isEmpty()) {
                Toast.makeText(this, "Remote Server Port cannot be empty", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (endpointEditText.text.toString().isEmpty()) {
                Toast.makeText(this, "Remote Server Endpoint cannot be empty", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            editor.apply {
                putString("remote_ip", ipEditText.text.toString())
                putString("remote_port", portEditText.text.toString())
                putString("remote_endpoint", endpointEditText.text.toString())
                putBoolean("background_service", backgroundSwitch.isChecked)
                apply()
            }
            Toast.makeText(this, "Saved successfully", Toast.LENGTH_SHORT).show()
            finish()
        }
    }
}