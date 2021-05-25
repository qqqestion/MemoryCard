package ru.lebedeva.memorycard.app.fragments

import android.os.Bundle
import android.view.*
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import ru.lebedeva.memorycard.R
import ru.lebedeva.memorycard.app.BaseFragment
import ru.lebedeva.memorycard.app.MainActivity
import ru.lebedeva.memorycard.app.adapters.MemoryCardsAdapter
import ru.lebedeva.memorycard.app.viewmodels.ListMemoryCardViewModel
import ru.lebedeva.memorycard.data.LocationMapper
import ru.lebedeva.memorycard.databinding.FragmentListMemoryCardBinding
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

        setHasOptionsMenu(true)
        viewModel.getUserCards()

        cardAdapter = MemoryCardsAdapter(LocationMapper(requireContext()))

        cardAdapter.setOnItemClickListener { memoryCard ->
            findNavController().navigate(
                ListMemoryCardFragmentDirections.actionListMemoryCardFragmentToDetailMemoryCardFragment(
                    memoryCard.id!!
                )
            )
        }

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
        binding.fab.setOnClickListener {
            findNavController().navigate(R.id.action_listMemoryCardFragment_to_createMemoryCardFragment)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        (requireActivity() as AppCompatActivity).supportActionBar?.let {
            it.title = "Список карточек"
            it.setHomeButtonEnabled(false)
            it.setDisplayHomeAsUpEnabled(false)
        }
        menu.findItem(R.id.action_logout).isVisible = true
    }


    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.appbar_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_logout -> {
                viewModel.signOut()
                findNavController().navigate(
                    ListMemoryCardFragmentDirections.actionListMemoryCardFragmentToLoginFragment()
                )
                return true
            }
            else -> {
                super.onOptionsItemSelected(item)
            }
        }
    }

}