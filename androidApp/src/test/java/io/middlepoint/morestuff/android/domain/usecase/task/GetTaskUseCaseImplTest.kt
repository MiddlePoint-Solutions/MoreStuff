package io.middlepoint.morestuff.android.domain.usecase.task

import arrow.core.Either
import io.middlepoint.morestuff.android.domain.repository.TaskRepository
import io.middlepoint.morestuff.android.domain.createTaskForTest
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
        val task = createTaskForTest()/*Task(1, "Task 1", TimeUtils.nowLocalDateTimeString)*/

        coEvery { taskRepository.getTask(taskId) } returns Either.Right(task)

        runBlocking {
            val result = getTaskUseCaseImpl.invoke(taskId)
            assertEquals(Either.Right(task), result)
        }

    }
}
