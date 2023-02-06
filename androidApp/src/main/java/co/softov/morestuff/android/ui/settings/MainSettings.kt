package co.softov.morestuff.android.ui.settings

import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import android.view.WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
import androidx.appcompat.app.AppCompatDelegate.*
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SeekBarPreference
import androidx.preference.SwitchPreference
import co.softov.morestuff.android.BuildConfig
import co.softov.morestuff.android.R
import co.softov.morestuff.android.app.presentation.extension.getColorFromAttr
import com.github.terrakok.cicerone.Router
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel


class MainSettings : PreferenceFragmentCompat() {

    private val viewModel: SettingsViewModel by viewModel()
    private val router: Router by inject()

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.main_settings, rootKey)
        configurePreferences()
    }

    private fun configurePreferences() {
        findPreference<Preference>("version")?.title = "Version: ${getVersion()}"
        findPreference<Preference>("debug_notification")?.setOnPreferenceClickListener {
//             NotifierImpl(requireContext()).showNotification("Debug notification")
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

        findPreference<SeekBarPreference>(getString(R.string.pref_key_snooze_limit))?.setOnPreferenceChangeListener { _, newValue ->
            viewModel.onSnoozeLimitChanged(newValue as Int)
            true
        }

        findPreference<SwitchPreference>(
            getString(R.string.pref_key_smart_reminder_enabled)
        )?.setOnPreferenceChangeListener { _, newValue ->
            viewModel.smartReminderEnabled(newValue as Boolean)
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

    fun onBackPressed() {
        router.exit()
    }

}

