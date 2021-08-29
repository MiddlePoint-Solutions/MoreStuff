package co.softov.morestuff.android.presentation.list.model

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import co.softov.morestuff.android.R

enum class PageType {
    PAGE_ACTIVE_TASKS,
    PAGE_COMPLETE_TASKS,
    PAGE_ACTIVE_SCHEDULES,
    PAGE_ACTIVE_TODAY,
    PAGE_ACTIVE_TOMORROW,
    PAGE_ACTIVE_LATER
}

data class Page(val type: PageType)

val Page.title
    @Composable get() = when (this.type) {
        PageType.PAGE_ACTIVE_TASKS -> stringResource(id = R.string.page_title_active)
        PageType.PAGE_COMPLETE_TASKS -> stringResource(id = R.string.page_title_complete)
        PageType.PAGE_ACTIVE_SCHEDULES -> stringResource(id = R.string.page_title_schedule)
        PageType.PAGE_ACTIVE_TODAY -> stringResource(id = R.string.page_title_today)
        PageType.PAGE_ACTIVE_TOMORROW -> stringResource(id = R.string.page_title_tomorrow)
        PageType.PAGE_ACTIVE_LATER -> stringResource(id = R.string.page_title_later)
    }

