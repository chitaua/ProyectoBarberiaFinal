package es.manuelgonzalez.proyectobarberia.ui.auth

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseUser
import es.manuelgonzalez.proyectobarberia.MainActivity
import es.manuelgonzalez.proyectobarberia.R
import es.manuelgonzalez.proyectobarberia.databinding.LoginActivityBinding
import es.manuelgonzalez.proyectobarberia.utils.FirebaseUtils.firebaseAuth
import es.manuelgonzalez.proyectobarberia.utils.hideSoftKeyboard

class LoginActivity : AppCompatActivity() {

    private lateinit var signInEmail: String
    private lateinit var signInPassword: String

    private val binding: LoginActivityBinding by lazy {
        LoginActivityBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setupViews()
    }

    override fun onStart() {
        super.onStart()
        val user: FirebaseUser? = firebaseAuth.currentUser
        user?.let {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }

    private fun setupViews() {
        binding.txtEmail.setCompoundDrawablesRelativeWithIntrinsicBounds(
            R.drawable.ic_baseline_email_24,
            0,
            0,
            0
        )
        binding.txtPassword.setCompoundDrawablesRelativeWithIntrinsicBounds(
            R.drawable.ic_baseline_vpn_key_24,
            0,
            0,
            0
        )
        binding.btnLogin.setOnClickListener { loginUser() }
        binding.btnCreateAccount.setOnClickListener { navigateToRegister() }
        binding.lblForgotPass.setOnClickListener { navigateToPassRecovery() }
    }


    private fun notEmpty(): Boolean = signInEmail.isNotEmpty() && signInPassword.isNotEmpty()

    private fun loginUser() {
        binding.txtEmail.hideSoftKeyboard()
        signInEmail = binding.txtEmail.text.toString().trim()
        signInPassword = binding.txtPassword.text.toString().trim()

        if (notEmpty()) {
            firebaseAuth.signInWithEmailAndPassword(signInEmail, signInPassword)
                .addOnCompleteListener { signIn ->
                    if (signIn.isSuccessful) {
                        startActivity(Intent(this, MainActivity::class.java))
                        finish()
                    } else {
                        Snackbar.make(
                            binding.root,
                            "Email y/o contraseña incorrectos",
                            Snackbar.LENGTH_LONG
                        )
                            .show()
                    }
                }
        } else {
            Snackbar.make(binding.root, "Los campos no pueden estar vacíos", Snackbar.LENGTH_LONG)
                .show()
        }
    }

    private fun navigateToRegister() {
        startActivity(Intent(this, RegisterActivity::class.java))
    }

    private fun navigateToPassRecovery() {
        startActivity(Intent(this, PassRecoveryActivity::class.java))
    }


}