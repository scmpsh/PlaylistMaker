package com.practicum.playlistmaker.main.ui

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.ActivityRootBinding

class RootActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRootBinding
    private var lastInsets: WindowInsetsCompat? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityRootBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { _, insets ->
            lastInsets = insets
            applyInsets(insets)
            insets
        }

        val navHostHolder =
            supportFragmentManager.findFragmentById(R.id.rootFragmentContainerView) as NavHostFragment
        val navController = navHostHolder.navController

        binding.bottomNavigationView.setupWithNavController(navController)

        navController.addOnDestinationChangedListener { _, destination, _ ->
            binding.bottomNavViewGroup.isVisible = !IDS_WITHOUT_BOTTOM_NAV.contains(destination.id)
            lastInsets?.let { applyInsets(it) }
        }
    }

    private fun applyInsets(insets: WindowInsetsCompat) {
        val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
        val ime = insets.getInsets(WindowInsetsCompat.Type.ime())

        binding.root.setPadding(systemBars.left, systemBars.top, systemBars.right, 0)

        if (binding.bottomNavViewGroup.isVisible) {
            binding.bottomNavViewGroup.setPadding(0, 0, 0, systemBars.bottom)
            binding.rootFragmentContainerView.setPadding(0, 0, 0, 0)
        } else {
            binding.bottomNavViewGroup.setPadding(0, 0, 0, 0)
            val bottomPadding = if (ime.bottom > systemBars.bottom) ime.bottom else systemBars.bottom
            binding.rootFragmentContainerView.setPadding(0, 0, 0, bottomPadding)
        }
    }

    companion object {
        private val IDS_WITHOUT_BOTTOM_NAV =
            setOf(R.id.audioPlayerFragment, R.id.createPlaylistFragment, R.id.playlistDetailsFragment, R.id.editPlaylistFragment)
    }
}