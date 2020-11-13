package co.softov.morestuff.androidApp.presentation.settings

import android.os.Bundle
import android.view.Menu
import android.view.View
import android.view.WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
import androidx.appcompat.app.AppCompatDelegate.*
import androidx.core.view.iterator
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SwitchPreference
import co.softov.morestuff.android.BuildConfig
import co.softov.morestuff.android.R
import co.softov.morestuff.androidApp.app.presentation.extension.getColorFromAttr
import com.github.terrakok.cicerone.Router
import org.koin.android.ext.android.inject


class MainSettings : PreferenceFragmentCompat() {

    private val router: Router by inject()

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

        findPreference<SwitchPreference>(
            getString(R.string.pref_key_debug_keep_screen_on)
        )?.setOnPreferenceChangeListener { _, newValue ->
            when (newValue) {
                true -> requireActivity().window.addFlags(FLAG_KEEP_SCREEN_ON)
                else -> requireActivity().window.clearFlags(FLAG_KEEP_SCREEN_ON)
            }
            true
        }

        findPreference<Preference>(getString(R.string.pref_key_app_theme))?.setOnPreferenceChangeListener { _, newValue ->
            when ((newValue as String).toInt()) {
                MODE_NIGHT_NO -> setDefaultNightMode(MODE_NIGHT_NO)
                MODE_NIGHT_YES -> setDefaultNightMode(MODE_NIGHT_YES)
                MODE_NIGHT_FOLLOW_SYSTEM -> setDefaultNightMode(
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

    fun onBackPressed() {
        router.exit()
    }

}

