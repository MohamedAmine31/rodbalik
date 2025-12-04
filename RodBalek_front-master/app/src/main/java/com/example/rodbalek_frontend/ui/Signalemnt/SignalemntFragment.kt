package com.example.rodbalek_frontend.ui.Signalemnt

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.rodbalek_frontend.R
import com.example.rodbalek_frontend.data.model.Signalement.Signalement
import com.example.rodbalek_frontend.databinding.FragmentSignalemntBinding
import com.example.rodbalek_frontend.ui.signalement.SignalementAdapter
import com.example.rodbalek_frontend.viewmodel.SignalemntViewModel
import com.example.rodbalek_frontend.viewmodel.SignalemntViewModelFactory

class SignalemntFragment : Fragment() {

    private var _binding: FragmentSignalemntBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: SignalementAdapter
    private lateinit var viewModel: SignalemntViewModel

    private var allSignalements: List<Signalement> = listOf()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSignalemntBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val factory = SignalemntViewModelFactory(requireContext())
        viewModel = ViewModelProvider(this, factory)[SignalemntViewModel::class.java]

        adapter = SignalementAdapter(emptyList())
        binding.recyclerSignalements.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerSignalements.adapter = adapter

        setupObservers()
        setupFilters()

        viewModel.getSignalements()
    }

    private fun setupObservers() {
        viewModel.signalements.observe(viewLifecycleOwner) { list ->
            allSignalements = list
            adapter.updateData(list)
        }

        viewModel.errorMessage.observe(viewLifecycleOwner) { errorMessage ->
            Log.e("SignalemntFragment", "Error: $errorMessage")
            Toast.makeText(context, "Erreur lors du chargement: $errorMessage", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setupFilters() {
        val filters = listOf(
            binding.filterAll,
            binding.filterEnCours,
            binding.filterApprouve,
            binding.filterRejete
        )

        filters.forEach { textView ->
            textView.setOnClickListener {
                filters.forEach {
                    it.setBackgroundResource(R.drawable.filter_unselected)
                    it.setTextColor(resources.getColor(android.R.color.black))
                }

                textView.setBackgroundResource(R.drawable.filter_selected)
                textView.setTextColor(resources.getColor(android.R.color.white))

                when (textView.id) {
                    R.id.filterAll -> adapter.updateData(allSignalements)
                    R.id.filterEnCours -> filtrerParEtat("EN_COURS")
                    R.id.filterApprouve -> filtrerParEtat("APPROUVE")
                    R.id.filterRejete -> filtrerParEtat("REJETE")
                }
            }
        }
    }

    private fun filtrerParEtat(etat: String) {
        val filtres = allSignalements.filter { it.statut == etat }
        adapter.updateData(filtres)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}