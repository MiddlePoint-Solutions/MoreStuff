package co.softov.morestuff.android.domain.usecase.task

import arrow.core.Either
import co.softov.morestuff.android.data.utils.TimeUtils
import co.softov.morestuff.android.domain.repository.TaskRepository
import co.softov.morestuff.android.domain.createTask
import co.softov.morestuff.android.domain.model.Task
import org.junit.jupiter.api.Assertions.*
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test

class GetTaskUseCaseImplTest {

    @Test
    fun `invoke should return task when task is found`()  {
        val taskRepository: TaskRepository = mockk()
        val getTaskUseCaseImpl = GetTaskUseCaseImpl(taskRepository)
        val taskId = 1L
        val task = createTask()/*Task(1, "Task 1", TimeUtils.nowLocalDateTimeString)*/

        coEvery { taskRepository.getTask(taskId) } returns Either.Right(task)

        runBlocking {
            val result = getTaskUseCaseImpl.invoke(taskId)
            assertEquals(Either.Right(task), result)
        }

    }
}
