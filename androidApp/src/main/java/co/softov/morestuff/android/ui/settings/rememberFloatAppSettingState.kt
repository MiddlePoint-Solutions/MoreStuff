package co.softov.morestuff.android.ui.settings

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import co.softov.morestuff.android.domain.enums.AppSetting
import com.alorma.compose.settings.storage.base.SettingValueState

@Composable
fun rememberFloatAppSettingState(
    defaultValue: () -> Float = { 0f },
    valueChanged: (Float) -> Unit = {}
): FloatAppSettingValueState {
    return remember {
        FloatAppSettingValueState(
            defaultValue = defaultValue,
            valueChanged = valueChanged,
        )
    }
}

class FloatAppSettingValueState(
    private val defaultValue: () -> Float = { 0f },
    private val valueChanged: (Float) -> Unit = {}
) : SettingValueState<Float> {

    private var _value by mutableFloatStateOf(defaultValue())

    override var value: Float
        set(value) {
            valueChanged(value)
            _value = value
        }
        get() = _value

    override fun reset() {
        value = defaultValue()
    }
}