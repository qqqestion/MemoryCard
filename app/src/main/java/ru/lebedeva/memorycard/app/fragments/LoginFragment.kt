package ru.lebedeva.memorycard.app.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.NavOptions
import androidx.navigation.Navigator
import androidx.navigation.fragment.findNavController
import ru.lebedeva.memorycard.app.BaseFragment
import ru.lebedeva.memorycard.app.MainActivity
import ru.lebedeva.memorycard.app.viewmodels.LoginViewModel
import ru.lebedeva.memorycard.databinding.FragmentLoginBinding
import ru.lebedeva.memorycard.domain.Resource

class LoginFragment : BaseFragment() {

    private var _binding: FragmentLoginBinding? = null

    private val binding get() = _binding!!

    private val viewModel: LoginViewModel by viewModels {
        (activity as MainActivity).viewModelProviderFactory
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.isLoggedIn()

        with(binding) {
            btnGoToRegistration.setOnClickListener {
                findNavController().navigate(
                    LoginFragmentDirections.actionLoginFragmentToRegisterFragment()
                )
            }
            btnLogin.setOnClickListener {
                viewModel.signIn(
                    etEmail.text.toString(),
                    etPassword.text.toString()
                )
            }
        }

        viewModel.isLoggedIn.observe(viewLifecycleOwner, {
            when (it) {
                is Resource.Success -> {
                    if (it.data!!) {
                        findNavController().navigate(
                            LoginFragmentDirections.actionLoginFragmentToListMemoryCardFragment()
                        )
                    }
                }
                is Resource.Error -> {
                    snackbar(it.msg.toString())
                }
                is Resource.Loading -> showLoadingBar()
            }
        })

        viewModel.signInStatus.observe(viewLifecycleOwner, {
            when (it) {
                is Resource.Success -> {
                    hideLoadingBar()
                    findNavController().navigate(
                        LoginFragmentDirections.actionLoginFragmentToListMemoryCardFragment()
                    )
                }
                is Resource.Error -> {
                    hideLoadingBar()
                    snackbar(it.msg.toString())
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