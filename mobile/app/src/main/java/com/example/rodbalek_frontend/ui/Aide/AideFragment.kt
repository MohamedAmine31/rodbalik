package com.example.rodbalek_frontend.ui.Aide

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.rodbalek_frontend.databinding.FragmentAideBinding

class AideFragment : Fragment() {

    private var _binding: FragmentAideBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentAideBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        // Retour en arrière
//        binding.btnRetour.setOnClickListener {
//            requireActivity().onBackPressedDispatcher.onBackPressed()
//        }

        toggleFaq(binding.faq1, binding.qs1)
        toggleFaq(binding.faq2, binding.qs2)
        toggleFaq(binding.faq3, binding.qs3)

        binding.btnSupport.setOnClickListener {
            // tu pourras ouvrir un email ou une activité plus tard a implementer ray
            // par exemple :
            val intent = Intent(Intent.ACTION_SENDTO).apply {
                data = Uri.parse("mailto:mouhamedamine8688@gmail.com")
                putExtra(Intent.EXTRA_SUBJECT, "Besoin d'aide")
            }

            try {
                startActivity(intent)
            } catch (e: ActivityNotFoundException) {
                Toast.makeText(requireContext(), "Aucune application email installée", Toast.LENGTH_SHORT).show()
            }


        }
    }

    private fun toggleFaq(answer: View, question: View?) {
        question?.setOnClickListener {
            answer.visibility = if (answer.visibility == View.VISIBLE) View.GONE else View.VISIBLE
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
