package com.example.wodrunapp


import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Rect
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.wodrunapp.service.ApiClient
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.squareup.picasso.Picasso
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class RunActivity : AppCompatActivity() {

    private var selectedItemId = R.id.nav_run

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_run)

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