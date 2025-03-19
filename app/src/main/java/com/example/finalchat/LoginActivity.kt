package com.example.finalchat

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class LoginActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var emailEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var signInButton: Button
    private lateinit var signUpButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        auth = Firebase.auth
        emailEditText = findViewById(R.id.emailEditText)
        passwordEditText = findViewById(R.id.passwordEditText)
        signInButton = findViewById(R.id.signInButton)
        signUpButton = findViewById(R.id.signUpButton)

        signInButton.setOnClickListener {
            signIn()
        }

        signUpButton.setOnClickListener {
            signUp()
        }
    }

    private fun signIn() {
        Log.d("LoginActivity", "signIn started")
        val email = emailEditText.text.toString().trim()
        val password = passwordEditText.text.toString().trim()

        if (email.isNotEmpty() && password.isNotEmpty()) {
            Log.d("LoginActivity", "Email and password not empty")
            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        Log.d("LoginActivity", "signIn successful")
                        Toast.makeText(baseContext, "Sign In Successful.", Toast.LENGTH_SHORT)
                            .show()

                        // Redirect to ActivityUser instead of MainActivity
                        val intent = Intent(this, ActivityUser::class.java)
                        startActivity(intent)
                        finish()

                    } else {
                        Log.e("LoginActivity", "signIn failed: ${task.exception}")
                        Toast.makeText(
                            baseContext,
                            "Sign In failed: ${task.exception?.message}",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
        } else {
            Log.d("LoginActivity", "Email or password empty")
            Toast.makeText(this, "Please enter email and password.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun signUp() {
        val email = emailEditText.text.toString().trim()
        val password = passwordEditText.text.toString().trim()

        if (email.isNotEmpty() && password.isNotEmpty()) {
            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(baseContext, "Sign Up Successful.", Toast.LENGTH_SHORT)
                            .show()

                        // Redirect to ActivityUser after sign up
                        val intent = Intent(this, ActivityUser::class.java)
                        startActivity(intent)
                        finish()

                    } else {
                        Toast.makeText(
                            baseContext,
                            "Sign Up failed: ${task.exception?.message}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
        } else {
            Toast.makeText(this, "Please enter email and password.", Toast.LENGTH_SHORT).show()
        }
    }
}
