package com.example.wodrunapp


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


class MouvementAdapter (
    private val activityParent: AppCompatActivity,
    private val mouvements: List<Mouvements>,
    private val buttonClicked: (mvt : Mouvements) -> Unit,
    private val youtubeClicked: (mvt : Mouvements) -> Unit
) : RecyclerView.Adapter<MouvementAdapter.ViewHolder>() {

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val nom: TextView = view.findViewById(R.id.nom)
        val categorie: TextView = view.findViewById(R.id.categorie)
        val btnYoutube: TextView = view.findViewById(R.id.btn_youtube)
        val prRecord: TextView = view.findViewById(R.id.prRecord)
        val btnRecord: TextView = view.findViewById(R.id.btn_record)
        val image: ImageView = view.findViewById(R.id.image)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.mouvement_layout, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val mouvement = mouvements[position]
        Picasso.get().load(mouvement.image).into(holder.image);
        holder.nom.text = mouvement.name
        holder.categorie.text = mouvement.type
        holder.btnYoutube.setOnClickListener {
            youtubeClicked(mouvement)
        }

        GlobalScope.launch(Dispatchers.Main) {
            holder.prRecord.text =
                (activityParent.application as WRApplication).personnalRecordDao.getLastPrRecordByMouvementId(mouvement.id)
        }

        holder.btnRecord.setOnClickListener {
            buttonClicked(mouvement)
        }
    }

    override fun getItemCount() = mouvements.size
}