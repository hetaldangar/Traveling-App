package com.example.travelingproject

import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.widget.Toast
import com.example.travelingproject.Activity.DashboardActivity
import com.example.travelingproject.Activity.SignUpActivity
import com.example.travelingproject.ModelClass.ModelClass
import com.example.travelingproject.databinding.ActivityMainBinding
import com.facebook.AccessToken
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginResult
import com.google.firebase.Firebase
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class MainActivity : AppCompatActivity() {

    lateinit var callbackManager: CallbackManager
    lateinit var binding: ActivityMainBinding

    private lateinit var auth: FirebaseAuth

    lateinit var reference: DatabaseReference
    lateinit var sharedPreferences: SharedPreferences
    lateinit var firebaseDatabase: FirebaseDatabase

    val firebaseAuth = FirebaseAuth.getInstance()

    companion object {
        lateinit var user: ModelClass
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initView()
    }

    private fun initView() {
        auth = Firebase.auth
        firebaseDatabase = FirebaseDatabase.getInstance()
        sharedPreferences = getSharedPreferences("MySharedPreferences", MODE_PRIVATE)
        callbackManager = CallbackManager.Factory.create()

        binding.txtSignup.setOnClickListener {

            val i = Intent(this@MainActivity, SignUpActivity::class.java)
            startActivity(i)
        }

    //    binding.btnFacebook.setReadPermissions("email", "public_profile")
        binding.btnFacebook.registerCallback(callbackManager, object :
            FacebookCallback<LoginResult> {
            override fun onSuccess(loginResult: LoginResult) {
                Log.d("TAG", "facebook:onSuccess:" + loginResult)
                handleFacebookAccessToken(loginResult.accessToken)
            }

            override fun onCancel() {
                Log.d("TAG", "facebook:onCancel")
            }

            override fun onError(error: FacebookException) {
                // Handle error if needed
            }
        })

        binding.btnsign.setOnClickListener {
            //  val dialog = createProgressDialog()

            val username = binding.edtUsername.text.toString()
            val email = binding.edtEmail.text.toString()
            val password = binding.edtPassword.text.toString()

            if (TextUtils.isEmpty(username)) {
                binding.edtUsername.setError("Enter Username")
                binding.edtUsername.requestFocus()
                return@setOnClickListener
            } else if (TextUtils.isEmpty(email)) {
                binding.edtEmail.setError("Enter Your E-Mail")
                binding.edtEmail.requestFocus()
                return@setOnClickListener
            } else if (!email.endsWith("@gmail.com")) {
                binding.edtEmail.setError("Enter a valid Gmail address (e.g., example@gmail.com)")
                binding.edtEmail.requestFocus()
                return@setOnClickListener
            } else if (password.length < 8) {
                binding.edtPassword.setError("Password must be at least 8 characters long")
                binding.edtPassword.requestFocus()
                return@setOnClickListener
            }
            else
            {
                auth = FirebaseAuth.getInstance()

                auth.signInWithEmailAndPassword(email, password).addOnCompleteListener {
                    if (it.isSuccessful)
                    {
                        Toast.makeText(this, "Login Successfully", Toast.LENGTH_SHORT).show()

                        firebaseDatabase.reference.root.child("UsertypeTb").child(username).addValueEventListener(object :
                            ValueEventListener {

                            override fun onDataChange(snapshot: DataSnapshot) {
                                val userModel = snapshot.getValue(ModelClass::class.java)
                                if (userModel != null) {
                                    user = userModel

                                    // Save user data to SharedPreferences
                                    val sharedPreferencesEditor: SharedPreferences.Editor = sharedPreferences.edit()
                                    sharedPreferencesEditor.putBoolean("isLogin", true)
                                    sharedPreferencesEditor.putString("email", user.email)
                                    sharedPreferencesEditor.putString("username", user.username)
                                    sharedPreferencesEditor.putString("address", user.address)
                                    // Add other user data if needed
                                    sharedPreferencesEditor.apply()

                                    // Start DashboardActivity
                                    val intent = Intent(this@MainActivity, DashboardActivity::class.java)
                                    startActivity(intent)
                                    finish() // Finish current activity to prevent going back to it
                                } else {
                                    Log.e("TAG", "User data is null")
                                    // Handle the case where user data is null, maybe show an error message to the user
//                                    Toast.makeText(this@MainActivity, "Failed to fetch user data", Toast.LENGTH_SHORT).show()
                                }
                            }

                            override fun onCancelled(error: DatabaseError) {
                                Log.e("TAG", "onCancelled: "+error.message)
                            }
                        })
                    }
                }.addOnFailureListener {
                    Toast.makeText(this, it.message, Toast.LENGTH_SHORT).show()
                }
            }

            // dialog.show()
            // At this point, both email and password are non-empty
            auth.signInWithEmailAndPassword(email, password).addOnCompleteListener { task ->

                //  dialog.dismiss()

                if (task.isSuccessful) {
                    Toast.makeText(this, "Login Successfully", Toast.LENGTH_SHORT).show()
                    var i = Intent(this, DashboardActivity::class.java)
                    startActivity(i)
                }
            }.addOnFailureListener { exception ->
                Log.e("TAG", "error: ${exception.message}")
                Toast.makeText(this@MainActivity, exception.message, Toast.LENGTH_SHORT).show()

            }

        }
    }
        override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
            super.onActivityResult(requestCode, resultCode, data)

            // The activity result pass back to the Facebook SDK
            callbackManager.onActivityResult(requestCode, resultCode, data)
        }

    private fun handleFacebookAccessToken(accessToken: AccessToken) {
        Log.d("TAG", "handleFacebookAccessToken:$accessToken")

        val credential = FacebookAuthProvider.getCredential(accessToken.token)

        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, UI will update with the signed-in user's information
                    Log.d("TAG", "signInWithCredential:success")
                    val user = auth.currentUser
                    Toast.makeText(this@MainActivity, "Authentication Succeeded.", Toast.LENGTH_SHORT).show()
                } else {
                    // If sign-in fails, a message will display to the user.
                    Log.w("TAG", "signInWithCredential:failure", task.exception)
                    Toast.makeText(this@MainActivity, "Authentication failed.", Toast.LENGTH_SHORT).show()
                }
            }
    }
}