package ru.lebedeva.memorycard.app.fragments


import android.content.Context
import android.os.Bundle
import android.text.format.DateUtils
import android.view.*
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import coil.load
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import ru.lebedeva.memorycard.R
import ru.lebedeva.memorycard.app.BaseFragment
import ru.lebedeva.memorycard.app.MainActivity
import ru.lebedeva.memorycard.app.viewmodels.CreateMemoryCardViewModel
import ru.lebedeva.memorycard.app.viewmodels.DetailMemoryCardViewModel
import ru.lebedeva.memorycard.databinding.FragmentDetailMemoryCardBinding
import ru.lebedeva.memorycard.domain.Resource
import java.util.*

class DetailMemoryCardFragment : BaseFragment() {

    private var _binding: FragmentDetailMemoryCardBinding? = null

    private val binding get() = _binding!!

    private var map: GoogleMap? = null

    private val args by navArgs<DetailMemoryCardFragmentArgs>()
    private val memoryCardId by lazy { args.memoryCardId }

    private val viewModel: DetailMemoryCardViewModel by viewModels {
        (activity as MainActivity).viewModelProviderFactory
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDetailMemoryCardBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)
        launchMap(savedInstanceState)
        viewModel.getMemoryCardById(memoryCardId)
        viewModel.card.observe(viewLifecycleOwner, { result ->
            when (result) {
                is Resource.Success -> {
                    hideLoadingBar()
                    setMarkerOnMapAndFocus(
                        LatLng(
                            result.data!!.location!!.latitude,
                            result.data.location!!.longitude
                        )
                    )
                    binding.tvDescription.text = result.data.description
                    binding.tvTitle.text = result.data.title
                    binding.ivImage.load(result.data.imageUri)
                    val calendar = Calendar.getInstance()
                    calendar.time = result.data.date!!.toDate()
                    setDateTimeInTextView(calendar, binding.tvDate, requireContext())
                }
                is Resource.Error -> {
                    hideLoadingBar()
                    snackbar("Ошибка, попробуйте позже")
                }
                is Resource.Loading -> {
                    showLoadingBar()
                }
            }
        })
    }

    private fun setDateTimeInTextView(
        pickedDateTime: Calendar,
        view: TextView,
        context: Context
    ) {
        view.text = DateUtils.formatDateTime(
            context,
            pickedDateTime.timeInMillis,
            DateUtils.FORMAT_SHOW_DATE or DateUtils.FORMAT_SHOW_YEAR
                    or DateUtils.FORMAT_SHOW_TIME
        )

    }

    private fun launchMap(savedInstanceState: Bundle?) {
        binding.mapView.onCreate(savedInstanceState)
        binding.mapView.getMapAsync { googleMap ->
            googleMap.uiSettings.isMyLocationButtonEnabled = false
            map = googleMap
        }
    }

    private fun setMarkerOnMapAndFocus(location: LatLng) {
        map?.addMarker(
            MarkerOptions()
                .position(location)
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE))
                .title("Место воспоминания")
        )
        map?.moveCamera(
            CameraUpdateFactory.newLatLngZoom(
                location, 15F
            )
        )
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        (requireActivity() as AppCompatActivity).supportActionBar?.let {
            it.title = "Подробности карточки"
            it.setHomeButtonEnabled(true)
            it.setDisplayHomeAsUpEnabled(true)
        }
    }


    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.appbar_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                findNavController().popBackStack()
                true
            }
            else -> {
                super.onOptionsItemSelected(item)
            }
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


    override fun onResume() {
        super.onResume()
        binding.mapView.onResume()
    }

    override fun onStart() {
        super.onStart()
        binding.mapView.onStart()
    }

    override fun onStop() {
        super.onStop()
        binding.mapView.onStop()
    }

    override fun onPause() {
        super.onPause()
        binding.mapView.onPause()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        binding.mapView.onLowMemory()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        binding.mapView.onSaveInstanceState(outState)
    }

}