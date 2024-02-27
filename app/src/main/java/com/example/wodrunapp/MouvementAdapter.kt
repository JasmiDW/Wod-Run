package com.example.wodrunapp


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


class MouvementAdapter (
    private val actyivityParent: AppCompatActivity,
    mouvements: List<Mouvements>,
    private val buttonClicked: (mvt : Mouvements) -> Unit,
    private val youtubeClicked: (mvt : Mouvements) -> Unit
) : ArrayAdapter<Mouvements>(actyivityParent, R.layout.mouvement_layout, mouvements){
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val cell = convertView ?: LayoutInflater.from(context).inflate(R.layout.mouvement_layout, null)

        getItem(position)?.let { mouvement ->
            cell.findViewById<TextView>(R.id.nom).text = mouvement?.name
            cell.findViewById<TextView>(R.id.categorie).text = mouvement?.type
            cell.findViewById<TextView>(R.id.btn_youtube).setOnClickListener {
                youtubeClicked(mouvement)
            }

            GlobalScope.launch(Dispatchers.Main) {
                cell.findViewById<TextView>(R.id.prRecord).text =
                    (actyivityParent.application as WRApplication).personnalRecordDao.getLastPrRecordByMouvementId(mouvement.id)
            }

            cell.findViewById<TextView>(R.id.btn_record).setOnClickListener {
                buttonClicked(mouvement);
            }
        }

        return cell
    }
}