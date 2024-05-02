package com.rios.spize

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.rios.spize.activity.DashboardActivity
import com.rios.spize.activity.IntroActivity
import com.rios.spize.activity.LoginActivity
import com.rios.spize.databinding.ActivityMainBinding
import com.rios.spize.model.UserData

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var authStateListener: FirebaseAuth.AuthStateListener

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize the auth property
        auth = FirebaseAuth.getInstance()

        // Load the Lottie animation
        binding.lottieAnimationView.setAnimation(R.raw.flying)
        binding.lottieAnimationView.playAnimation()

        // Set up the authentication state listener
        authStateListener = FirebaseAuth.AuthStateListener { firebaseAuth ->
            val currentUser = firebaseAuth.currentUser
            val targetActivity = if (currentUser != null) {
                IntroActivity::class.java
            } else {
                LoginActivity::class.java
            }

            // Delay the start of the next activity by 5 seconds
            Handler(Looper.getMainLooper()).postDelayed({
                startActivity(Intent(this, targetActivity))
                finish() // Close the current activity
            }, 5000) // 5000 milliseconds = 5 seconds
        }

        // Start listening for authentication state changes
        auth.addAuthStateListener(authStateListener)
    }

    override fun onDestroy() {
        super.onDestroy()
        // Remove the authentication state listener when the activity is destroyed
        auth.removeAuthStateListener(authStateListener)
    }
}