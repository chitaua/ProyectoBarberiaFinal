package es.manuelgonzalez.proyectobarberia.ui.historyDates

import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.fragment.app.*
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import es.manuelgonzalez.proyectobarberia.R
import es.manuelgonzalez.proyectobarberia.data.model.Date
import es.manuelgonzalez.proyectobarberia.data.model.User
import es.manuelgonzalez.proyectobarberia.databinding.HistoryDatesFragmentBinding
import es.manuelgonzalez.proyectobarberia.ui.historyDates.addReview.AddReviewFragment
import es.manuelgonzalez.proyectobarberia.ui.userInfo.UserInfoFragment
import es.manuelgonzalez.proyectobarberia.utils.OnNavigateUpListener
import es.manuelgonzalez.proyectobarberia.utils.viewBinding

class HistoryDatesFragment : Fragment(R.layout.history_dates_fragment) {

    private val binding: HistoryDatesFragmentBinding by viewBinding {
        HistoryDatesFragmentBinding.bind(it)
    }

    lateinit var currentUser: User

    private val viewModel: HistoryDatesFragmentViewModel by viewModels()

    private val listAdapter: HistoryDatesFragmentAdapter = HistoryDatesFragmentAdapter().apply {
        onDoReviewClickListener = {
            if (currentUser.admin)
                navigateToSeeUser(it)
            else
                navigateToDoReview(it)
        }
    }

    private fun navigateToDoReview(position: Int) {
        observeDate(position)
    }

    private fun navigateToSeeUser(position: Int) {
        val date: Date = listAdapter.currentList[position]
        setFragmentResult("requestKeyFromHistory", bundleOf("dateKey" to date.idDate))
        parentFragmentManager.commit {
            setReorderingAllowed(true)
            replace<UserInfoFragment>(R.id.fcContent)
            addToBackStack("")
        }
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViews()
        observeViewModel()
    }

    private fun observeViewModel() {
        observeUser()
    }

    private fun observeDate(position: Int) {
        val date: Date = listAdapter.currentList[position]
        viewModel.getReviewByIdDate(date.idDate).observe(viewLifecycleOwner, {
            if (it == null) {
                setFragmentResult("requestKeyToDoReview", bundleOf("reviewKey" to date.idDate))
                parentFragmentManager.commit {
                    setReorderingAllowed(true)
                    replace<AddReviewFragment>(R.id.fcContent)
                    addToBackStack("")
                }
            } else {
                Snackbar.make(
                    binding.root,
                    "Esa cita ya tiene una reseña existente",
                    Snackbar.LENGTH_LONG
                ).show()
            }
        })
    }


    private fun observeHistoryDates(isAdmin: Boolean) {
        if (isAdmin) {
            viewModel.datesHistory.observe(viewLifecycleOwner, {
                updateList(it)
            })
        } else {
            viewModel.datesHistoryByCurrentUser.observe(viewLifecycleOwner, {
                updateList(it)
            })
        }
    }


    private fun observeUser() {
        viewModel.currentUser.observe(viewLifecycleOwner, { user ->
            observeHistoryDates(user.admin)
            currentUser = user
            listAdapter.sendData(user)
        })
    }

    private fun setupViews() {
        setupToolbar()
        setupRecyclerView()

    }

    private fun setupRecyclerView() {
        binding.lstHistoryDate.run {
            setHasFixedSize(true)
            layoutManager = GridLayoutManager(context, 1)
            itemAnimator = DefaultItemAnimator()
            addItemDecoration(DividerItemDecoration(context, RecyclerView.VERTICAL))
            adapter = listAdapter
        }
    }

    private fun updateList(newList: List<Date>) {
        listAdapter.submitList(newList)
        binding.run {
            lblEmptyView.visibility = if (newList.isEmpty()) View.VISIBLE else View.GONE
        }
    }

    private fun setupToolbar() {
        binding.toolbar.run {
            title = getString(R.string.history_dates_title)
            setNavigationOnClickListener {
                (requireActivity() as OnNavigateUpListener).onNavigateUp()
            }
        }
    }


}