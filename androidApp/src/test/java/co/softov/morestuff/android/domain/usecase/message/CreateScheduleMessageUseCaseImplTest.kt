package co.softov.morestuff.android.domain.usecase.message
import arrow.core.Either
import arrow.core.right
import co.softov.morestuff.android.domain.createMessage
import co.softov.morestuff.android.domain.createSchedule
import co.softov.morestuff.android.domain.createTask
import co.softov.morestuff.android.domain.enums.ContentType
import co.softov.morestuff.android.domain.enums.ReplyType
import co.softov.morestuff.android.domain.model.Message
import co.softov.morestuff.android.domain.model.Schedule
import co.softov.morestuff.android.domain.model.Task
import co.softov.morestuff.android.domain.usecase.task.GetScheduleTaskUseCase
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test



class CreateScheduleMessageUseCaseImplTest{

    @Test
    fun`should return message when schedule is created`(){
        val getScheduleTaskUseCase: GetScheduleTaskUseCase = mockk()
        val createMessageUseCase: CreateMessageUseCase = mockk()
        val createScheduleMessageUseCaseImpl = CreateScheduleMessageUseCaseImpl(
            getScheduleTaskUseCase,
            createMessageUseCase
        )
        val scheduleId = 1L
        val taskId = 1L
        val schedule = createSchedule()
        val task = createTask()
        val message = createMessage()

        coEvery { getScheduleTaskUseCase(scheduleId) } returns Either.Right(task)
        coEvery { createMessageUseCase(taskId, scheduleId, task.title, ContentType.TASK_REMINDER) } returns message.right()

        runBlocking {
            val result = createScheduleMessageUseCaseImpl(scheduleId)
            assertTrue(result is Either.Right)
            result as Either.Right
            assertEquals(message, result.value)
            }
        }


    }






