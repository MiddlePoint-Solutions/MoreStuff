package co.softov.morestuff.android.domain.model

data class Task(
    val id: Long,
    val title: String,
    val createTime: String,
    val completeTime: String? = null
) {


    override fun equals(other: Any?): Boolean {
        return super.equals(other)
    }

    companion object {
        fun empty() = Task(
            id = 0L,
            title = "",
            createTime = ""
        )
    }
}
