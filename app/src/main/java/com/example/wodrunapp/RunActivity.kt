package com.example.wodrunapp


import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Build

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Looper
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts

import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.osmdroid.config.Configuration
import org.osmdroid.events.MapEventsReceiver
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.MapEventsOverlay
import org.osmdroid.views.overlay.Marker
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority

class RunActivity : AppCompatActivity() {

    private var selectedItemId = R.id.nav_run
    private lateinit var mapView: MapView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_run)
        mapView = findViewById(R.id.mapView)
        mapView.setTileSource(TileSourceFactory.DEFAULT_TILE_SOURCE)
        mapView.setMultiTouchControls(true)

        Configuration.getInstance().userAgentValue = packageName
        val mapController = mapView.controller
        mapController.setZoom(18.5)
        val startPoint = GeoPoint(48.8583, 2.2944)
        mapController.setCenter(startPoint)

        listenToNewMarkers()

        if(checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
            checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            //on a déjà la permission
            activateLocation()
        } else {
            //On n'a pas encore la permission
            if (shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_COARSE_LOCATION) ||
                shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION)) {
                requestPermission()
                activateLocation()
            } else {
                requestPermission()
            }


        }

        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_navigation)

        // Définir manuellement l'élément sélectionné à Run
        bottomNavigationView.selectedItemId = selectedItemId

        bottomNavigationView.setOnNavigationItemSelectedListener { item ->
            if (item.itemId == selectedItemId) {
                // L'élément sélectionné est déjà l'élément actuellement sélectionné, donc aucune action n'est nécessaire
                false
            } else {
                selectedItemId = item.itemId
                when (item.itemId) {
                    R.id.nav_crossfit -> {
                        // Naviguer vers CrossfitActivity
                        val intent = Intent(this, CrossfitActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_REORDER_TO_FRONT or Intent.FLAG_ACTIVITY_SINGLE_TOP
                        startActivity(intent)
                        true
                    }
                    R.id.nav_run -> {
                        // Vous êtes déjà dans RunActivity, donc aucune action n'est nécessaire
                        true
                    }
                    else -> false
                }
            }
        }

        executeCall()
    }
    private fun executeCall(){
        GlobalScope.launch(Dispatchers.Main) {

        }
    }

    private fun addMarker() {
        val clickListener = { marker: Marker, mapView: MapView ->
            Toast.makeText(this, marker.snippet, Toast.LENGTH_LONG).show()
            marker.showInfoWindow()
            true
        }

        listOf(
            Triple(48.8583, 2.2944, "Eiffel Tower"),
            Triple(21, 1, "Arc de Triomphe"),
            Triple(55, 4, "Lac"),
        ).forEach {
            val marker = Marker(mapView)
            marker.title = it.third
            marker.snippet = "Latitude: ${it.first}, Longitude: ${it.second}"
            marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
            marker.setOnMarkerClickListener(clickListener)
            mapView.overlays.add(marker)

        }
    }

    private fun listenToNewMarkers() {
        val evOverlay = MapEventsOverlay(object : MapEventsReceiver {
            override fun singleTapConfirmedHelper(p: GeoPoint?): Boolean {
                return false
            }

            override fun longPressHelper(p: GeoPoint?): Boolean {
                val marker = Marker(mapView)
                marker.position = p
                marker.title = "New Marker"
                marker.snippet = "Latitude: ${p?.latitude}, Longitude: ${p?.longitude}"
                mapView.overlays.add(marker)
                vibratePhone()
                return true
            }
        })
        mapView.overlays.add(evOverlay)
    }

    fun vibratePhone(){
        val vib = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val vibratorManager =
                getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
            vibratorManager.defaultVibrator
        } else {
            @Suppress("DEPRECIE")
            getSystemService(VIBRATOR_SERVICE) as Vibrator
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vib.vibrate(VibrationEffect.createOneShot(150, VibrationEffect.DEFAULT_AMPLITUDE))

        }else {
            @Suppress("DEPRECIE")
            vib.vibrate(150)
        }
    }

    fun requestPermission(){
        val locationPermissionRequest = registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) {permissions ->
            when {
                permissions.getOrDefault(Manifest.permission.ACCESS_FINE_LOCATION, false) -> {
                    Toast.makeText(this, "Fine OK", Toast.LENGTH_SHORT).show()
                }

                permissions.getOrDefault(Manifest.permission.ACCESS_COARSE_LOCATION, false) -> {
                    Toast.makeText(this, "Approximative", Toast.LENGTH_SHORT).show()
                }
                else -> {
                    Toast.makeText(this, "Refusée", Toast.LENGTH_SHORT).show()
                }
            }
        }
        locationPermissionRequest.launch(
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        )
    }

    fun activateLocation(){
        if(checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
            checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            val fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
            fusedLocationClient.lastLocation
                .addOnSuccessListener {
                        location: Location? ->
                    Toast.makeText(this,
                        "Dernière position connue : ${location?.latitude}, ${location?.longitude} ", Toast.LENGTH_SHORT).show()
                }

            val locationRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 5000)
                .setMinUpdateDistanceMeters(20f)
                .setWaitForAccurateLocation(true)
                .build()

            val locationCallback = object : LocationCallback() {
                override fun onLocationResult(locationResult: LocationResult) {
                    for (location in locationResult.locations) {
                        Toast.makeText(
                            this@RunActivity,
                            "Nouvelle position connue :  ${location?.latitude}, ${location?.longitude} ", Toast.LENGTH_SHORT).show()

                    }
                }
            }

            fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper())
        }

    }
}