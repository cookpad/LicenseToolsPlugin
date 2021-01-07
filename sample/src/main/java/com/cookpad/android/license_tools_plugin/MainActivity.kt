package com.cookpad.android.license_tools_plugin

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.cookpad.android.license_tools_plugin.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.setTitle(R.string.oss_licenses)
        setupLicensesView()
    }

    private fun setupLicensesView() {
        binding.licensesView.settings.javaScriptEnabled = false
        binding.licensesView.loadUrl(LICENSES_FILE_PATH)
    }

    companion object {
        private const val LICENSES_FILE_PATH = "file:///android_asset/licenses.html"
    }
}
