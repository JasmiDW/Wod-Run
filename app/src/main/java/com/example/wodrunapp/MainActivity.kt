package com.example.wodrunapp

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        var button = findViewById<Button>(R.id.button)

        button.setOnClickListener{
            val intent = Intent(this, CrossfitActivity::class.java)
            startActivity(intent)
        }

        var button2 = findViewById<Button>(R.id.buttonRun)
        button2.setOnClickListener{
                val intent = Intent(this, RunActivity::class.java)
                startActivity(intent)
            }

    }
}