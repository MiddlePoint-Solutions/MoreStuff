package co.softov.morestuff.android.ui.chat

import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.rememberNestedScrollInteropConnection
import androidx.fragment.app.ListFragment
import co.softov.morestuff.android.app.presentation.compose.BaseComposeFragment
import co.softov.morestuff.android.ui.list.ListsFragment
import co.softov.morestuff.android.ui.main.MainConductor
import co.softov.morestuff.android.ui.main.MainContent

class TaskChatFragment : BaseComposeFragment() {

    private val conductor = object : MainConductor {

        override fun showTaskList() {
            ListsFragment().show(parentFragmentManager, ListFragment::javaClass.name)
        }
    }

    @Composable
    override fun ScreenContent() {
        val nestedScrollInterop = rememberNestedScrollInteropConnection()
        MainContent(
            conductor,
            modifier = Modifier
                .systemBarsPadding()
                .nestedScroll(nestedScrollInterop)
        )
    }

}