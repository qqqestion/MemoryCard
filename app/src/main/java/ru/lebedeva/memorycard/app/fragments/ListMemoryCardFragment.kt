package ru.lebedeva.memorycard.app.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import ru.lebedeva.memorycard.app.BaseFragment
import ru.lebedeva.memorycard.app.MainActivity
import ru.lebedeva.memorycard.app.adapters.MemoryCardsAdapter
import ru.lebedeva.memorycard.app.viewmodels.ListMemoryCardViewModel
import ru.lebedeva.memorycard.app.viewmodels.LoginViewModel
import ru.lebedeva.memorycard.databinding.FragmentListMemoryCardBinding
import ru.lebedeva.memorycard.databinding.FragmentLoginBinding
import ru.lebedeva.memorycard.domain.Resource

class ListMemoryCardFragment : BaseFragment() {

    private var _binding: FragmentListMemoryCardBinding? = null

    private val binding get() = _binding!!

    lateinit var cardAdapter: MemoryCardsAdapter

    private val viewModel: ListMemoryCardViewModel by viewModels {
        (activity as MainActivity).viewModelProviderFactory
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentListMemoryCardBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.getUserCards()
        cardAdapter = MemoryCardsAdapter()

        with(binding) {
            recyclerView.adapter = cardAdapter
            recyclerView.layoutManager = LinearLayoutManager(requireContext())
        }

        viewModel.cards.observe(viewLifecycleOwner, {
            when (it) {
                is Resource.Error -> {
                    hideLoadingBar()
                    snackbar(it.msg.toString())
                }
                is Resource.Loading -> {
                    showLoadingBar()
                }
                is Resource.Success -> {
                    hideLoadingBar()
                    cardAdapter.cards = it.data!!
                    binding.tvEmptyMemoryListLabel.isVisible = cardAdapter.cards.isEmpty()
                }
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}