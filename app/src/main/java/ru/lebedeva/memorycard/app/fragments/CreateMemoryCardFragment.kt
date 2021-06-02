package ru.lebedeva.memorycard.app.fragments

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.location.LocationManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.provider.Settings
import android.text.format.DateUtils
import android.view.*
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.GeoPoint
import com.google.firebase.storage.FirebaseStorage
import pub.devrel.easypermissions.EasyPermissions
import ru.lebedeva.memorycard.R
import ru.lebedeva.memorycard.app.BaseFragment
import ru.lebedeva.memorycard.app.MainActivity
import ru.lebedeva.memorycard.app.viewmodels.CreateMemoryCardViewModel
import ru.lebedeva.memorycard.databinding.FragmentCreateMemoryCardBinding
import ru.lebedeva.memorycard.domain.MemoryCard
import ru.lebedeva.memorycard.domain.Resource
import timber.log.Timber
import java.util.*

class CreateMemoryCardFragment : BaseFragment(), EasyPermissions.PermissionCallbacks {

    private var _binding: FragmentCreateMemoryCardBinding? = null

    private val binding get() = _binding!!

    private var map: GoogleMap? = null
    private lateinit var manager: LocationManager

    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    private var marker: Marker? = null
    private val PICK_IMAGE_REQUEST = 111

    private val TAKE_IMAGE_REQUEST = 222

    private var needDatetime = Calendar.getInstance()
    private var isEnterDatetime = false
    private var location: GeoPoint? = null

    private var filePath: Uri? = null

    private var bitmap: Bitmap? = null

    private val viewModel: CreateMemoryCardViewModel by viewModels {
        (activity as MainActivity).viewModelProviderFactory
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCreateMemoryCardBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        requestPermission()
        manager = requireActivity().getSystemService(Context.LOCATION_SERVICE) as LocationManager
        fusedLocationProviderClient =
            LocationServices.getFusedLocationProviderClient(requireContext())
        launchMap(savedInstanceState)
        setHasOptionsMenu(true)
        binding.ivImage.setOnClickListener {
//            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI)
//            intent.action = Intent.ACTION_GET_CONTENT
//            startActivityForResult(
//                intent,
//                PICK_IMAGE_REQUEST
//            )
            val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            try {
                startActivityForResult(takePictureIntent, TAKE_IMAGE_REQUEST)
            } catch (e: ActivityNotFoundException) {
                Timber.d(e)
            }
        }
        binding.tvDate.setOnClickListener {
            pickDateTime()
        }
        viewModel.uploadCardStatus.observe(viewLifecycleOwner, { result ->
            when (result) {
                is Resource.Success -> {
                    hideLoadingBar()
                    findNavController().popBackStack()
                }
                is Resource.Loading -> {
                    showLoadingBar()
                }
                is Resource.Error -> {
                    hideLoadingBar()
                    snackbar("Ошибка создания карточки(")
                }
            }
        })
    }

    private fun launchMap(savedInstanceState: Bundle?) {
        binding.mapView.onCreate(savedInstanceState)
        binding.mapView.getMapAsync { googleMap ->
            googleMap.uiSettings.isMyLocationButtonEnabled = true
            googleMap.setOnMapLongClickListener {
                setMarkerOnMap(LatLng(it.latitude, it.longitude))
                location = GeoPoint(it.latitude, it.longitude)
            }
            map = googleMap
            mapUISettings()
            moveCameraToUserLocation()
            if (ActivityCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                requestPermission()
            } else {
                map?.isMyLocationEnabled = true
            }
        }
    }

    private fun mapUISettings() {
        map?.uiSettings?.isCompassEnabled = false
        map?.uiSettings?.isMyLocationButtonEnabled = true
    }

    @SuppressLint("MissingPermission")
    private fun moveCameraToUserLocation() {
        if (manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            fusedLocationProviderClient.lastLocation.addOnSuccessListener {
                it?.let {
                    map?.moveCamera(
                        CameraUpdateFactory.newLatLngZoom(
                            LatLng(it.latitude, it.longitude), 15F
                        )
                    )
                }

            }
        } else {
            buildAlertMessageNoLocationService()
        }

    }


    private fun setMarkerOnMap(location: LatLng) {
        marker?.remove()
        val buffMarker: Marker? = map?.addMarker(
            MarkerOptions()
                .position(LatLng(location.latitude, location.longitude))
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE))
                .title("Место воспоминания")
        )
        buffMarker?.let {
            it.tag = location
            marker = it
        }
    }

    private fun pickDateTime() {
        val currentDateTime = Calendar.getInstance()
        val startYear = currentDateTime.get(Calendar.YEAR)
        val startMonth = currentDateTime.get(Calendar.MONTH)
        val startDay = currentDateTime.get(Calendar.DAY_OF_MONTH)
        val startHour = currentDateTime.get(Calendar.HOUR_OF_DAY)
        val startMinute = currentDateTime.get(Calendar.MINUTE)

        DatePickerDialog(requireContext(), { _, year, month, day ->
            TimePickerDialog(requireContext(), { _, hour, minute ->
                val pickedDateTime = Calendar.getInstance()
                pickedDateTime.set(year, month, day, hour, minute)
                needDatetime.set(year, month, day, hour, minute)
                setDateTimeInTextView(pickedDateTime)
                isEnterDatetime = true
            }, startHour, startMinute, true).show()
        }, startYear, startMonth, startDay).show()
    }

    private fun setDateTimeInTextView(pickedDateTime: Calendar) {
        val date = DateUtils.formatDateTime(
            requireContext(),
            pickedDateTime.timeInMillis,
            DateUtils.FORMAT_SHOW_DATE or DateUtils.FORMAT_SHOW_YEAR
                    or DateUtils.FORMAT_SHOW_TIME
        )
        binding.tvDate.setText(date, TextView.BufferType.EDITABLE)

    }

    private fun buildAlertMessageNoLocationService() {
        val builder: AlertDialog.Builder = AlertDialog.Builder(requireContext())
        builder.setCancelable(false)
            .setMessage("Включите GPS для отслеживания вашего местоположения")
            .setPositiveButton(
                "Включить"
            ) { _, _ -> startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)) }
        val alert: AlertDialog = builder.create()
        alert.show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK) {
            val imageUri = data?.data ?: return
            filePath = imageUri
            binding.ivImage.setImageURI(imageUri)
            binding.ivImage.scaleType = ImageView.ScaleType.FIT_XY
        } else if (requestCode == TAKE_IMAGE_REQUEST && resultCode == Activity.RESULT_OK) {
            Timber.d("Image taken successfully!")
            bitmap = data?.extras?.get("data") as? Bitmap
            binding.ivImage.setImageBitmap(bitmap)
        }
    }

    private fun requestPermission() {
        if (EasyPermissions.hasPermissions(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        ) {
            return
        }
        EasyPermissions.requestPermissions(
            this,
            "Приложение не будет правильно работать, если вы не разрешите отслеживание локации!",
            0,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION
        )
    }

    override fun onPermissionsGranted(requestCode: Int, perms: MutableList<String>) {
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            Snackbar.make(requireView(), "Отслеживание включено", Snackbar.LENGTH_SHORT).show()
        } else {
            requestPermission()
        }
    }

    override fun onPermissionsDenied(requestCode: Int, perms: MutableList<String>) {
        Snackbar.make(requireView(), "Ошибка получения нужных разрешений", Snackbar.LENGTH_SHORT)
            .show()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        binding.mapView.onSaveInstanceState(outState)
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        (requireActivity() as AppCompatActivity).supportActionBar?.let {
            it.title = "Добавление карточки"
            it.setHomeButtonEnabled(true)
            it.setDisplayHomeAsUpEnabled(true)
        }
        menu.findItem(R.id.action_save_card).isVisible = true
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
            R.id.action_save_card -> {
                createCard()?.let { card ->
                    viewModel.createMemoryCard(card, bitmap)
                }
                true
            }
            else -> {
                super.onOptionsItemSelected(item)
            }
        }
    }

    private fun createCard(): MemoryCard? {
        if (location == null) {
            Snackbar.make(requireView(), "Выберите локации на карте!", Snackbar.LENGTH_SHORT).show()
            return null
        }
        if (filePath == null && bitmap == null) {
            Snackbar.make(requireView(), "Добавьте фото!", Snackbar.LENGTH_SHORT).show()
            return null
        }
        if (binding.etTitle.text.toString() == "") {
            Snackbar.make(requireView(), "Введите название!", Snackbar.LENGTH_SHORT).show()
            return null
        }

        if (!isEnterDatetime) {
            Snackbar.make(requireView(), "Введите дату!", Snackbar.LENGTH_SHORT).show()
            return null
        }
        return MemoryCard(
            null,
            FirebaseAuth.getInstance().uid,
            location,
            binding.etTitle.text.toString(),
            Timestamp(needDatetime.time),
            binding.etDescription.text.toString(),
            filePath?.toString()
        )
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
}