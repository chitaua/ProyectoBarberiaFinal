package es.manuelgonzalez.proyectobarberia.ui.userInfo

import android.app.AlertDialog
import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import es.manuelgonzalez.proyectobarberia.R
import es.manuelgonzalez.proyectobarberia.data.model.User
import es.manuelgonzalez.proyectobarberia.databinding.UserInfoFragmentBinding
import es.manuelgonzalez.proyectobarberia.utils.viewBinding

class UserInfoFragment : Fragment(R.layout.user_info_fragment) {

    private val binding: UserInfoFragmentBinding by viewBinding {
        UserInfoFragmentBinding.bind(it)
    }

    private val viewModel: UserInfoFragmentViewModel by viewModels()

    lateinit var user: User

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setFragmentResultListener("requestKey") { key, bundle ->
            val result = bundle.getString("bundleKey")
            val idDate = result.toString()
            observeViewModelFromDates(idDate)
        }
        setFragmentResultListener("requestKeyFromHistory") { key, bundle ->
            val result = bundle.getString("dateKey")
            val idDate = result.toString()
            observeViewModelFromHistory(idDate)
        }
        setupViews()
    }

    private fun observeViewModelFromDates(idDate: String) {
        viewModel.getDatebyIdDate(idDate).observe(viewLifecycleOwner, {
            viewModel.getUser(it.idUser!!).observe(viewLifecycleOwner, {
                setupFields(it, idDate, true)
                user = it
            })
        })
    }

    private fun observeViewModelFromHistory(idDate: String) {
        viewModel.getDatebyIdDate(idDate).observe(viewLifecycleOwner, {
            viewModel.getUser(it.idUser!!).observe(viewLifecycleOwner, {
                setupFields(it, idDate, false)
                user = it
            })
        })
    }

    private fun setupFields(usuario: User, idDate: String, haveCancelButton: Boolean) {
        binding.run {
            txtName.text = usuario.fullName
            txtEmail.text = usuario.email
            txtPhone.text = usuario.telephone
            txtActiveDate.text = idDate
            if (!haveCancelButton)
                btnCancelDate.visibility = View.GONE
        }
    }

    private fun setupViews() {
        setupToolbar()
        setupButtons()
    }

    private fun setupButtons() {
        binding.run {
            btnSendEmailToUser.setOnClickListener { sendEmail() }
            btnCallUser.setOnClickListener { callPhone() }
            btnCancelDate.setOnClickListener { cancelDate() }
        }
    }

    private fun cancelDate() {

        AlertDialog.Builder(requireContext())
            .setMessage("¿Estás seguro de querer eliminar la cita?")
            .setPositiveButton(R.string.confirm) { _, _ ->
                viewModel.deleteDate(binding.txtActiveDate.text.toString())
                viewModel.changeActiveDateUser(user.uid, false)
                requireActivity().onBackPressed()
            }
            .setNegativeButton(R.string.cancel, null)
            .show()
    }

    private fun setupToolbar() {
        binding.toolbar.run {
            title = getString(R.string.user_info_title)
            setNavigationIcon(R.drawable.ic_baseline_arrow_back_24)
            setNavigationOnClickListener {
                requireActivity().onBackPressed()
            }
        }
    }

    private fun callPhone() {
        val phoneNumber = binding.txtPhone.text
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

    private fun sendEmail() {
        val email = binding.txtEmail.text
        val intent = Intent(
            Intent.ACTION_SENDTO,
            Uri.parse("mailto:$email")
        )
        try {
            startActivity(intent)
        } catch (e: ActivityNotFoundException) {
            e.message
        }
    }
}