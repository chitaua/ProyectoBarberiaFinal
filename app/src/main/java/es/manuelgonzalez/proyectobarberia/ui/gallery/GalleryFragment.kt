package es.manuelgonzalez.proyectobarberia.ui.gallery

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import es.manuelgonzalez.proyectobarberia.R
import es.manuelgonzalez.proyectobarberia.data.model.Photo
import es.manuelgonzalez.proyectobarberia.databinding.GalleryFragmentBinding
import es.manuelgonzalez.proyectobarberia.utils.OnNavigateUpListener
import es.manuelgonzalez.proyectobarberia.utils.viewBinding


class GalleryFragment : Fragment(R.layout.gallery_fragment) {

    private val binding: GalleryFragmentBinding by viewBinding {
        GalleryFragmentBinding.bind(it)
    }

    private val viewModel: GalleryFragmentViewModel by viewModels()

    private val listAdapter: GalleryFragmentAdapter = GalleryFragmentAdapter()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViews()
        observeViewModel()
    }

    private fun setupViews() {
        setupToolbar()
        setupRecyclerView()
    }

    private fun setupRecyclerView() {
        binding.lstPhotos.run {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(context)
            itemAnimator = DefaultItemAnimator()
            adapter = listAdapter
        }
    }

    private fun setupToolbar() {
        binding.toolbar.run {
            title = getString(R.string.gallery_title)
            setNavigationOnClickListener {
                (requireActivity() as OnNavigateUpListener).onNavigateUp()
            }
        }
    }

    private fun observeViewModel() {
        viewModel.photos.observe(viewLifecycleOwner) { showPhotos(it) }
    }

    private fun showPhotos(photos: List<Photo>) {
        listAdapter.submitList(photos)
        binding.lblEmptyView.visibility =
            if (photos.isEmpty()) View.VISIBLE else View.INVISIBLE
    }


}