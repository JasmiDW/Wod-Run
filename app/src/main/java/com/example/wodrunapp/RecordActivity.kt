package com.example.wodrunapp

import android.content.ContentValues
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.room.Room
import com.example.wodrunapp.service.SQLHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class RecordActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_record)

        val current = (application as WRApplication).currentMouvement
        val button = findViewById<TextView>(R.id.btn_submit)
        findViewById<TextView>(R.id.nom_record).text = current.name
        findViewById<TextView>(R.id.categorie_record).text = current.type

        val backButton = findViewById<TextView>(R.id.btn_back)
        backButton.setOnClickListener{
            val intent = Intent(this@RecordActivity, CrossfitActivity::class.java)
            startActivity(intent)
            finish()
        }

        button.setOnClickListener {
            val input = findViewById<EditText>(R.id.addRecord).text
            val intent = Intent(this@RecordActivity, CrossfitActivity::class.java)
            intent.putExtra("Input", input)
            startActivity(intent)
            addRecords()
            finish()
        }
    }

    private fun addRecords(){
        GlobalScope.launch(Dispatchers.Main) {

            val current = (application as WRApplication).currentMouvement

            val input = findViewById<EditText>(R.id.addRecord).text.toString()
            (application as WRApplication).personnalRecordDao.insertAll(PersonnalRecord(0, current.id, input ))

            val record = (application as WRApplication).personnalRecordDao.getLastPrRecordByMouvementId(current.id)
            Toast.makeText(this@RecordActivity, "PR Enregistré: ${record}", Toast.LENGTH_SHORT).show()

        }
    }
}