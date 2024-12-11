//package com.example.driverattentiveness.ui.maps
//
//import android.Manifest
//import android.app.Activity.RESULT_CANCELED
//import android.app.Activity.RESULT_OK
//import android.content.IntentSender
//import android.content.pm.PackageManager
//import android.graphics.Color
//import android.location.Location
//import androidx.fragment.app.Fragment
//import android.os.Bundle
//import android.os.Looper
//import android.util.Log
//import android.view.View
//import android.widget.Toast
//import androidx.activity.result.IntentSenderRequest
//import androidx.activity.result.contract.ActivityResultContracts
//import androidx.core.content.ContextCompat
//import androidx.lifecycle.lifecycleScope
//import com.example.driverattentiveness.R
//import com.example.driverattentiveness.data.UserRepository
//import com.example.driverattentiveness.data.api.retrofit.LocationData
//import com.example.driverattentiveness.data.api.retrofit.LocationRequestData
//import com.example.driverattentiveness.databinding.FragmentMapsBinding
//import com.google.android.gms.common.api.ResolvableApiException
//import com.google.android.gms.location.FusedLocationProviderClient
//import com.google.android.gms.location.LocationCallback
//import com.google.android.gms.location.LocationRequest
//import com.google.android.gms.location.LocationResult
//import com.google.android.gms.location.LocationServices
//import com.google.android.gms.location.LocationSettingsRequest
//import com.google.android.gms.location.Priority
//import com.google.android.gms.maps.CameraUpdateFactory
//import com.google.android.gms.maps.GoogleMap
//import com.google.android.gms.maps.OnMapReadyCallback
//import com.google.android.gms.maps.SupportMapFragment
//import com.google.android.gms.maps.model.LatLng
//import com.google.android.gms.maps.model.LatLngBounds
//import com.google.android.gms.maps.model.MarkerOptions
//import com.google.android.gms.maps.model.PolylineOptions
//import kotlinx.coroutines.flow.first
//import kotlinx.coroutines.launch
//import java.util.concurrent.TimeUnit
//
//class MapsFragment : Fragment(R.layout.fragment_maps), OnMapReadyCallback {
//
//    private lateinit var mMap: GoogleMap
//    private lateinit var binding: FragmentMapsBinding
//    private lateinit var fusedLocationClient: FusedLocationProviderClient
//    private lateinit var locationRequest: LocationRequest
//    private var isTracking = false
//    private lateinit var locationCallback: LocationCallback
//    private var allLatLng = ArrayList<LatLng>()
//    private var boundsBuilder = LatLngBounds.Builder()
//
//    private lateinit var userRepository: UserRepository
//
//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
//        binding = FragmentMapsBinding.bind(view)
//
//        createLocationRequest()
//
//        // Mendapatkan map dan memberi notifikasi ketika map siap digunakan
//        val mapFragment = childFragmentManager
//            .findFragmentById(R.id.map) as SupportMapFragment
//        mapFragment.getMapAsync(this)
//
//        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
//
//        binding.btnStart.setOnClickListener {
//            if (!isTracking) {
//                clearMaps()
//                updateTrackingStatus(true)
//                startLocationUpdates()
//            } else {
//                updateTrackingStatus(false)
//                stopLocationUpdates()
//            }
//        }
//    }
//
//    private val requestPermissionLauncher =
//        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
//            when {
//                permissions[Manifest.permission.ACCESS_FINE_LOCATION] ?: false -> {
//                    // Lokasi tepat berhasil diberikan akses
//                    getMyLastLocation()
//                }
//
//                permissions[Manifest.permission.ACCESS_COARSE_LOCATION] ?: false -> {
//                    // Lokasi kasar diberikan akses
//                    getMyLastLocation()
//                }
//
//                else -> {
//                    // Tidak ada akses lokasi yang diberikan
//                }
//            }
//        }
//
//    private fun checkPermission(permission: String): Boolean {
//        return ContextCompat.checkSelfPermission(requireContext(), permission) == PackageManager.PERMISSION_GRANTED
//    }
//
//    private fun sendLocationToApi() {
//        lifecycleScope.launch {
//            try {
//                // Get the current user session
//                val userSession = userRepository.getSession().first()
//                val userId = userSession.id // Get the user ID
//
//                // Prepare location data from collected LatLng points
//                val locations = allLatLng.map {
//                    LocationData(
//                        latitude = it.latitude,
//                        longitude = it.longitude,
//                        timestamp = System.currentTimeMillis() // Timestamp for each location
//                    )
//                }
//
//                val tripId = "trip_id" // Replace with dynamic trip ID if available
//
//                // Send location data to the API
//                val locationRequestData = LocationRequestData(tripId, locations)
//
//                // Call the API to send location data
//                val response = userRepository.sendTrackingData(locationRequestData)
//
//                if (response.isSuccessful) {
//                    Log.d(TAG, "Data tracking sent successfully")
//                } else {
//                    Log.e(TAG, "Failed to send tracking data: ${response.message()}")
//                    Toast.makeText(requireContext(), "Failed to send tracking data", Toast.LENGTH_SHORT).show()
//                }
//            } catch (e: Exception) {
//                Log.e(TAG, "Error sending location data: ${e.message}")
//                Toast.makeText(requireContext(), "Error: ${e.message}", Toast.LENGTH_SHORT).show()
//            }
//        }
//    }
//
//
//
//    private fun getMyLastLocation() {
//        if (checkPermission(Manifest.permission.ACCESS_FINE_LOCATION) &&
//            checkPermission(Manifest.permission.ACCESS_COARSE_LOCATION)
//        ) {
//            fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
//                if (location != null) {
//                    showStartMarker(location)
//                } else {
//                    Toast.makeText(
//                        requireContext(),
//                        "Location is not found. Try Again",
//                        Toast.LENGTH_SHORT
//                    ).show()
//                }
//            }
//        } else {
//            requestPermissionLauncher.launch(
//                arrayOf(
//                    Manifest.permission.ACCESS_FINE_LOCATION,
//                    Manifest.permission.ACCESS_COARSE_LOCATION
//                )
//            )
//        }
//    }
//
//    private fun showStartMarker(location: Location) {
//        val startLocation = LatLng(location.latitude, location.longitude)
//        mMap.addMarker(
//            MarkerOptions()
//                .position(startLocation)
//                .title(getString(R.string.start_point))
//        )
//        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(startLocation, 17f))
//    }
//
//    private val resolutionLauncher =
//        registerForActivityResult(ActivityResultContracts.StartIntentSenderForResult()) { result ->
//            when (result.resultCode) {
//                RESULT_OK -> Log.i(TAG, "onActivityResult: All location settings are satisfied.")
//                RESULT_CANCELED -> {
//                    Toast.makeText(
//                        requireContext(),
//                        "Anda harus mengaktifkan GPS untuk menggunakan aplikasi ini!",
//                        Toast.LENGTH_SHORT
//                    ).show()
//                }
//            }
//        }
//
//    private fun createLocationRequest() {
//        val priority = Priority.PRIORITY_HIGH_ACCURACY
//        val interval = TimeUnit.SECONDS.toMillis(1)
//        val maxWaitTime = TimeUnit.SECONDS.toMillis(1)
//        locationRequest = LocationRequest.Builder(priority, interval).apply {
//            setMaxUpdateDelayMillis(maxWaitTime)
//        }.build()
//
//        val builder = LocationSettingsRequest.Builder()
//            .addLocationRequest(locationRequest)
//        val client = LocationServices.getSettingsClient(requireContext())
//        client.checkLocationSettings(builder.build())
//            .addOnSuccessListener {
//                getMyLastLocation()
//            }
//            .addOnFailureListener { exception ->
//                if (exception is ResolvableApiException) {
//                    try {
//                        resolutionLauncher.launch(
//                            IntentSenderRequest.Builder(exception.resolution).build()
//                        )
//                    } catch (sendEx: IntentSender.SendIntentException) {
//                        Toast.makeText(requireContext(), sendEx.message, Toast.LENGTH_SHORT).show()
//                    }
//                }
//            }
//    }
//
//    private fun createLocationCallback() {
//        locationCallback = object : LocationCallback() {
//            override fun onLocationResult(locationResult: LocationResult) {
//                for (location in locationResult.locations) {
//                    Log.d(TAG, "onLocationResult: " + location.latitude + ", " + location.longitude)
//                    val lastLatLng = LatLng(location.latitude, location.longitude)
//
//                    // Menambahkan polyline
//                    allLatLng.add(lastLatLng)
//                    mMap.addPolyline(
//                        PolylineOptions()
//                            .color(Color.CYAN)
//                            .width(10f)
//                            .addAll(allLatLng)
//                    )
//
//                    // Menyesuaikan kamera sesuai dengan jalur yang diambil
//                    boundsBuilder.include(lastLatLng)
//                    val bounds: LatLngBounds = boundsBuilder.build()
//                    mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 64))
//                }
//            }
//        }
//    }
//
//    private fun startLocationUpdates() {
//        try {
//            fusedLocationClient.requestLocationUpdates(
//                locationRequest,
//                locationCallback,
//                Looper.getMainLooper()
//            )
//        } catch (exception: SecurityException) {
//            Log.e(TAG, "Error: " + exception.message)
//        }
//    }
//
//    private fun stopLocationUpdates() {
//        fusedLocationClient.removeLocationUpdates(locationCallback)
//    }
//
//    override fun onResume() {
//        super.onResume()
//        if (isTracking) {
//            startLocationUpdates()
//        }
//    }
//
//    override fun onPause() {
//        super.onPause()
//        stopLocationUpdates()
//    }
//
//    private fun updateTrackingStatus(newStatus: Boolean) {
//        isTracking = newStatus
//        if (isTracking) {
//            binding.btnStart.text = getString(R.string.stop_running)
//        } else {
//            binding.btnStart.text = getString(R.string.start_driving)
//        }
//    }
//
//    private fun clearMaps() {
//        mMap.clear()
//        allLatLng.clear()
//        boundsBuilder = LatLngBounds.Builder()
//    }
//
//    companion object {
//        private const val TAG = "MapsFragment"
//    }
//
//    override fun onMapReady(googleMap: GoogleMap) {
//        mMap = googleMap
//        if (checkPermission(Manifest.permission.ACCESS_FINE_LOCATION) &&
//            checkPermission(Manifest.permission.ACCESS_COARSE_LOCATION)) {
//            mMap.isMyLocationEnabled = true
//        } else {
//            requestPermissionLauncher.launch(
//                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)
//            )
//        }
//
//        getMyLastLocation()
//    }
//}
