package es.manuelgonzalez.proyectobarberia.ui.auth

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import es.manuelgonzalez.proyectobarberia.databinding.PassRecoveryActivityBinding
import es.manuelgonzalez.proyectobarberia.utils.FirebaseUtils.firebaseAuth
import es.manuelgonzalez.proyectobarberia.utils.hideSoftKeyboard

class PassRecoveryActivity : AppCompatActivity() {

    private val binding: PassRecoveryActivityBinding by lazy {
        PassRecoveryActivityBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setupViews()
    }

    private fun setupViews() {
        binding.btnRecoveryPass.setOnClickListener { sendRecoveryEmail() }
    }

    private fun sendRecoveryEmail() {
        binding.txtEmail.hideSoftKeyboard()
        if (binding.txtEmail.text.isEmpty()) {
            Snackbar.make(binding.root, "El campo no puede estar vacío", Snackbar.LENGTH_LONG)
                .show()
        } else {
            firebaseAuth.sendPasswordResetEmail(binding.txtEmail.text.toString())
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Snackbar.make(
                            binding.root,
                            "Se ha enviado un email, compruebe la bandeja de entrada",
                            Snackbar.LENGTH_LONG
                        ).show()
                        startActivity(Intent(this, LoginActivity::class.java))
                        finish()
                    } else {
                        Snackbar.make(
                            binding.root,
                            "El email introducido no ha sido registrado",
                            Snackbar.LENGTH_LONG
                        ).show()
                    }
                }
        }
    }


}