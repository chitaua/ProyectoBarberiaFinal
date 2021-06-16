package es.manuelgonzalez.proyectobarberia

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.fragment.app.commit
import com.google.android.material.snackbar.Snackbar
import es.manuelgonzalez.proyectobarberia.databinding.MainActivityBinding
import es.manuelgonzalez.proyectobarberia.ui.about.AboutFragment
import es.manuelgonzalez.proyectobarberia.ui.auth.LoginActivity
import es.manuelgonzalez.proyectobarberia.ui.dates.DatesFragment
import es.manuelgonzalez.proyectobarberia.ui.gallery.GalleryFragment
import es.manuelgonzalez.proyectobarberia.ui.historyDates.HistoryDatesFragment
import es.manuelgonzalez.proyectobarberia.ui.reviews.ReviewsFragment
import es.manuelgonzalez.proyectobarberia.utils.FirebaseUtils.firebaseAuth
import es.manuelgonzalez.proyectobarberia.utils.OnNavigateUpListener

class MainActivity : AppCompatActivity(), OnNavigateUpListener {

    private val binding: MainActivityBinding by lazy {
        MainActivityBinding.inflate(layoutInflater)
    }

    override fun onNavigateUp(): Boolean {
        binding.drawerLayout.openDrawer(GravityCompat.START)
        return true
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setupViews()
    }

    private fun setupViews() {
        setupNavigationView()
    }

    private fun setupNavigationView() {
        binding.navigationView.run {
            // Select initial option
            setCheckedItem(R.id.mnuDates)
            setNavigationItemSelectedListener { navigateToOption(it) }
        }
    }

    private fun navigateToOption(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.mnuDates -> navigateToDates()
            R.id.mnuHistoryDates -> navigateToHistoryDates()
            R.id.mnuReviews -> navigateToReviews()
            R.id.mnuGallery -> navigateToGallery()
            R.id.mnuAbout -> navigateToAbout()
            R.id.mnuLogOut -> logOut()
        }
        binding.drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

    private fun logOut() {
        firebaseAuth.signOut()
        startActivity(Intent(this, LoginActivity::class.java))
        Snackbar.make(binding.root, "Sesión cerrada correctamente", Snackbar.LENGTH_LONG)
            .show()
        finish()
    }

    private fun navigateToDates() {
        supportFragmentManager.commit {
            replace(R.id.fcContent, DatesFragment())
        }
    }

    private fun navigateToHistoryDates() {
        supportFragmentManager.commit {
            replace(R.id.fcContent, HistoryDatesFragment())
        }
    }

    private fun navigateToReviews() {
        supportFragmentManager.commit {
            replace(R.id.fcContent, ReviewsFragment())
        }
    }

    private fun navigateToGallery() {
        supportFragmentManager.commit {
            replace(R.id.fcContent, GalleryFragment())
        }
    }

    private fun navigateToAbout() {
        supportFragmentManager.commit {
            replace(R.id.fcContent, AboutFragment())
        }
    }

    override fun onBackPressed() {
        if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            binding.drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }
}