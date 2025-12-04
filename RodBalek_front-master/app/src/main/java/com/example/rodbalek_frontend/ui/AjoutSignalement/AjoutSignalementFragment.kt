package com.example.rodbalek_frontend.ui.AjoutSignalement

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.rodbalek_frontend.api.RetrofitClient
import com.example.rodbalek_frontend.data.repository.AjoutSignalementRepository
import com.example.rodbalek_frontend.databinding.FragmentAjoutSignalementBinding
import com.example.rodbalek_frontend.viewmodel.AjoutSignalementViewModel
import com.example.rodbalek_frontend.viewmodel.AjoutSignalementViewModelFactory
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.overlay.Marker
import java.io.File

class AjoutSignalementFragment : Fragment() {

    private var _binding: FragmentAjoutSignalementBinding? = null
    private val binding get() = _binding!!

    private lateinit var marker: Marker
    private lateinit var locationHelper: LocationHelper
    private lateinit var photoHelper: PhotoHelper
    private var selectedImageFile: File? = null

    private var latitude: Double? = null
    private var longitude: Double? = null

    private val viewModel: AjoutSignalementViewModel by viewModels {
        val apiService = RetrofitClient.getInstance(requireContext())
        val repository = AjoutSignalementRepository(apiService)
        AjoutSignalementViewModelFactory(repository)
    }

    companion object {
        const val REQUEST_LOCATION_PERMISSION = 200
        const val REQUEST_CAMERA_PERMISSION = 300
    }

    private val cameraLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val photo = photoHelper.savePhoto(result.data)

                if (photo != null) {
                    binding.imageView2.setImageBitmap(photo)
                    selectedImageFile = photoHelper.photoFile
                }
            }
        }

    private val imagePicker =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            if (uri != null) {

                binding.imageView2.setImageURI(uri)

                val inputStream = requireContext().contentResolver.openInputStream(uri)
                val tempFile = File(requireContext().cacheDir, "image_galerie_${System.currentTimeMillis()}.jpg")

                tempFile.outputStream().use { output ->
                    inputStream?.copyTo(output)
                }

                selectedImageFile = tempFile
            }
        }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAjoutSignalementBinding.inflate(inflater, container, false)

        setupObservers()
        setupMap()

        marker = Marker(binding.map)
        val startPoint = GeoPoint(33.8869, 9.5375)
        marker.position = startPoint
        marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
        binding.map.overlays.add(marker)
        binding.map.controller.setCenter(startPoint)
        binding.map.controller.setZoom(7.0)

        latitude = startPoint.latitude
        longitude = startPoint.longitude

        locationHelper = LocationHelper(requireContext(), binding.map, marker)
        photoHelper = PhotoHelper(requireContext())

        checkLocationPermission()
        setupListeners()

        return binding.root
    }

    private fun setupObservers() {
        viewModel.categories.observe(viewLifecycleOwner) { categories ->
             val adapter = ArrayAdapter(
                requireContext(),
                android.R.layout.simple_spinner_item,
                categories
            )
            binding.spinnerCategorie.setAdapter(adapter)
        }
// traitment fil cas de success
        viewModel.sendResult.observe(viewLifecycleOwner) { result ->
             result.onSuccess {
                 Toast.makeText(requireContext(), "Signalement envoyé", Toast.LENGTH_SHORT).show()
                 binding.txtDescription.text?.clear()
                 binding.spinnerCategorie.setText("", false)
                 binding.imageView2.setImageURI(null)
                 selectedImageFile = null
// traitment fil cas d'echec


             }.onFailure { t ->


                  Log.e("API_ERROR", "Error: ${t.message}")
                  Toast.makeText(requireContext(), "Erreur lors de l'envoi", Toast.LENGTH_SHORT).show()
             }
        }

        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
             // Show/Hide loading indicator
        }

        viewModel.error.observe(viewLifecycleOwner) { errorMessage ->
            // Handle other errors
             Log.e("API_ERROR", errorMessage)
        }
    }

    private fun setupMap() {
        Configuration.getInstance()
            .load(requireContext(), requireActivity().getSharedPreferences("osmdroid", 0))
        binding.map.setTileSource(TileSourceFactory.MAPNIK)
        binding.map.setMultiTouchControls(true)
    }

    private fun setupListeners() {
        binding.map.setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_UP) {
                val geoPoint =
                    binding.map.projection.fromPixels(event.x.toInt(), event.y.toInt()) as GeoPoint
                marker.position = geoPoint
                latitude = geoPoint.latitude
                longitude = geoPoint.longitude
            }
            false
        }

        binding.btnPhoto.setOnClickListener {
            checkCameraPermissionAndOpen()
        }

        binding.btnMyLocation.setOnClickListener {
            checkLocationPermission()
        }
        binding.btnGalerie.setOnClickListener {
            openGallery()
        }

        binding.btnEnvoyer.setOnClickListener {
            envoyer()
        }
    }

    private fun checkLocationPermission() {
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                REQUEST_LOCATION_PERMISSION
            )
        } else {
            checkIfGpsEnabled()
        }
    }

    private fun checkIfGpsEnabled() {
        val locationManager =
            requireContext().getSystemService(Context.LOCATION_SERVICE) as android.location.LocationManager
        val gpsEnabled =
            locationManager.isProviderEnabled(android.location.LocationManager.GPS_PROVIDER)

        if (!gpsEnabled) {
            Toast.makeText(requireContext(), "Activez la localisation", Toast.LENGTH_SHORT).show()
            startActivity(Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS))
        } else {
            locationHelper.enableLocation { lat, lon ->
                latitude = lat
                longitude = lon
            }
        }
    }

    private fun openGallery() {
        imagePicker.launch("image/*")
    }

    private fun checkCameraPermissionAndOpen() {
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA)
            != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.CAMERA),
                REQUEST_CAMERA_PERMISSION
            )
        } else {
            photoHelper.openCamera(cameraLauncher)
        }
    }

    private fun envoyer() {
        val categorie = binding.spinnerCategorie.text.toString()
        val description = binding.txtDescription.text.toString()

        if (selectedImageFile == null) {
            Toast.makeText(requireContext(), "⚠️ Prenez une photo ou choisissez-en une", Toast.LENGTH_SHORT).show()
            return
        }

        val file = selectedImageFile!!
        val requestFile = file.asRequestBody("image/*".toMediaTypeOrNull())

        val photoPart = MultipartBody.Part.createFormData(
            "Photo",
            file.name,
            requestFile
        )

        val descriptionPart = description.toRequestBody("text/plain".toMediaTypeOrNull())
        val categoriePart = categorie.toRequestBody("text/plain".toMediaTypeOrNull())
        val latPart = latitude.toString().toRequestBody("text/plain".toMediaTypeOrNull())
        val lonPart = longitude.toString().toRequestBody("text/plain".toMediaTypeOrNull())

        viewModel.envoyerSignalement(descriptionPart, categoriePart, latPart, lonPart, photoPart)
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            REQUEST_LOCATION_PERMISSION -> if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                checkLocationPermission()
            }

            REQUEST_CAMERA_PERMISSION -> if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                photoHelper.openCamera(cameraLauncher)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}