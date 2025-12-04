package com.example.rodbalek_frontend.ui.Profile

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.example.rodbalek_frontend.api.RetrofitClient
import com.example.rodbalek_frontend.data.model.user.UserRes
import com.example.rodbalek_frontend.databinding.FragmentProfilBinding
import com.example.rodbalek_frontend.ui.auth.LoginActivity
import com.example.rodbalek_frontend.utils.SessionManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ProfilFragment: Fragment() {

    private var _binding: FragmentProfilBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfilBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        fetchUserProfile()

        binding.btnLogout.setOnClickListener {
            SessionManager.logout(requireContext())
            Toast.makeText(requireContext(), "Déconnecté", Toast.LENGTH_SHORT).show()
            requireActivity().finish()
            val intent = Intent(requireContext(), LoginActivity::class.java)
            startActivity(intent)
        }
    }

    private fun fetchUserProfile() {
        RetrofitClient.getInstance(requireContext()).getUserProfile()
            .enqueue(object : Callback<UserRes> {
                override fun onResponse(call: Call<UserRes>, response: Response<UserRes>) {
                    if (response.isSuccessful) {
                        response.body()?.let { user ->
                            bindUserData(user)
                        }
                    } else {
                        Toast.makeText(requireContext(), "Erreur API : ${response.code()}", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<UserRes>, t: Throwable) {
                    Toast.makeText(requireContext(), "Erreur réseau", Toast.LENGTH_SHORT).show()
                }
            })
    }

    private fun bindUserData(user: UserRes) {
        binding.tvUsername.text = user.username
        binding.tvEmail.text = user.email
        binding.tvFirstName.text = user.first_name
        binding.tvLastName.text = user.last_name
        binding.tvPhone.text = user.phone?.toString() ?: "Non renseigné"
        binding.tvCreatedAt.text = user.created_at.replace("T", " ").substring(0, 16)
        binding.chipRole.text = user.role

        Glide.with(this)
            .load(user.imageUrl)
            .placeholder(com.example.rodbalek_frontend.R.drawable.ic_profile_placeholder_1)
            .into(binding.profileAvatar)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
