package com.rios.spize.activity

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

import com.bumptech.glide.Glide
import com.google.android.material.textfield.MaterialAutoCompleteTextView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.rios.spize.R
import com.rios.spize.databinding.ActivityIntroBinding
import com.rios.spize.model.UserData

class IntroActivity : AppCompatActivity() {

    private lateinit var binding: ActivityIntroBinding
    private lateinit var userData: UserData
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityIntroBinding.inflate(layoutInflater)
        setContentView(binding.root)
        userData = UserData("", "", "") // Provide default values or appropriate initialization
        // Define gender options
        val genders = arrayOf("Male", "Female")

// Create an ArrayAdapter using the string array and a default spinner layout
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, genders)

// Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

// Apply the adapter to the spinner
        val spinner = findViewById<Spinner>(R.id.genderSpinner)
        spinner.adapter = adapter

        // Load user data locally
//        loadUserDataLocally()

        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser == null) {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
            return
        }

        val userId = currentUser.uid
        val usersRef = FirebaseDatabase.getInstance().getReference("users").child(userId)

        usersRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val userData = dataSnapshot.getValue(UserData::class.java)
                if (userData != null) {
                    setUserData(userData)
                } else {
                    val id = currentUser.uid
                    val userName = intent.getStringExtra("userName")
                    val profileImageUriString = intent.getStringExtra("profileImageUri")
                    val userMail = intent.getStringExtra("userMail")
                    val userData = UserData(id,userName, userMail, profileImageUriString)
                    setUserData(userData)

                    saveUserDataToFirebase(userId, userData)
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Handle database error
                Toast.makeText(this@IntroActivity, DatabaseError.NETWORK_ERROR, Toast.LENGTH_SHORT).show()
            }
        })

        binding.continueButton.setOnClickListener {
             userData.userName = binding.Name.text.toString()
            userData.userAge = 26
            val intent = Intent(this, DashboardActivity::class.java)
            startActivity(intent) // Start the IntroActivity with the prepared intent
            finish() // Fini
        }

        binding.profile.setOnClickListener {


        }
//        setupRadioButtonClickListener()
    }

    private fun setUserData(userData: UserData) {
        binding.NameLayout.editText?.setText(userData.userName)
        binding.mailLayout.editText?.setText(userData.userMail)
        val spinner = findViewById<Spinner>(R.id.genderSpinner)
        val genderIndex = if (userData.userGender == "Male") 0 else 1
        spinner.setSelection(genderIndex)

        // Load image using Glide
        Glide.with(this)
            .load(userData.userProfileImage)
            .placeholder(R.drawable.ic_launcher_foreground)
            .error(R.drawable.ic_launcher_background)
            .into(binding.profile)
    }

    private fun saveUserDataToFirebase(userId: String, userData: UserData) {
        val usersRef = FirebaseDatabase.getInstance().getReference("users").child(userId)
        usersRef.setValue(userData)
    }


}

