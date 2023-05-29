package com.syukron.nutriary

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatDelegate
import com.syukron.nutriary.ui.onboarding.OnBoardingActivity
import com.syukron.nutriary.util.ThemeProvider

class SplashActivity : AppCompatActivity() {

    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        val theme = ThemeProvider(this).getThemeFromPreferences()
        AppCompatDelegate.setDefaultNightMode(theme)

        sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)

        // Check if the app has been installed before
        // Periksa apakah aktivitas sudah dijalankan sebelumnya
        val isActivityExecuted = sharedPreferences.getBoolean("isActivityExecuted", false)
        if (!isActivityExecuted) {
            // Jalankan aktivitas hanya saat aplikasi diinstall
            Handler(this.mainLooper).postDelayed({
                startActivity(Intent(this, OnBoardingActivity::class.java))
                finish()
            }, ANIMATION_TIME)

            // Set tanda bahwa aktivitas telah dijalankan
            val editor = sharedPreferences.edit()
            editor.putBoolean("isActivityExecuted", true)
            editor.apply()
        }
        else{
            Handler(this.mainLooper).postDelayed({
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            }, ANIMATION_TIME)
        }
        supportActionBar?.hide()
    }

    companion object {
        const val ANIMATION_TIME: Long = 3000
    }
}