package com.rios.spize.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.rios.spize.R
import com.rios.spize.databinding.ActivityLoginBinding
import com.rios.spize.viewmodel.LoginViewModel

class LoginActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var binding: ActivityLoginBinding
    private lateinit var activityResultLauncher: ActivityResultLauncher<Intent>
    private lateinit var viewModel: LoginViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        val clientId = getString(R.string.client_id)
        viewModel = ViewModelProvider(this).get(LoginViewModel::class.java)
        configureGoogleSignIn()

        if (viewModel.isUserLoggedIn()) {
            startActivity(Intent(this, IntroActivity::class.java))
            finish()
        }


//        // Initialize GoogleSignInOptions with the desired options
//        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
//            .requestIdToken(getString(R.string.client_id))
//            .requestEmail()
//            .build()
//
//        // Initialize GoogleSignInClient with the configured GoogleSignInOptions
//        googleSignInClient = GoogleSignIn.getClient(this, gso)
//
//        // Initialize activityResultLauncher inside onCreate
//        activityResultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
//            if (result.resultCode == RESULT_OK) {
//                val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
//                handleSignInResult(task)
//            } else {
//                // Handle sign-in failure
//            }
//        }

        binding.SigninBtn.setOnClickListener {
            // Handle click event here
            signIn() // Call the signIn function or any other logic
        }
        // Other initialization and setup code goes here
    }

    private fun configureGoogleSignIn() {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.client_id))
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(this, gso)
    }

    private fun signIn() {
        val signInIntent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)!!
                viewModel.signInWithGoogle(account.idToken!!,
                    onSuccess = {
                        val userName = account?.displayName
                        val profileImageUri = account?.photoUrl
                        val userMail = account?.email
                        val intent = Intent(this, IntroActivity::class.java)
                        intent.putExtra("userName", userName)
                        val profileImageUriString = profileImageUri?.toString()
                        intent.putExtra("profileImageUri", profileImageUriString)
                        intent.putExtra("userMail", userMail)
                        startActivity(intent) // Start the IntroActivity with the prepared intent
                        finish() // Finish the LoginActivity
                    },
                    onFailure = { errorMessage ->
                        Log.w(TAG, "Google sign in failed")
                        Toast.makeText(baseContext, errorMessage, Toast.LENGTH_SHORT).show()
                    })
            } catch (e: ApiException) {
                Log.w(TAG, "Google sign in failed", e)
            }
        }
    }
//    private fun handleSignInResult(task: Task<GoogleSignInAccount>) {
//        try {
//            val account = task.getResult(ApiException::class.java)
//            // Signed in successfully, handle account
//
//            // Get user information
//            val userName = account?.displayName
//            val profileImageUri = account?.photoUrl
//            val userMail = account?.email
//
//            // Navigate to dashboard activity
//            val intent = Intent(this, IntroActivity::class.java)
//            intent.putExtra("userName", userName)
//            val profileImageUriString = profileImageUri?.toString()
//            intent.putExtra("profileImageUri", profileImageUriString)
//            intent.putExtra("userMail", userMail)
//            startActivity(intent)
//            finish() // Close the current activity
//        } catch (e: ApiException) {
//            // Sign in failed, handle exception
//            Log.e("LoginActivity", "Google sign-in failed", e)
//            // Show a toast or some UI message to the user indicating sign-in failure
//        }
//    }

    companion object {
        private const val TAG = "GoogleActivity"
        private const val RC_SIGN_IN = 9001
    }
}