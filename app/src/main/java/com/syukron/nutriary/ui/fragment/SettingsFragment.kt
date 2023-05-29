package com.syukron.nutriary.ui.fragment

import android.os.Bundle
import android.text.InputType
import android.text.TextUtils
import androidx.appcompat.app.AppCompatDelegate
import androidx.preference.*
import com.syukron.nutriary.MainActivity
import com.syukron.nutriary.R
import com.syukron.nutriary.util.ThemeProvider

class SettingsFragment : PreferenceFragmentCompat() {

    private val themeProvider by lazy { ThemeProvider(requireContext()) }
    private val themePreference by lazy {
        findPreference<ListPreference>(getString(R.string.theme_preferences_key))
    }

    override fun onCreatePreferences(
        savedInstanceState: Bundle?,
        rootKey: String?
    ) {
        setPreferencesFromResource(R.xml.preferences, rootKey)
        setThemePreference()
    }

    private fun setThemePreference() {
        themePreference?.onPreferenceChangeListener =
            Preference.OnPreferenceChangeListener { _, newValue ->
                if (newValue is String) {
                    val theme = themeProvider.getTheme(newValue)
                    AppCompatDelegate.setDefaultNightMode(theme)
                }
                true
            }
    }

}