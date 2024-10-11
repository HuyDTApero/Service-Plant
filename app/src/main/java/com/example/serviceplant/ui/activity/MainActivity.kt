package com.example.serviceplant.ui.activity

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.serviceplant.R
import com.example.serviceplant.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    companion object {
        private const val READ_EXTERNAL_STORAGE_REQUEST_CODE = 1001
        private const val READ_MEDIA_IMAGES_REQUEST_CODE = 1002
    }

    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityMainBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    override fun onResume() {
        super.onResume()
        binding.btnChooseImage.setOnClickListener {
            requestImagePermissions()
        }
    }

    private fun requestImagePermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requestReadMediaImagesPermission()
        } else {
            requestReadExternalStoragePermission()
        }
    }

    private fun requestReadExternalStoragePermission() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            if (!ActivityCompat.shouldShowRequestPermissionRationale(
                    this,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                )
            ) {
                showRationaleAndRequestPermission(
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    READ_EXTERNAL_STORAGE_REQUEST_CODE,
                    R.string.permission_read_external_storage_rationale
                )
            } else {
                directToAppSettings()
            }
        } else {
            onPermissionsGranted()
        }
    }

    private fun requestReadMediaImagesPermission() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_MEDIA_IMAGES
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            if (!ActivityCompat.shouldShowRequestPermissionRationale(
                    this,
                    Manifest.permission.READ_MEDIA_IMAGES,
                )
            ) {
                showRationaleAndRequestPermission(
                    Manifest.permission.READ_MEDIA_IMAGES,
                    READ_MEDIA_IMAGES_REQUEST_CODE,
                    R.string.permission_read_media_images_rationale
                )
            } else {
                directToAppSettings()
            }
        } else {
            onPermissionsGranted()
        }

    }

    private fun showRationaleAndRequestPermission(permission: String, requestCode: Int, rationaleMessageId: Int) {
        AlertDialog.Builder(this)
            .setTitle("Permission Required")
            .setMessage(getString(rationaleMessageId))
            .setPositiveButton("OK") { _, _ ->
                ActivityCompat.requestPermissions(this, arrayOf(permission), requestCode)
            }
            .setNegativeButton("Cancel") { _, _ ->
               Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show()
            }
            .show()
    }

    private fun directToAppSettings() {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
        val uri = Uri.fromParts("package", packageName, null)
        intent.data = uri
        startActivity(intent)
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            READ_MEDIA_IMAGES_REQUEST_CODE, READ_EXTERNAL_STORAGE_REQUEST_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    onPermissionsGranted()
                } else {
                    onPermissionsDenied()
                }
            }
        }
    }

    private fun onPermissionsGranted() {
        val intent = Intent(this, GalleryActivity::class.java)
        startActivity(intent)
    }

    private fun onPermissionsDenied() {
        Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show()
    }
}