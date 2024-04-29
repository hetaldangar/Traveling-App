package com.example.travelingproject.Activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.webkit.WebView
import com.example.travelingproject.R
import com.example.travelingproject.databinding.ActivityPrivacyPolicyBinding

class PrivacyPolicyActivity : AppCompatActivity() {

    lateinit var web: WebView

    lateinit var binding: ActivityPrivacyPolicyBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPrivacyPolicyBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initView()
    }

    private fun initView() {

        binding.webView.loadUrl("https://www.termsfeed.com/live/27c7c9b3-d1cf-4e6b-8a04-7b4c76ece66a")
    }
}