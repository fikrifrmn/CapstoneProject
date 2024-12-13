package com.example.driverattentiveness.ui.maps

import android.Manifest
import android.app.Activity.RESULT_CANCELED
import android.app.Activity.RESULT_OK
import android.content.ContentValues.TAG
import android.content.IntentSender
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Location
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import android.location.Geocoder
import androidx.activity.result.IntentSenderRequest
import java.util.Locale
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.driverattentiveness.R
import com.example.driverattentiveness.data.UserRepository
import com.example.driverattentiveness.data.api.retrofit.ApiConfig
import com.example.driverattentiveness.data.database.DatabaseHelper
import com.example.driverattentiveness.data.pref.UserPreference
import com.example.driverattentiveness.data.pref.dataStore
import com.example.driverattentiveness.databinding.FragmentMapsBinding
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PolylineOptions
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.math.roundToInt

class MapsFragment : Fragment(), OnMapReadyCallback {

    private var _binding: FragmentMapsBinding? = null
    private val binding get() = _binding!!

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationCallback: LocationCallback
    private lateinit var locationRequest: LocationRequest
    private var googleMap: GoogleMap? = null
    private var isTracking = false
    private var totalDistance = 0.0
    private var lastLocation: Location? = null
    private var allLatLng = ArrayList<LatLng>()
    private var boundsBuilder = LatLngBounds.Builder()
    private lateinit var userPreference: UserPreference
    private var tripStartTime: String? = null
    private lateinit var userRepository: UserRepository
    private val TAG = MapsFragment::class.java.simpleName


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMapsBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        createLocationCallback()
        createLocationRequest()

        // Menyimpan userPreference dan userRepository
        userPreference = UserPreference.getInstance(requireContext().dataStore)
        userRepository = UserRepository.getInstance(userPreference, ApiConfig.getApiService(""))

        val mapFragment =
            childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())

        locationRequest = LocationRequest.create().apply {
            interval = 5000
            fastestInterval = 2000
            priority = Priority.PRIORITY_HIGH_ACCURACY
        }

        setupLocationCallback()

        binding.btnStart.setOnClickListener {
            Log.d(TAG, "Button clicked. Current tracking state: $isTracking")
            if (!isTracking) {
                clearMaps()
                updateTrackingStatus(true)
                startLocationUpdates()
                startTracking()
            } else {
                updateTrackingStatus(false)
                stopLocationUpdates()
                stopTracking()  // Logika berhenti tracking
            }
        }
    }

    private fun startTracking() {
        if (checkPermission(Manifest.permission.ACCESS_FINE_LOCATION)) {
            isTracking = true
            totalDistance = 0.0
            lastLocation = null

            tripStartTime = getCurrentTimeFormatted()

            // Periksa tripId di dalam coroutine
            lifecycleScope.launch {
                val user = userRepository.getSession().first()
                if (user.tripId.isBlank()) {
                    createTrip() // Buat trip jika belum ada
                } else {
                    fusedLocationClient.requestLocationUpdates(
                        locationRequest,
                        locationCallback,
                        Looper.getMainLooper()
                    )
                    Toast.makeText(requireContext(), "Tracking started", Toast.LENGTH_SHORT).show()
                }
            }
        } else {
            requestLocationPermission()
        }
    }

    private fun createTrip() {
        lifecycleScope.launch {
            try {
                val user = userRepository.getSession().first()
                val apiService = ApiConfig.getApiService(user.token)

                val startLocationName = getLocationNameFromLatLng(allLatLng.firstOrNull() ?: LatLng(0.0, 0.0))
                val startTime = tripStartTime ?: getCurrentTimeFormatted()
                Log.d(TAG, "Creating trip: startLocation=$startLocationName, startTime=$startTime, userId=${user.id}, tripId=${user.tripId}")

                val response = apiService.createTrip(
                    startLocation = startLocationName ?: "Unknown",
                    startTime = startTime,
                    userId = user.id.toInt(),
                    id = user.tripId.toInt()
                )

                if (response.isSuccessful) {
                    val tripData = response.body()?.data
                    val tripId = tripData?.id?.toString() ?: return@launch
                    userRepository.saveTripId(tripId)
                    userRepository.saveSession(user.copy(tripId = tripId))
                    Log.d(TAG, "Trip created successfully. ID: $tripId")
                } else {
                    Log.e(TAG, "Failed to create trip, error: ${response.errorBody()}")
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error creating trip", e)
            }
        }
    }

    private fun stopTracking() {
        if (!isTracking) return

        isTracking = false
        fusedLocationClient.removeLocationUpdates(locationCallback)

        val distanceInKm = (totalDistance / 1000).roundToInt()
        Toast.makeText(
            requireContext(),
            "Tracking stopped. Total distance: $distanceInKm km",
            Toast.LENGTH_LONG
        ).show()
        Log.d(TAG, "distance: $distanceInKm")

        lastLocation?.let {
            val endTime = getCurrentTimeFormatted()
            Log.d(TAG, "End Time: $endTime")
            sendLocationToServer(it, isTripEnd = true, endTime = endTime)

            // Save total distance to database
            lifecycleScope.launch {
                val user = userRepository.getSession().first()
                val dbHelper = DatabaseHelper(requireContext())
                dbHelper.saveTotalDistance(user.id, totalDistance)  // Save based on user ID
            }
        }
    }



    private fun sendLocationToServer(location: Location, isTripEnd: Boolean, endTime: String? = null) {
        lifecycleScope.launch {
            try {
                // Ambil data pengguna dari sesi
                val user = userRepository.getSession().first()
                val apiService = ApiConfig.getApiService(user.token) // Gunakan token dari sesi

                val startTime = tripStartTime ?: getCurrentTimeFormatted()  // Gunakan waktu mulai yang sesuai
                val startLocationName = allLatLng.firstOrNull()?.let { getLocationNameFromLatLng(it) } ?: ""
                val endLocationName = getLocationNameFromLatLng(LatLng(location.latitude, location.longitude)) ?: "Unknown Location"
                val endTime = getCurrentTimeFormatted()

                // Gunakan user.id yang sudah diambil dari sesi untuk pengiriman
                val userId = if (user.id.isNotBlank()) user.id.toInt() else -1  // Pastikan id tidak kosong

                // Gunakan trip ID dari sesi atau default value jika kosong
                val tripId = if (user.tripId.isNotBlank()) user.tripId.toInt() else -1  // Default value jika kosong
                Log.d(TAG, "Sending location to server: tripId=$tripId, userId=$userId, endLocation=$endLocationName, endTime=$endTime")

                // Cek apakah trip sudah selesai atau masih berjalan
                if (isTripEnd) {
                    // Panggil API untuk update trip (mengirimkan end location dan end time)

                    val response = apiService.updateTrip(
                        id = tripId,
                        userId = userId,
                        endLocation = endLocationName,
                        endTime = endTime ?: getCurrentTimeFormatted() // Pastikan ada end time
                    )

                    if (response.isSuccessful) {
                        Log.d(TAG, "Trip updated successfully.")
                    } else {
                        Log.e(TAG, "Failed to update trip, error: ${response.errorBody()}")
                    }
                } else {
                    // Panggil API untuk membuat trip pertama kali
                    val response = apiService.createTrip(
                        startLocation = startLocationName,
                        startTime = startTime,
                        userId = userId,
                        id = tripId
                    )

                    if (response.isSuccessful) {
                        val tripData = response.body()?.data
                        val tripId = tripData?.id?.toString() // Dapatkan trip ID dari response

                        tripId?.let {
                            userRepository.saveTripId(it)
                            val updatedUserModel = user.copy(tripId = it)
                            userRepository.saveSession(updatedUserModel)
                        }
                    } else {
                        Log.e(TAG, "Failed to create trip, error: ${response.errorBody()}")
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error generating trip ID", e)
            }
        }
    }




    private fun getLocationNameFromLatLng(latLng: LatLng): String? {
        val geocoder = Geocoder(requireContext(), Locale.getDefault())
        val addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1)
        return if (addresses != null && addresses.isNotEmpty()) {
            val locality = addresses[0].locality ?: addresses[0].subAdminArea ?: addresses[0].adminArea
            locality ?: "Unknown Location"
        } else {
            "Unknown Location"
        }
    }

    private fun getCurrentTimeFormatted(): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
        return sdf.format(Date())
    }

    private fun clearMaps() {
        googleMap?.clear()
        allLatLng.clear()
        boundsBuilder = LatLngBounds.Builder()
    }

    private fun updateTrackingStatus(newStatus: Boolean) {
        isTracking = newStatus
        binding.btnStart.text = if (isTracking) getString(R.string.stop_running) else getString(R.string.start_driving)
    }

    private fun startLocationUpdates() {
        try {
            fusedLocationClient.requestLocationUpdates(
                locationRequest,
                locationCallback,
                Looper.getMainLooper()
            )
        } catch (exception: SecurityException) {
            Log.e(TAG, "Error: ${exception.message}")
        }
    }

    private fun stopLocationUpdates() {
        fusedLocationClient.removeLocationUpdates(locationCallback)
    }

    override fun onResume() {
        super.onResume()
        if (isTracking) {
            startLocationUpdates()
        }
    }

    override fun onPause() {
        super.onPause()
        stopLocationUpdates()
    }

    private fun setupLocationCallback() {
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                super.onLocationResult(locationResult)
                if (isTracking) {
                    for (location in locationResult.locations) {
                        updateLocation(location)
                    }
                }
            }
        }
    }

    private fun updateLocation(location: Location) {
        lastLocation?.let {
            totalDistance += it.distanceTo(location)
        }
        lastLocation = location


        val latLng = LatLng(location.latitude, location.longitude)
        allLatLng.add(latLng)
        boundsBuilder.include(latLng)
        googleMap?.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15f))


        Log.d(TAG, "Total distance: ${totalDistance / 1000} km")
        sendLocationToServer(location, false)
    }

    private suspend fun getUserToken(): String {
        val user = userPreference.getSession().first()
        return user.token
    }

    override fun onMapReady(map: GoogleMap) {
        googleMap = map

        if (checkPermission(Manifest.permission.ACCESS_FINE_LOCATION) &&
            checkPermission(Manifest.permission.ACCESS_COARSE_LOCATION)) {
            googleMap?.isMyLocationEnabled = true
        }

        getMyLastLocation()
    }

    private fun getMyLastLocation() {
        if (checkPermission(Manifest.permission.ACCESS_FINE_LOCATION)) {
            fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
                if (location != null) {
                    showStartMarker(location)
                } else {
                    Toast.makeText(requireContext(), "Location not found. Try again.", Toast.LENGTH_SHORT).show()
                }
            }
        } else {
            requestLocationPermission()
        }
    }

    private val resolutionLauncher =
        registerForActivityResult(ActivityResultContracts.StartIntentSenderForResult()) { result ->
            when (result.resultCode) {
                RESULT_OK -> Log.i(TAG, "onActivityResult: All location settings are satisfied.")
                RESULT_CANCELED -> {
                    Toast.makeText(
                        requireContext(),
                        "Anda harus mengaktifkan GPS untuk menggunakan aplikasi ini!",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }

    private fun createLocationRequest() {
        val priority = Priority.PRIORITY_HIGH_ACCURACY
        val interval = TimeUnit.SECONDS.toMillis(1)
        val maxWaitTime = TimeUnit.SECONDS.toMillis(1)
        locationRequest = LocationRequest.Builder(priority, interval).apply {
            setMaxUpdateDelayMillis(maxWaitTime)
        }.build()
        val builder = LocationSettingsRequest.Builder()
            .addLocationRequest(locationRequest)
        val client = LocationServices.getSettingsClient(requireContext())
        client.checkLocationSettings(builder.build())
            .addOnSuccessListener {
                getMyLastLocation()
            }
            .addOnFailureListener { exception ->
                if (exception is ResolvableApiException) {
                    try {
                        resolutionLauncher.launch(
                            IntentSenderRequest.Builder(exception.resolution).build()
                        )
                    } catch (sendEx: IntentSender.SendIntentException) {
                        Toast.makeText(requireContext(), sendEx.message, Toast.LENGTH_SHORT).show()
                    }
                }
            }
    }

    private fun createLocationCallback() {
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                for (location in locationResult.locations) {
                    Log.d(TAG, "onLocationResult: " + location.latitude + ", " + location.longitude)
                    val lastLatLng = LatLng(location.latitude, location.longitude)

                    // Draw polyline
                    allLatLng.add(lastLatLng)
                    googleMap?.addPolyline(
                        PolylineOptions()
                            .color(Color.CYAN)
                            .width(10f)
                            .addAll(allLatLng)
                    )

                    // Adjust camera according to the route taken
                    boundsBuilder.include(lastLatLng)
                    val bounds: LatLngBounds = boundsBuilder.build()
                    googleMap?.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 64))
                }
            }
        }
    }

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            when {
                permissions[Manifest.permission.ACCESS_FINE_LOCATION] ?: false -> {
                    // Precise location access granted.
                    getMyLastLocation()
                }

                permissions[Manifest.permission.ACCESS_COARSE_LOCATION] ?: false -> {
                    // Only approximate location access granted.
                    getMyLastLocation()
                }

                else -> {
                    // No location access granted.
                }
            }
        }

    private fun showStartMarker(location: Location) {
        val startLocation = LatLng(location.latitude, location.longitude)
        googleMap?.addMarker(
            MarkerOptions()
                .position(startLocation)
                .title(getString(R.string.start_point))
        )
        googleMap?.moveCamera(CameraUpdateFactory.newLatLngZoom(startLocation, 17f))
    }

    private fun checkPermission(permission: String): Boolean {
        return ContextCompat.checkSelfPermission(requireContext(), permission) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestLocationPermission() {
        requestPermissionLauncher.launch(
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1
    }
}