package com.example.travelingproject.Activity

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.travelingproject.Fragment.HomeFragment
import com.example.travelingproject.MainActivity
import com.example.travelingproject.R
import com.example.travelingproject.databinding.ActivityDashboardBinding
import com.google.firebase.auth.FirebaseAuth


class DashboardActivity : AppCompatActivity() {

    lateinit var okay_text: TextView
    lateinit var cancel_text: TextView
    lateinit var auth: FirebaseAuth

    lateinit var binding: ActivityDashboardBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()

        var sharedPreferences = getSharedPreferences("MySharedPreferences", MODE_PRIVATE)

        initview()

    }

    private fun initview() {

//        var i=Intent(this@DashboardActivity,HomeFragment::class.java)
//        startActivity(i)

        val sharedPreferences = getSharedPreferences("my_preferences", Context.MODE_PRIVATE)

        val username = sharedPreferences.getString("username", "")
        val mail = sharedPreferences.getString("email", "")
        val password = sharedPreferences.getString("password", "")
        val address = sharedPreferences.getString("address", "")
        val mobile = sharedPreferences.getString("mobile", "")
        val type = sharedPreferences.getString("type", "")
        val image = sharedPreferences.getString("Image", "")

        //  sharedPreferences store date
        binding.txtemail.setText(mail)
        binding.txtUsername.setText(username)
        binding.txtusertype.setText(type)

        Glide.with(this@DashboardActivity)
            .load(image)
            .error(R.drawable.logo2) // Error image if loading fails
            .into(binding.civProfileImages)

        binding.imgmenu.setOnClickListener {
            binding.drawer.openDrawer(binding.navigationview)
        }

//        binding.txtHome.setOnClickListener {
//            var i = Intent(this@DashboardActivity, HomeFragment2::class.java)
//            startActivity(i)
//        }
        binding.lnrhome.setOnClickListener {

//            var i =Intent(this@DashboardActivity,HomeFragment::class.java)
//            startActivity(i)
                val fragment = HomeFragment()
                val bundle = Bundle()
                bundle.putString("userType", type)
                fragment.arguments = bundle
                supportFragmentManager.beginTransaction()
                    .replace(R.id.LoutFrame, fragment)
                    .addToBackStack(null)
                    .commit()

        }
        binding.lnrprivacypolicy.setOnClickListener {
            var i = Intent(this@DashboardActivity, PrivacyPolicyActivity::class.java)
            startActivity(i)
        }

        binding.imgmenu.setOnClickListener {
            binding.drawer.openDrawer(binding.navigationview)
            Log.e("TAG", "nav: " + binding.navigationview)
        }

//        binding.lnrProfile.setOnClickListener {
//            binding.drawer.closeDrawer(binding.navigationview)
//        }




//        val fragmentTransaction = supportFragmentManager.beginTransaction()
//        fragmentTransaction.addToBackStack(null)
//        fragmentTransaction.commit()

        binding.txtHome.setOnClickListener {
            val fragmentTransaction = supportFragmentManager.beginTransaction()
            fragmentTransaction.addToBackStack(null)
            fragmentTransaction.commit()
        }

        binding.linrateapp.setOnClickListener {
            var ratingusdialog = RatingDialog(this@DashboardActivity)
            ratingusdialog.window?.setBackgroundDrawable(ColorDrawable(resources.getColor(android.R.color.transparent)))
            ratingusdialog.setCancelable(false)
            ratingusdialog.show()
        }
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.LoutFrame, HomeFragment())
        fragmentTransaction.addToBackStack(null)
        fragmentTransaction.commit()

        binding.txtHome.setOnClickListener {
            val fragmentTransaction = supportFragmentManager.beginTransaction()
            fragmentTransaction.replace(R.id.LoutFrame, HomeFragment())
            fragmentTransaction.addToBackStack(null)
            fragmentTransaction.commit()
        }

        binding.lnrLogout.setOnClickListener {
            var sharedPreferences: SharedPreferences.Editor = sharedPreferences.edit()
            sharedPreferences.remove("isLogin")
            sharedPreferences.commit()
            logout()
       }
    }

    private fun logout() {



        val dialog = Dialog(this@DashboardActivity)
        dialog.setContentView(R.layout.dialog_box)
        dialog.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        dialog.setCancelable(false)

        // Find the TextViews from the dialog's view hierarchy
        okay_text = dialog.findViewById(R.id.okay_text)
        cancel_text = dialog.findViewById(R.id.cancel_text)

        okay_text.setOnClickListener {
            auth.signOut()
            val intent = Intent(this@DashboardActivity, MainActivity::class.java)
            Toast.makeText(this@DashboardActivity, "Successfully Logged Out", Toast.LENGTH_SHORT)
                .show()
            startActivity(intent)
            finish()
            dialog.dismiss()
        }

        cancel_text.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }


    }