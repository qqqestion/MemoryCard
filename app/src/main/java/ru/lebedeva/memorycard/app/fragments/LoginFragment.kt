package ru.lebedeva.memorycard.app.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import ru.lebedeva.memorycard.app.BaseFragment
import ru.lebedeva.memorycard.ui.MainActivity
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
        viewModel.signUp()

        viewModel.signUpStatus.observe(viewLifecycleOwner, {
            when (it) {
                is Resource.Success -> {
                    snackbar("Регистрация прошла успешно")
                }
                is Resource.Error -> {
                    snackbar(it.msg.toString())
                }
                Resource.Loading -> Unit
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}