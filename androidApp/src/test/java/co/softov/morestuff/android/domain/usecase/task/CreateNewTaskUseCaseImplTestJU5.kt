package co.softov.morestuff.android.domain.usecase.task

import arrow.core.Either
import co.softov.morestuff.android.domain.repository.TaskRepository
import co.softov.morestuff.android.domain.createTaskForTest
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.runBlocking

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach



class CreateNewTaskUseCaseImplTestJU5{
    private lateinit var taskRepository: TaskRepository
    private lateinit var useCase: CreateNewTaskUseCaseImpl

    @BeforeEach
    fun setup() {
        taskRepository = mockk()
        useCase = CreateNewTaskUseCaseImpl(taskRepository)
    }

    @Test
    fun `create new task use case`() = runBlocking {
        val taskParams = TaskParams("title")
        val task = createTaskForTest()

        coEvery { taskRepository.createTask(taskParams.title) } returns Either.Right(task)

        val result = useCase.invoke(taskParams)
        assertEquals(Either.Right(task), result)
    }
    }














