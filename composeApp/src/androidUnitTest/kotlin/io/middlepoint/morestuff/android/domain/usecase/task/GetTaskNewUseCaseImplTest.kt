package io.middlepoint.morestuff.android.domain.usecase.task

import arrow.core.Either
import io.middlepoint.morestuff.android.domain.createTaskForTest
import io.middlepoint.morestuff.shared.data.utils.generate
import io.middlepoint.morestuff.shared.domain.model.Uuid
import io.middlepoint.morestuff.shared.domain.repository.TaskRepository
import io.middlepoint.morestuff.shared.domain.usecase.task.GetTaskUseCaseImpl
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class GetTaskNewUseCaseImplTest {

    @Test
    fun `invoke should return task when task is found`()  {
        val taskRepository: TaskRepository = mockk()
        val getTaskUseCaseImpl = GetTaskUseCaseImpl(taskRepository)
        val taskId = Uuid.generate()
        val task = createTaskForTest(id = taskId)

        coEvery { taskRepository.getTask(taskId) } returns Either.Right(task)

        runBlocking {
            val result = getTaskUseCaseImpl.invoke(taskId)
            Assertions.assertEquals(Either.Right(task), result)
        }

    }
}
