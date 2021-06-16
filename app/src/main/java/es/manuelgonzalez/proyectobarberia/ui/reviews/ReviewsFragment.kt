package es.manuelgonzalez.proyectobarberia.ui.reviews

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import es.manuelgonzalez.proyectobarberia.R
import es.manuelgonzalez.proyectobarberia.data.model.Review
import es.manuelgonzalez.proyectobarberia.databinding.ReviewsFragmentBinding
import es.manuelgonzalez.proyectobarberia.utils.OnNavigateUpListener
import es.manuelgonzalez.proyectobarberia.utils.viewBinding


class ReviewsFragment : Fragment(R.layout.reviews_fragment) {

    private val binding: ReviewsFragmentBinding by viewBinding {
        ReviewsFragmentBinding.bind(it)
    }

    private val viewModel: ReviewsFragmentViewModel by viewModels()

    private val listAdapter: ReviewsFragmentAdapter = ReviewsFragmentAdapter()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViews()
        obverveViewModel()
    }

    private fun obverveViewModel() {
        viewModel.reviews.observe(viewLifecycleOwner, { listaReview ->
            showReviews(listaReview)
        })
    }

    private fun showReviews(reviewList: List<Review>) {
        listAdapter.submitList(reviewList)
        binding.lblEmptyView.visibility =
            if (reviewList.isEmpty()) View.VISIBLE else View.INVISIBLE
    }

    private fun setupViews() {
        setupToolbar()
        setupRecyclerView()
    }

    private fun setupRecyclerView() {
        binding.lstReviews.run {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(context)
            adapter = listAdapter
        }
    }

    private fun setupToolbar() {
        binding.toolbar.run {
            title = getString(R.string.reviews_title)
            setNavigationOnClickListener {
                (requireActivity() as OnNavigateUpListener).onNavigateUp()
            }
        }
    }


}