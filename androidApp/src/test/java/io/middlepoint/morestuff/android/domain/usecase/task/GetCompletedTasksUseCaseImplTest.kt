package io.middlepoint.morestuff.android.domain.usecase.task

import io.middlepoint.morestuff.android.domain.model.TaskDomain
import io.middlepoint.morestuff.android.domain.repository.TaskRepository
import io.middlepoint.morestuff.android.domain.createListOfTasks
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test


class GetCompletedTasksUseCaseImplTest {

    @Test
    fun `get completed task impl test`() {
        val mockTaskRepository: TaskRepository = mockk()
        val getCompletedTasksImpl = GetCompletedTasksUseCaseImpl(mockTaskRepository)
        val completeTasks: List<TaskDomain> = createListOfTasks(2)

        coEvery { mockTaskRepository.getCompleteTasksFlow() } returns flowOf(completeTasks)

        runBlocking {
            val resultList = getCompletedTasksImpl.invoke()
            Assertions.assertEquals(completeTasks, resultList.first())
    }
}
}


