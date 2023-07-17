package co.softov.morestuff.android.ui.settings

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import co.softov.morestuff.android.domain.enums.AppSetting
import com.alorma.compose.settings.storage.base.SettingValueState

@Composable
fun rememberIntAppSettingState(
    defaultValue: () -> Int = { 0 },
    valueChanged: (Int) -> Unit = {}
): IntAppSettingValueState {
    return remember {
        IntAppSettingValueState(
            defaultValue = defaultValue,
            valueChanged = valueChanged,
        )
    }
}

class IntAppSettingValueState(
    private val defaultValue: () -> Int,
    private val valueChanged: (Int) -> Unit,
) : SettingValueState<Int> {

    private var _value by mutableIntStateOf(defaultValue())

    override var value: Int
        set(value) {
            valueChanged(value)
            _value = value
        }
        get() = _value

    override fun reset() {
        value = defaultValue()
    }
}