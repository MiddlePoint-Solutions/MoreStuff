package io.middlepoint.morestuff.android.ui.settings

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisallowComposableCalls
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import io.middlepoint.morestuff.android.domain.enums.AppSetting
import com.alorma.compose.settings.storage.base.SettingValueState

@Composable
fun <T> rememberAppSettingState(
    defaultValue: () -> T,
    valueChanged: (T) -> Unit,
): AppSettingValueState<T> {
    return remember {
        AppSettingValueState(
            defaultValue = defaultValue,
            valueChanged = valueChanged,
        )
    }
}

class AppSettingValueState<T>(
    private val defaultValue: () -> T,
    private val valueChanged: (T) -> Unit,
) : SettingValueState<T> {

    private var _value by mutableStateOf(defaultValue())

    override var value: T
        set(value) {
            valueChanged(value)
            _value = value
        }
        get() = _value

    override fun reset() {
        value = defaultValue()
    }
}