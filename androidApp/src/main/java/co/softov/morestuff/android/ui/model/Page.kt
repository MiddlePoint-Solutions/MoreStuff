package co.softov.morestuff.android.ui.model

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import co.softov.morestuff.android.R

enum class PageType {
    PAGE_COMPLETE_TASKS,
    PAGE_ACTIVE_NOW,
    PAGE_ACTIVE_LATER,
    PAGE_SCHEDULE
}

val PageType.title
    @Composable get() = when (this) {
        PageType.PAGE_COMPLETE_TASKS -> stringResource(id = R.string.page_title_complete)
        PageType.PAGE_ACTIVE_NOW -> stringResource(id = R.string.page_title_now)
        PageType.PAGE_ACTIVE_LATER -> stringResource(id = R.string.page_title_later)
        PageType.PAGE_SCHEDULE -> stringResource(id = R.string.page_title_schedule)
    }