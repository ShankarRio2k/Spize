package com.rios.spize.viewmodel

import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider

class AuthViewmodel : ViewModel() {
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    fun signInWithGoogle(idToken: String, onSuccess: () -> Unit, onFailure: (String) -> Unit) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    onSuccess.invoke()
                } else {
                    onFailure.invoke("Authentication failed.")
                }
            }
    }

    fun isUserLoggedIn(): Boolean {
        return auth.currentUser != null
    }
}