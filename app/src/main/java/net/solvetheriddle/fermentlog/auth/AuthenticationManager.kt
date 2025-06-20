package net.solvetheriddle.fermentlog.auth

import android.app.Activity
import android.util.Log
import androidx.activity.ComponentActivity
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract
import com.firebase.ui.auth.data.model.FirebaseAuthUIAuthenticationResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import net.solvetheriddle.fermentlog.R

class AuthenticationManager(private val activity: ComponentActivity) {
    private val auth = FirebaseAuth.getInstance()
    private val _currentUser = MutableStateFlow<FirebaseUser?>(null)
    val currentUser: StateFlow<FirebaseUser?> = _currentUser.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    init {
        // Update current user when auth state changes
        auth.addAuthStateListener { firebaseAuth ->
            _currentUser.value = firebaseAuth.currentUser
        }
    }

    // Check if user is already signed in
    fun isUserSignedIn(): Boolean = auth.currentUser != null

    // Get current user email or null if not signed in
    fun getUserEmail(): String? = auth.currentUser?.email

    // Sign in with Google using FirebaseUI
    private val signInLauncher = activity.registerForActivityResult(
        FirebaseAuthUIActivityResultContract()
    ) { result ->
        onSignInResult(result)
    }

    private fun onSignInResult(result: FirebaseAuthUIAuthenticationResult) {
        if (result.resultCode == Activity.RESULT_OK) {
            // Successfully signed in
            val user = FirebaseAuth.getInstance().currentUser

            if (user != null) {
                Log.d("AuthUI_GoogleSignIn", "Google Sign-In successful. User ID: ${user.uid}")
                Log.d("AuthUI_GoogleSignIn", "User Email: ${user.email}") // << CHECK THIS!
                Log.d("AuthUI_GoogleSignIn", "User Display Name: ${user.displayName}")
                Log.d("AuthUI_GoogleSignIn", "Provider ID: ${user.providerId}") // Should be "google.com"

                // If user.email is null here, that's a big problem leading to the error.
                if (user.email == null) {
                    Log.e("AuthUI_GoogleSignIn", "ERROR: User email is null after Google Sign-In!")
                    // This is likely the root cause if you hit this.
                }
            } else {
                Log.e("AuthUI_GoogleSignIn", "ERROR: FirebaseAuth.getInstance().currentUser is null after successful sign-in response!")
                // This would be very unusual if resultCode is RESULT_OK.
            }
            Log.d(TAG, "Successfully signed in user: ${auth.currentUser?.email}")
            _error.value = null // Clear any previous errors
        } else {
            // Sign in failed
            val response = result.idpResponse
            val errorMessage = response?.error?.message ?: "Sign in failed"
            Log.w(TAG, errorMessage)
            _error.value = errorMessage
        }
    }

    
    fun signIn() {
        val providers = arrayListOf(
            AuthUI.IdpConfig.GoogleBuilder().build()
        )

        val signInIntent = AuthUI.getInstance()
            .createSignInIntentBuilder()
            .setAvailableProviders(providers)
            .setLogo(R.mipmap.ic_launcher)
            .setTheme(R.style.Theme_Ferment)
            .build()

        signInLauncher.launch(signInIntent)
    }

    fun clearError() {
        _error.value = null
    }

    fun signOut() {
        AuthUI.getInstance()
            .signOut(activity)
            .addOnCompleteListener {
                _currentUser.value = null
            }
    }

    companion object {
        private const val TAG = "AuthenticationManager"
    }
}
