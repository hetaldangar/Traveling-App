package com.example.travelingproject.Activity

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager.widget.ViewPager
import com.example.travelingproject.Adapter.ViewPagerAdapter
import com.example.travelingproject.R
import com.example.travelingproject.databinding.ActivityPackageBinding

class PackageActivity : AppCompatActivity() {
    private lateinit var viewPager: ViewPager
    private lateinit var binding: ActivityPackageBinding
    private lateinit var imageList: MutableList<String>
    private lateinit var adapter: ViewPagerAdapter
    private var currentPage = 0
    private val handler = Handler(Looper.getMainLooper())

    private lateinit var txtDialogButton: TextView
    private lateinit var mobiles: String
    private lateinit var txtYes: TextView
    private lateinit var txtMobiles: TextView
    private lateinit var txtNo: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPackageBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initView()
    }

    @SuppressLint("SetTextI18n")
    private fun initView() {
        viewPager = findViewById(R.id.vipImageSlider)
        binding.imgMap.setOnClickListener {
            startActivity(Intent(this, MapsActivity::class.java))
        }

        imageList = mutableListOf()
        val name = intent.extras?.getString("name")
        mobiles = intent.extras?.getString("mobile").toString()
        val allImageLists = intent.extras?.getStringArrayList("allImageList")
        val day = intent.extras?.getInt("day")
        val price = intent.extras?.getString("Price")
        val notes = intent.extras?.getString("Notes")

        binding.txtName.text = name
        binding.txtNotes.text = notes
        binding.txtMobile.text = mobiles
        binding.txtPrices.text = price
        binding.txtDays.text = "Day :- $day"

        binding.txtMobile.setOnClickListener {
            phoneCall()
        }

        binding.btnbooknow.setOnClickListener {
            val i = Intent(this, BookingActivity::class.java)
            i.putExtra("name", name)
            startActivity(i)
        }

        adapter = ViewPagerAdapter(this, allImageLists)
        viewPager.adapter = adapter

        // Initialize the image count when the activity is created
        updateImageCount(viewPager.currentItem)

        viewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {}

            override fun onPageSelected(position: Int) {
                // Update the image count when the page changes
                updateImageCount(position)
            }

            override fun onPageScrollStateChanged(state: Int) {}
        })

        // Start auto image sliding
        startAutoSlide()
    }

    private fun updateImageCount(position: Int) {
        // Update the text view with the current image count
        val countText = getString(R.string.image_count, position + 1, adapter.count)
        binding.imageCountTextView.text = countText
    }

    private fun startAutoSlide() {
        val updateImageRunnable = object : Runnable {
            override fun run() {
                if (currentPage == adapter.count - 1) {
                    currentPage = 0
                } else {
                    currentPage++
                }
                viewPager.setCurrentItem(currentPage, true)
                handler.postDelayed(this, 3000) // Change image every 3 seconds
            }
        }
        handler.postDelayed(updateImageRunnable, 2000)
    }

    override fun onDestroy() {
        // Remove the callback to prevent memory leaks
        handler.removeCallbacksAndMessages(null)
        super.onDestroy()
    }

    private fun phoneCall() {
        txtDialogButton = findViewById(R.id.txtMobile)
        val dialog = Dialog(this@PackageActivity)

        txtDialogButton.setOnClickListener {
            dialog.setContentView(R.layout.call)
            dialog.window?.setLayout(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            dialog.setCancelable(false)

            txtYes = dialog.findViewById(R.id.txtbtnYes)
            txtNo = dialog.findViewById(R.id.txtbtnNo)
            txtMobiles = dialog.findViewById(R.id.txtMobiles)

            txtMobiles.text = mobiles
            txtYes.setOnClickListener {
                val number = mobiles
                val callIntent = Intent(Intent.ACTION_CALL)
                callIntent.data = Uri.parse("tel:$number")
                startActivity(callIntent)
            }

            txtNo.setOnClickListener {
                dialog.dismiss()
            }
            dialog.show()
        }
    }
}
