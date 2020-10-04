package co.softov.morestuff.androidApp.presentation.settings

import android.os.Bundle
import android.view.Menu
import android.view.View
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
import androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_NO
import androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_YES
import androidx.core.view.iterator
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import co.softov.morestuff.android.BuildConfig

import co.softov.morestuff.android.R
import co.softov.morestuff.androidApp.app.extensions.getColorFromAttr

class MainSettings : PreferenceFragmentCompat() {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setHasOptionsMenu(true)
        setPreferencesFromResource(R.xml.main_settings, rootKey)
        configurePreferences()
    }

    private fun configurePreferences() {
        findPreference<Preference>("version")?.title = "Version: ${getVersion()}"
        findPreference<Preference>("debug_notification")?.setOnPreferenceClickListener {
            // NotifierImpl(requireContext()).showNotification("Debug notification")
            true
        }

        findPreference<Preference>(getString(R.string.pref_key_app_theme))?.setOnPreferenceChangeListener { _, newValue ->
            when ((newValue as String).toInt()) {
                MODE_NIGHT_NO -> AppCompatDelegate.setDefaultNightMode(MODE_NIGHT_NO)
                MODE_NIGHT_YES -> AppCompatDelegate.setDefaultNightMode(MODE_NIGHT_YES)
                MODE_NIGHT_FOLLOW_SYSTEM -> AppCompatDelegate.setDefaultNightMode(
                    MODE_NIGHT_FOLLOW_SYSTEM
                )
            }
            true
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.setBackgroundColor(view.context.getColorFromAttr(android.R.attr.colorBackground))
    }

    private fun getVersion(): String {
        return "${BuildConfig.VERSION_NAME} (${BuildConfig.VERSION_CODE})"
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        super.onPrepareOptionsMenu(menu)
        menu.iterator().forEach { it.isVisible = false }
    }
}

