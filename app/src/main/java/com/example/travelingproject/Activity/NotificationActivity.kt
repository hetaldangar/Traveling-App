package com.example.travelingproject.Activity

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.travelingproject.databinding.ActivityNotificationBinding

class NotificationActivity : AppCompatActivity() {

    lateinit var binding : ActivityNotificationBinding

    private lateinit var phoneStateReceiver: MyFirebaseMessagingService

    companion object {
        private const val PERMISSION_SEND_SMS = 123
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNotificationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initView()    }

    private fun initView() {

        binding.sendSMSButton.setOnClickListener {
            val phoneNumber = binding.edtphoneNumber.text.toString().trim()
            val message = binding.edtmessage.text.toString().trim()

            if (phoneNumber.isNotEmpty())
            {
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED)
                {
                    ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.SEND_SMS), PERMISSION_SEND_SMS)
                }
                else
                {
                    sendSMS(phoneNumber, message)
                }
            }
            else
            {
                binding.edtphoneNumber.error = "Please enter a phone number"
            }

            if(message.isEmpty())
            {
                binding.edtmessage.error = "Please enter a phone number"
            }
        }
        phoneStateReceiver = MyFirebaseMessagingService(this, "", "")
    }
    private fun sendSMS(phoneNumber: String, message: String) {
        // Your SMS sending logic here
        // Example: Send an SMS using Intent
        val smsIntent = Intent(Intent.ACTION_VIEW, Uri.parse("sms:$phoneNumber"))
        smsIntent.putExtra("sms_body", message)
        Log.e("TAG", "sendSMS: "+message)
        startActivity(smsIntent)
    }
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray)
    {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_SEND_SMS) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)
            {
                val phoneNumber = binding.edtphoneNumber.text.toString().trim()
                val message = binding.edtmessage.text.toString().trim()

                sendSMS(phoneNumber,message)
                Log.e("TAG", "onRequestPermissionsResult: "+phoneNumber)
            }
            else
            {
                Toast.makeText(this, "SMS permission denied", Toast.LENGTH_SHORT).show()
            }
        }
    }

}