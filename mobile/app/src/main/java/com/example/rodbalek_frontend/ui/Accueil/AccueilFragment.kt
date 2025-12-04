package com.example.rodbalek_frontend.ui.Accueil

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.rodbalek_frontend.R
import com.example.rodbalek_frontend.databinding.FragmentAccueilBinding

class AccueilFragment : Fragment() {

    private var _binding: FragmentAccueilBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAccueilBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnAddSignalement.setOnClickListener {
            findNavController().navigate(R.id.action_accueilFragment_to_ajoutSignalementFragment)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
