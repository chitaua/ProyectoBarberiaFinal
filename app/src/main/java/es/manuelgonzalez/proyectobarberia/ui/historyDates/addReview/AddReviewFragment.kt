package es.manuelgonzalez.proyectobarberia.ui.historyDates.addReview

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import es.manuelgonzalez.proyectobarberia.R
import es.manuelgonzalez.proyectobarberia.data.model.Review
import es.manuelgonzalez.proyectobarberia.databinding.AddReviewFragmentBinding
import es.manuelgonzalez.proyectobarberia.utils.hideSoftKeyboard
import es.manuelgonzalez.proyectobarberia.utils.viewBinding

class AddReviewFragment : Fragment(R.layout.add_review_fragment) {

    private var currentUserId: String = ""
    private var newReviewId: String = ""
    private var currentIdDate: String = ""
    private val binding: AddReviewFragmentBinding by viewBinding {
        AddReviewFragmentBinding.bind(it)
    }

    private val viewModel: AddReviewViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setFragmentResultListener("requestKeyToDoReview") { key, bundle ->
            val result = bundle.getString("reviewKey")
            currentIdDate = result.toString()
        }
        setupViews()
        observeViewModel()
    }

    private fun observeViewModel() {
        viewModel.currentUser.observe(viewLifecycleOwner, {
            currentUserId = it.uid
        })
        viewModel.newIdReview.observe(viewLifecycleOwner, {
            newReviewId = it.toString()
        })
    }

    private fun setupViews() {
        setupToolbar()
        binding.btnAddReview.setOnClickListener {
            viewModel.addReview(
                Review(
                    newReviewId,
                    currentUserId,
                    currentIdDate,
                    binding.ratingBar.rating + 0.1F,
                    binding.txtReviewText.text.toString()
                )
            )
            binding.lblReviewText.hideSoftKeyboard()
            requireActivity().onBackPressed()
        }
    }

    private fun setupToolbar() {
        binding.toolbar.run {
            title = getString(R.string.add_review_title)
            setNavigationIcon(R.drawable.ic_baseline_arrow_back_24)
            setNavigationOnClickListener {
                requireActivity().onBackPressed()
            }
        }
    }


}