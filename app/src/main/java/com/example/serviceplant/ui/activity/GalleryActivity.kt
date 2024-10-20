package com.example.serviceplant.ui.activity

import android.os.Build
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import com.example.serviceplant.R
import com.example.serviceplant.databinding.ActivityGalleryBinding
import com.example.serviceplant.ui.adapter.GalleryAdapter
import com.example.serviceplant.ui.viewmodel.GalleryViewmodel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

@AndroidEntryPoint
class GalleryActivity : AppCompatActivity() {
    private val viewModel: GalleryViewmodel by viewModels()
    private lateinit var binding: ActivityGalleryBinding
    private val adapter by lazy { GalleryAdapter() }
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityGalleryBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        handleClickListener()
        handleObserver()

        binding.rcvImage.apply {
            adapter = this@GalleryActivity.adapter
            layoutManager =
                GridLayoutManager(
                    this@GalleryActivity,
                    3,
                    GridLayoutManager.VERTICAL,
                    false
                )
        }
    }

    private fun handleObserver() {
        lifecycleScope.launch {
            viewModel.listImage
                .flowWithLifecycle(lifecycle, Lifecycle.State.STARTED)
                .collectLatest {
                    adapter.differ.submitList(it)
                }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun handleClickListener() {
        adapter.onClick= {
            viewModel.setCurrentImage(it)
        }
        binding.tvNext.setOnClickListener {
            viewModel.identifyPlant("ABC", "ABC", "ABC")
        }
    }

    override fun onResume() {
        super.onResume()
    }
}