package es.manuelgonzalez.proyectobarberia.ui.auth

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseUser
import es.manuelgonzalez.proyectobarberia.MainActivity
import es.manuelgonzalez.proyectobarberia.data.FirestoreDB
import es.manuelgonzalez.proyectobarberia.data.model.User
import es.manuelgonzalez.proyectobarberia.databinding.RegisterActivityBinding
import es.manuelgonzalez.proyectobarberia.utils.FirebaseUtils.firebaseAuth
import es.manuelgonzalez.proyectobarberia.utils.hideSoftKeyboard

class RegisterActivity : AppCompatActivity() {

    private lateinit var userEmail: String
    private lateinit var userPassword: String

    private val binding: RegisterActivityBinding by lazy {
        RegisterActivityBinding.inflate(layoutInflater)
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
        }
    }

    private fun notEmpty(): Boolean = binding.txtFullName.text.toString().trim().isNotEmpty() &&
            binding.txtEmail.text.toString().trim().isNotEmpty() &&
            binding.txtTelephone.text.toString().trim().isNotEmpty() &&
            binding.txtPassword.text.toString().trim().isNotEmpty() &&
            binding.txtConfirmPassword.text.toString().trim().isNotEmpty()

    private fun identicalPassword(): Boolean {
        var identical = false
        if (notEmpty() &&
            binding.txtPassword.text.toString().trim() == binding.txtPassword.text.toString().trim()
        ) {
            identical = true
        } else if (!notEmpty()) {
            Snackbar.make(binding.root, "Todos los campos son obligatorios", Snackbar.LENGTH_LONG)
                .show()
        } else {
            Snackbar.make(binding.root, "Las contraseñas no coinciden", Snackbar.LENGTH_LONG).show()
        }
        return identical
    }

    private fun setupViews() {
        binding.btnRegister.setOnClickListener { registerUser() }
    }

    private fun registerUser() {
        binding.txtEmail.hideSoftKeyboard()
        if (identicalPassword()) {
            // identicalPassword() returns true only  when inputs are not empty and passwords are identical
            userEmail = binding.txtEmail.text.toString().trim()
            userPassword = binding.txtPassword.text.toString().trim()

            /*create a user*/
            firebaseAuth.createUserWithEmailAndPassword(userEmail, userPassword)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        FirestoreDB.addUser(
                            User(
                                firebaseAuth.uid.toString(),
                                binding.txtFullName.text.toString(),
                                binding.txtEmail.text.toString(),
                                binding.txtTelephone.text.toString(),
                                false,
                                false
                            )
                        )
                        Log.i("", "Usuario registrado correctamente")
                        startActivity(Intent(this, MainActivity::class.java))
                        finish()
                    } else {
                        Snackbar.make(
                            binding.root,
                            "El email introducido ya existe",
                            Snackbar.LENGTH_LONG
                        ).show()
                    }
                }
        }
    }
}