package com.example.rodbalek_frontend

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupActionBarWithNavController
import com.example.rodbalek_frontend.databinding.ActivityNavigationBinding

class Navigation : AppCompatActivity() {
    private lateinit var binding: ActivityNavigationBinding
    private lateinit var appBarConfiguration: AppBarConfiguration

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityNavigationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.topAppBar)

        val navController = findNavController(R.id.nav_host_fragment_activity_navigation)

        appBarConfiguration = AppBarConfiguration(
            setOf(R.id.nav_accueil, R.id.nav_signalement, R.id.nav_aide, R.id.nav_profil)
        )


        setupActionBarWithNavController(navController, appBarConfiguration)

        // Connecter la BottomNavigationView avec le NavController
        NavigationUI.setupWithNavController(binding.navView, navController)
    }
    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_activity_navigation)
        return navController.navigateUp() || super.onSupportNavigateUp()
    }
}
