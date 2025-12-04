package com.example.rodbalek_frontend.ui.signalement

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.rodbalek_frontend.R
import com.example.rodbalek_frontend.data.model.Signalement.Signalement
import com.example.rodbalek_frontend.databinding.ItemSignalementBinding

class SignalementAdapter(private var signalements: List<Signalement>) :
    RecyclerView.Adapter<SignalementAdapter.SignalementViewHolder>() {

    inner class SignalementViewHolder(val binding: ItemSignalementBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SignalementViewHolder {
        val binding = ItemSignalementBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return SignalementViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SignalementViewHolder, position: Int) {
        val signalement = signalements[position]
        with(holder.binding) {
            txtTitre.text = signalement.description
            txtDate.text = signalement.date

            // Charger l'image depuis l'URL avec Glide
            if (!signalement.photo.isNullOrEmpty()) {
                Glide.with(imgSignalement.context)
                    .load(signalement.photo) // URL renvoyée par le backend
                    .placeholder(R.drawable.ic_launcher_background)
                    .error(R.drawable.ic_launcher_background)
                    .into(imgSignalement)
            } else {
                imgSignalement.setImageResource(R.drawable.ic_launcher_background)
            }

            when (signalement.statut) {
                "EN_COURS" -> {
                    txtEtat.text = "⏳ En cours"
                    holder.binding.root.setBackgroundColor(0xFFFFF9C4.toInt()) // jaune clair
                }
                "APPROUVE" -> {
                    txtEtat.text = "✅ Approuvé"
                    holder.binding.root.setBackgroundColor(0xFFC8E6C9.toInt()) // vert clair
                }
                "REJETE" -> {
                    txtEtat.text = "❌ Rejeté"
                    holder.binding.root.setBackgroundColor(0xFFFFCDD2.toInt()) // rouge clair
                }
                else -> {
                    txtEtat.text = "Tous"
                    holder.binding.root.setBackgroundColor(0xFFFFFFFF.toInt()) // blanc
                }
            }
        }
    }

    fun updateData(newList: List<Signalement>) {
        signalements = newList
        notifyDataSetChanged()
    }

    override fun getItemCount() = signalements.size
}
