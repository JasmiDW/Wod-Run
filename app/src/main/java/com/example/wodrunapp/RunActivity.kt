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
import android.text.InputType
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.room.Room

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
import java.util.Date
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat

class RunActivity : AppCompatActivity() {

    private var selectedItemId = R.id.nav_run
    private lateinit var mapView: MapView
    private var lastKnownLocation: GeoPoint? = null
    private lateinit var db: AppDatabaseRun


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_run)

        db = Room.databaseBuilder(
            applicationContext,
            AppDatabaseRun::class.java, "run_database"
        ).addMigrations(MIGRATION_1_2).build()


        mapView = findViewById(R.id.mapView)
        mapView.setTileSource(TileSourceFactory.DEFAULT_TILE_SOURCE)
        mapView.setMultiTouchControls(true)

        Configuration.getInstance().userAgentValue = packageName
        val mapController = mapView.controller
        mapController.setZoom(18.5)
        val startPoint = lastKnownLocation ?: GeoPoint(48.8583, 2.2944)
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
                        // Déjà dans RunActivity, donc aucune action n'est nécessaire
                        true
                    }
                    else -> false
                }
            }
        }

        db.courseDao().getAll().observe(this@RunActivity) { courses ->
            val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
            recyclerView.layoutManager = LinearLayoutManager(this@RunActivity)
            recyclerView.adapter = CourseAdapter(courses.toMutableList(), db)
        }

        executeCall()
    }
    private fun executeCall(){
        GlobalScope.launch(Dispatchers.Main) {

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
                marker.title = "Nouvelle course"
                marker.snippet = "Latitude: ${p?.latitude}, Longitude: ${p?.longitude}"
                mapView.overlays.add(marker)
                vibratePhone()

                // Crée un dialog pour ajouter une nouvelle course
                val builder = AlertDialog.Builder(this@RunActivity)
                builder.setTitle("Nouvelle course")

                // Créer les input
                val nameInput = EditText(this@RunActivity)
                nameInput.inputType = InputType.TYPE_CLASS_TEXT
                nameInput.hint = "Nom de la course"

                val distanceInput = EditText(this@RunActivity)
                distanceInput.inputType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL
                distanceInput.hint = "Distance en km"

                val timeInput = EditText(this@RunActivity)
                timeInput.inputType = InputType.TYPE_CLASS_NUMBER
                timeInput.hint = "Temps en minutes"

                // Ajoute les input dans le dialog
                val layout = LinearLayout(this@RunActivity)
                layout.orientation = LinearLayout.VERTICAL
                layout.addView(nameInput)
                layout.addView(distanceInput)
                layout.addView(timeInput)

                builder.setView(layout)

                // Met en place les boutons
                builder.setPositiveButton("Valider") { dialog, _ ->
                    val distance = distanceInput.text.toString().toDoubleOrNull() ?: 0.0
                    val time = timeInput.text.toString().toLongOrNull() ?: 0L
                    val name = nameInput.text.toString()

                    val course = CourseEntity(
                        id = 0,
                        name = name,
                        latitude = p?.latitude ?: 0.0,
                        longitude = p?.longitude ?: 0.0,
                        date = SimpleDateFormat("yyyy-MM-dd").format(Date()),
                        distance = distance,
                        time = time,
                    )

                    GlobalScope.launch{
                        db.courseDao().insertAll(course)
                    }

                    dialog.dismiss()
                }
                builder.setNegativeButton("Annuler") { dialog, _ -> dialog.cancel() }

                builder.show()

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

    fun activateLocation(): GeoPoint?{
        if(checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
            checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            val fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
            fusedLocationClient.lastLocation
                .addOnSuccessListener {
                        location: Location? ->
                    Toast.makeText(this,
                        "Dernière position connue : ${location?.latitude}, ${location?.longitude} ", Toast.LENGTH_SHORT).show()

                    if (location != null) {
                        lastKnownLocation = GeoPoint(location.latitude, location.longitude)
                    }
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
                            lastKnownLocation = GeoPoint(location.latitude, location.longitude)

                    }

                    // Déplace le centre de la carte à la nouvelle position
                    val mapController = mapView.controller
                    mapController.animateTo(lastKnownLocation)
                }
            }

            fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper())
        }
        return lastKnownLocation
    }

}