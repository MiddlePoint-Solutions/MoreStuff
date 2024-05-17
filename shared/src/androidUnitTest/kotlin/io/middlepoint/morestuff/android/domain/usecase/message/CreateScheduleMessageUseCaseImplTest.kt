package io.middlepoint.morestuff.android.domain.usecase.message

import arrow.core.Either
import arrow.core.right
import io.middlepoint.morestuff.shared.domain.enums.ContentType
import io.middlepoint.morestuff.shared.domain.model.MessageData
import io.middlepoint.morestuff.shared.domain.usecase.message.CreateMessageUseCase
import io.middlepoint.morestuff.shared.domain.usecase.message.CreateScheduleMessageUseCaseImpl
import io.middlepoint.morestuff.shared.domain.usecase.task.GetTaskForScheduleUseCase
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test


class CreateScheduleMessageUseCaseImplTest {

  @Test
  fun `should return message when schedule is created`() {
    val getScheduleTaskUseCase: GetTaskForScheduleUseCase = mockk()
    val createMessageUseCase: CreateMessageUseCase = mockk()
    val createScheduleMessageUseCaseImpl = CreateScheduleMessageUseCaseImpl(
      getScheduleTaskUseCase,
      createMessageUseCase
    )
    val scheduleId = 1L
    val taskId = 1L
    val task = io.middlepoint.morestuff.android.domain.createTaskForTest()
    val message = io.middlepoint.morestuff.android.domain.createMessageForTest()
    val messageData: MessageData? = null

    coEvery { getScheduleTaskUseCase(scheduleId) } returns Either.Right(task)
    coEvery {
      createMessageUseCase(
        taskId,
        task.title,
        ContentType.TASK_REMINDER,
        messageData,
        scheduleId,
      )
    } returns message.right()

    runBlocking {
      val result = createScheduleMessageUseCaseImpl(scheduleId)
      assertTrue(result is Either.Right)
      result as Either.Right
      assertEquals(message, result.value)
    }
  }
}






