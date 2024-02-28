package com.example.wodrunapp


import android.annotation.SuppressLint
import android.content.Intent

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView

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
        mapController.setZoom(9.5)
        val startPoint = GeoPoint(48.8583, 2.2944)
        mapController.setCenter(startPoint)


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
}