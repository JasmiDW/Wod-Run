package com.example.wodrunapp

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

class CrossfitActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_crossfit)

        val mouvementList = findViewById<RecyclerView>(R.id.mouvementList)
        val layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)

        mouvementList.layoutManager = layoutManager
        mouvementList.addItemDecoration(SpacesItemDecoration(10))

        executeCall(mouvementList, layoutManager)

        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_navigation)

        bottomNavigationView.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_crossfit -> {
                    // Vous êtes déjà dans CrossfitActivity, donc aucune action n'est nécessaire
                    true
                }
                R.id.nav_run -> {
                    // Naviguer vers RunActivity
                    val intent = Intent(this, CrossfitActivity::class.java)
                    startActivity(intent)
                    true
                }
                else -> false
            }
        }
    }
    private fun executeCall(mouvementList: RecyclerView, layoutManager: LinearLayoutManager){
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
                        youtube.apply {
                            text = "Youtube"
                            setOnClickListener {
                                val youtubeIntent = Intent(Intent.ACTION_VIEW, Uri.parse(first.video))
                                context.startActivity(youtubeIntent)
                            }
                        }

                        record.text = (application as WRApplication).personnalRecordDao.getLastPrRecordByMouvementId(first.id)

                        val autres = content?.drop(1)
                        val adapter = MouvementAdapter(this@CrossfitActivity, autres!!,
                            buttonClicked = { goToDetails(it) },
                            youtubeClicked = { openYoutubeLink(it) }
                        )

                        mouvementList.adapter = adapter
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

    private fun openYoutubeLink(mouvement: Mouvements) {
        val youtubeIntent = Intent(Intent.ACTION_VIEW, Uri.parse(mouvement.video))
        startActivity(youtubeIntent)
    }


}
class SpacesItemDecoration(private val space: Int) : RecyclerView.ItemDecoration() {
    override fun getItemOffsets(
        outRect: Rect, view: View,
        parent: RecyclerView, state: RecyclerView.State
    ) {
        outRect.left = space
        outRect.right = space
        outRect.bottom = space

        // Add top margin only for the first item to avoid double space between items
        if (parent.getChildLayoutPosition(view) == 0) {
            outRect.top = space
        }
    }
}