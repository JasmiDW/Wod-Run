package com.example.wodrunapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import android.widget.ListView
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import com.example.wodrunapp.service.ApiClient
import com.squareup.picasso.Picasso
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class CrossfitActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_crossfit)

        executeCall()
    }
    private fun executeCall(){
        GlobalScope.launch(Dispatchers.Main) {
            try {
                val response = ApiClient.apiService.getMouvements()
                if (response.isSuccessful && response.body() != null) {
                    val content = response.body()
                    content?.get(0)?.let {first ->
                        var button = findViewById<TextView>(R.id.btn_record)
                        button.setOnClickListener{
                            goToDetails(first)
                        }

                        val titre = findViewById<TextView>(R.id.mouvement)
                        val name = findViewById<TextView>(R.id.nom)
                        val type = findViewById<TextView>(R.id.categorie)
                        val image = findViewById<ImageView>(R.id.image)
                        val record = findViewById<TextView>(R.id.prRecord)
                        val youtube = findViewById<TextView>(R.id.btn_youtube)

                        titre.text = "Les mouvements"
                        name.text = first.name
                        type.text = first.type
                        Picasso.get().load(first.image).into(image);
                        youtube.text = first.video

                        record.text = (application as WRApplication).personnalRecordDao.getLastPrRecordByMouvementId(first.id)

                    }

                    val autres = content?.drop(1)
                    val listView = findViewById<ListView>(R.id.mouvementList)
                    listView.adapter = MouvementAdapter(this@CrossfitActivity, autres!!) {
                        goToDetails(it)
                    }

                } else {
                    Toast.makeText(
                        this@CrossfitActivity, "Erreur: ${response.message()}",
                        Toast.LENGTH_LONG ).show()
                }

            } catch (e: Exception) {
                Toast.makeText(
                    this@CrossfitActivity, "Pas de données: ${e.message}",
                    Toast.LENGTH_LONG ).show()

            }
        }
    }
    private fun goToDetails(mouvement: Mouvements) {
        (application as WRApplication).currentMouvement = mouvement
        startActivity(Intent(this@CrossfitActivity, RecordActivity::class.java))
    }
}