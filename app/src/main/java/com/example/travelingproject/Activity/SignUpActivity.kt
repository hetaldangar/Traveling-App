package com.example.travelingproject.Activity

import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.example.travelingproject.MainActivity
import com.example.travelingproject.ModelClass.ModelClass
import com.example.travelingproject.R
import com.example.travelingproject.databinding.ActivitySignUpBinding
import com.facebook.AccessToken
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginResult
import com.google.firebase.Firebase
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.io.IOException
import java.util.UUID

class SignUpActivity : AppCompatActivity() {

    lateinit var binding: ActivitySignUpBinding
    private lateinit var auth: FirebaseAuth
    lateinit var firebaseDatabase: FirebaseDatabase

    lateinit var callbackManager: CallbackManager

    lateinit var filePath: Uri
    var downloadUrl: Uri? = null

    lateinit var storage: FirebaseStorage
    lateinit var storageReference: StorageReference

    var id = " "


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initView()
        showImageSourceDialog()
    }

    private fun showImageSourceDialog() {

        val dialogView = layoutInflater.inflate(R.layout.dialog_item_file, null)
        val dialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .setTitle("Select Image Source")
            .create()

        binding.imgUpload.setOnClickListener {
            dialog.show()
            // Handle camera selection

            openGallery()
            dialog.dismiss()
        }


    }

    private fun openGallery() {
        val galleryIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)

        startActivityForResult(galleryIntent, GALLERY_REQUEST_CODE)
    }

    companion object {
        private const val GALLERY_REQUEST_CODE = 100
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // Check if the result comes from the gallery activity and is successful
        if (requestCode == GALLERY_REQUEST_CODE && resultCode == RESULT_OK && data != null && data.data != null) {
            // If the result is from the gallery and successful, assign the selected image URI to the filePath property
            filePath = data.data!! // Ensure you are assigning the result to the correct property
            try {
                // Load the selected image into a Bitmap and display it in the ImageView
                val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, filePath)
                binding.imgUpload.setImageBitmap(bitmap)
            } catch (e: IOException) {
                // Handle any errors that occur while loading the image
                e.printStackTrace()
            }
        }
    }

    private fun setupProfilePictureSelection() {
        binding.imgUpload.setOnClickListener {
            selectImage()
        }
    }

    // Launch image selection intent
    private fun selectImage() {
        val intent = Intent().apply {
            type = "image/*"
            action = Intent.ACTION_GET_CONTENT
        }
        startActivityForResult(
            Intent.createChooser(intent, "Select Image from here..."),
            GALLERY_REQUEST_CODE
        )
    }

    private fun initView() {

        auth = Firebase.auth
        callbackManager = CallbackManager.Factory.create()
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

        firebaseDatabase = FirebaseDatabase.getInstance()
        binding.txtSignin.setOnClickListener {
            var i = Intent(this@SignUpActivity, MainActivity::class.java)
            startActivity(i)
        }

        binding.btnCreateAccount.setOnClickListener {

            //  val dialog = createProgressDialog()

            val email = binding.edtEmail.text.toString()
            val password = binding.edtPassword.text.toString()
            val username = binding.edtUsername.text.toString()
            val address = binding.edtAddress.text.toString()
            val mobile = binding.edtMobile.text.toString()
            val userType = if (binding.rbUser.isChecked) "User" else "Admin"
            val selectedCategory =
                if (userType == "User") 1 else 0 // Convert userType string to integer


            if (TextUtils.isEmpty(username)) {
                binding.edtUsername.setError("Enter UserName")
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
            } else if (TextUtils.isEmpty(address)) {
                binding.edtAddress.setError("Enter Your Address")
                binding.edtAddress.requestFocus()
                return@setOnClickListener
            } else if (TextUtils.isEmpty(mobile)) {
                binding.edtMobile.setError("Enter Your Number ")
                binding.edtMobile.requestFocus()

            } else if (binding.edtMobile.length() < 10 != binding.edtMobile.length() > 10) {
                binding.edtMobile.setError("Enter 10 digit Number ")
                binding.edtMobile.requestFocus()
            }
            //   dialog.show()


            // At this point, both email and password are non-empty
            auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener { task ->

                //     dialog.dismiss()

                if (task.isSuccessful) {

                    val user = auth.currentUser

                    Toast.makeText(this, "Account Created successfully", Toast.LENGTH_SHORT).show()

                    val databaseReference = firebaseDatabase.reference.child("DetailsTb")

                    // Create a ModelClass instance with the user data
//                    val selectedCategory = when (binding.rgGroup.checkedRadioButtonId) {
//                        R.id.rbAdmin -> "Admin"
//                        R.id.rbUser -> "User"
//                        else -> ""
//                    }
                    val model = ModelClass(
                        user!!.uid, // Assuming you want to use the UID as the ID
                        binding.edtUsername.text.toString(),
                        binding.edtEmail.text.toString(),
                        binding.edtPassword.text.toString(),
                        binding.edtAddress.text.toString(),
                        binding.edtMobile.text.toString(),
                        selectedCategory
                    )
                    databaseReference.child(user.uid).setValue(model)
                        .addOnSuccessListener {
                            Toast.makeText(
                                this@SignUpActivity,
                                "Data inserted successfully",
                                Toast.LENGTH_SHORT
                            ).show()
                            // Clear input fields after successful insertion
//                            binding.edtUsername.setText("")
//                            binding.edtEmail.setText("")
//                            binding.edtPassword.setText("")
//                            binding.edtAddress.setText("")
//                            binding.edtMobile.setText("")
                            sharedPreferences()

                            var i = Intent(this, DashboardActivity::class.java)
                            startActivity(i)
                        }
                        .addOnFailureListener { e ->
                            Log.e("TAG", "Error inserting data: ${e.message}")
                            Toast.makeText(
                                this@SignUpActivity,
                                "Failed to insert data",
                                Toast.LENGTH_SHORT
                            ).show()
                        }

                }
            }.addOnFailureListener { exception ->
                Log.e("TAG", "error: ${exception.message}")
                Toast.makeText(this@SignUpActivity, exception.message, Toast.LENGTH_SHORT).show()

            }
//            if (id.isEmpty()) {
//                val key = firebaseDatabase.reference.root.child("DetailsTb").push().key
//                Log.e("TAG", "initView: ==>" + key)
//
//                val selectedCategory = when (binding.rgGroup.checkedRadioButtonId) {
//                    R.id.rbAdmin -> "Admin"
//                    R.id.rbUser -> "User"
//                    else -> ""
//                }
//
//                var model = key?.let { it1 ->
//                    ModelClass(
//                        it1,
//                        binding.edtUsername.text.toString(),
//                        binding.edtEmail.text.toString(),
//                        binding.edtPassword.text.toString(),
//                        binding.edtAddress.text.toString(),
//                        binding.edtMobile.text.toString(),
//                        selectedCategory
//                    )
//                }
//                key?.let { it1 ->
//                    firebaseDatabase.reference.root.child("DetailsTb").child(it1)
//                        .setValue(model)
//
//                        .addOnCompleteListener {
//                            if (it.isSuccessful) {
//                                Toast.makeText(
//                                    this@SignUpActivity,
//                                    "record inserted successfully",
//                                    Toast.LENGTH_SHORT
//                                ).show()
//                                binding.edtUsername.setText("")
//                                binding.edtEmail.setText("")
//                                binding.edtPassword.setText("")
//                                binding.edtAddress.setText("")
//                                binding.edtMobile.setText("") } else {
//                                Toast.makeText(
//                                    this@SignUpActivity,
//                                    "fail -->",
//                                    Toast.LENGTH_SHORT
//                                ).show()
//                                Log.e("TAG", "initView: " + it.exception?.message)
//                            }
//                        }.addOnFailureListener {
//                            Toast.makeText(this@SignUpActivity, "fail", Toast.LENGTH_SHORT)
//                                .show()
//                            Log.e("TAG", "initView: " + it.message)
//                        }
//                }
//            }
        }
        binding.edtPassword.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                val password = s.toString()
                if (password.length < 8) {
                    binding.edtPassword.setError("Password must be at least 8 characters long")
                } else {
                    binding.edtPassword.error = null
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // Not used
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // Not used
            }
        })

    }

    private fun sharedPreferences() {
        val sharedPreferences = getSharedPreferences("my_preferences", MODE_PRIVATE)

        val editor = sharedPreferences.edit()

        with(binding) {

            // Assuming you have variables edtUserName and edtEmail defined earlier
            editor.putString("username", edtUsername.text.toString())
            editor.putString("email", edtEmail.text.toString())
            editor.putString("password", edtPassword.text.toString())
            editor.putString("address", edtAddress.text.toString())
            editor.putString("mobile", edtMobile.text.toString())
            editor.putString("type",if (rbUser.isChecked)"User" else "Admin")
            editor.putString("Image", filePath.toString())

            editor.putBoolean("isLogin", true)
        }

        editor.apply()

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
                    Toast.makeText(
                        this@SignUpActivity,
                        "Authentication Succeeded.",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    // If sign-in fails, a message will display to the user.
                    Log.w("TAG", "signInWithCredential:failure", task.exception)
                    Toast.makeText(
                        this@SignUpActivity,
                        "Authentication failed.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
    }

    private fun uploadImage() {
        if (filePath != null) {
            val progressDialog = ProgressDialog(this)
            progressDialog.setTitle("Uploading...")
            progressDialog.show()

            val ref = storageReference.child("images/" + UUID.randomUUID().toString())


            ref.putFile(filePath).addOnSuccessListener { // Image uploaded successfully
                // Dismiss dialog
                progressDialog.dismiss()
                Toast.makeText(this@SignUpActivity, "Image Uploaded!!", Toast.LENGTH_SHORT)
                    .show()

                ref.downloadUrl.addOnSuccessListener { uri ->

                    downloadUrl = uri
                    Log.e("TAG", "uploadImage: downloadable URL $uri")

                }
            }.addOnFailureListener { e -> // Error, Image not uploaded
                progressDialog.dismiss()
                Toast.makeText(this@SignUpActivity, "Failed " + e.message, Toast.LENGTH_SHORT)
                    .show()
            }.addOnProgressListener { taskSnapshot ->
                val progress =
                    (100.0 * taskSnapshot.bytesTransferred / taskSnapshot.totalByteCount)

                progressDialog.setMessage("Uploaded " + progress.toInt() + "%")
            }
        }
    }

}