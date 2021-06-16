package es.manuelgonzalez.proyectobarberia.ui.about

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import es.manuelgonzalez.proyectobarberia.R
import es.manuelgonzalez.proyectobarberia.databinding.AboutFragmentBinding
import es.manuelgonzalez.proyectobarberia.utils.OnNavigateUpListener
import es.manuelgonzalez.proyectobarberia.utils.viewBinding

class AboutFragment : Fragment(R.layout.about_fragment), OnMapReadyCallback {

    private val binding: AboutFragmentBinding by viewBinding {
        AboutFragmentBinding.bind(it)
    }

    private val listAdapter: AboutFragmentAdapter = AboutFragmentAdapter()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val supportMapFragment: SupportMapFragment =
            childFragmentManager.findFragmentById(R.id.fragmentMap) as SupportMapFragment
        supportMapFragment.getMapAsync(this)
        setupViews()
    }

    private fun setupViews() {
        setupToolbar()
        setupCallButton()
        setupRecyclerView()
    }

    private fun setupRecyclerView() {
        binding.lstSchedule.run {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(context)
            itemAnimator = DefaultItemAnimator()
            adapter = listAdapter
        }
    }

    private fun setupCallButton() {
        binding.btnCall.setOnClickListener { callPhone() }
    }

    private fun callPhone() {
        val phoneNumber = binding.lblTelephoneNumber.text
        val intent = Intent(
            Intent.ACTION_DIAL,
            Uri.parse("tel:$phoneNumber")
        )
        try {
            startActivity(intent)
        } catch (e: ActivityNotFoundException) {
            e.message
        }
    }

    private fun setupToolbar() {
        binding.toolbar.run {
            title = getString(R.string.about_title)
            setNavigationOnClickListener {
                (requireActivity() as OnNavigateUpListener).onNavigateUp()
            }
        }
    }

    override fun onMapReady(map: GoogleMap) {
        val coordinates = LatLng(36.121183, -5.451275)
        val marker = MarkerOptions().position(coordinates).title("Ubicación")
        map.setOnMapClickListener { showInMapIntent() }
        map.addMarker(marker)
        map.animateCamera(
            CameraUpdateFactory.newLatLngZoom(coordinates, 18f),
            4000,
            null
        )
    }

    private fun showInMapIntent() {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("geo:36.121183,-5.451275?z=18f"))
        try {
            startActivity(intent)
        } catch (e: ActivityNotFoundException) {
            e.message
        }
    }

}