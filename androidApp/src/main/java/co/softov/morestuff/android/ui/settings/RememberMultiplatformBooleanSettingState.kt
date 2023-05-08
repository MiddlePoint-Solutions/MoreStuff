package co.softov.morestuff.android.ui.settings

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.alorma.compose.settings.storage.base.SettingValueState
import com.russhwolf.settings.Settings
import org.koin.compose.koinInject

@Composable
fun rememberMultiplatformBooleanSettingState(
    key: String,
    defaultValue: Boolean,
    settings: Settings = koinInject()
): BooleanMultiplatformSettingValueState {
    return remember {
        BooleanMultiplatformSettingValueState(
            settings = settings,
            key = key,
            defaultValue = defaultValue
        )
    }
}

class BooleanMultiplatformSettingValueState(
    private val settings: Settings,
    val key: String,
    val defaultValue: Boolean = false,
) : SettingValueState<Boolean> {

    private var _value by mutableStateOf(settings.getBoolean(key, defaultValue))

    override var value: Boolean
        set(value) {
            _value = value
            settings.putBoolean(key, value)
        }
        get() = _value

    override fun reset() {
        value = defaultValue
    }
}