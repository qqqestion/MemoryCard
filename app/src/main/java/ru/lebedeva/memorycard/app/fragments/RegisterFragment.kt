package ru.lebedeva.memorycard.app.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import ru.lebedeva.memorycard.app.BaseFragment
import ru.lebedeva.memorycard.app.MainActivity
import ru.lebedeva.memorycard.app.viewmodels.RegisterViewModel
import ru.lebedeva.memorycard.databinding.FragmentRegisterBinding
import ru.lebedeva.memorycard.domain.Resource

class RegisterFragment : BaseFragment() {

    private var _binding: FragmentRegisterBinding? = null

    private val binding get() = _binding!!

    private val viewModel: RegisterViewModel by viewModels {
        (activity as MainActivity).viewModelProviderFactory
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRegisterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        with(binding) {
            btnRegister.setOnClickListener {
                viewModel.signUp(
                    etEmail.text.toString(),
                    etPassword.text.toString(),
                    etConfirmPassword.text.toString()
                )
            }
        }

        viewModel.signUpStatus.observe(viewLifecycleOwner, {
            when (it) {
                is Resource.Success -> {
                    hideLoadingBar()
                    findNavController().navigate(
                        RegisterFragmentDirections.actionRegisterFragmentToListMemoryCardFragment()
                    )
                }
                is Resource.Error -> {
                    hideLoadingBar()
                    snackbar(it.msg!!)
                }
                is Resource.Loading -> showLoadingBar()
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}