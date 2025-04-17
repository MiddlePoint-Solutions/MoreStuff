package io.middlepoint.morestuff.shared.domain.usecase.message

import arrow.core.Either
import arrow.core.right
import io.middlepoint.morestuff.android.domain.createMessageForTest
import io.middlepoint.morestuff.android.domain.createTaskForTest
import io.middlepoint.morestuff.shared.domain.enums.ContentType
import io.middlepoint.morestuff.shared.domain.model.MessageExtra
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
    val task = createTaskForTest()
    val message = createMessageForTest()
    val messageExtra: MessageExtra? = null

    coEvery { getScheduleTaskUseCase(scheduleId) } returns Either.Right(task)
    coEvery {
      createMessageUseCase(
        taskId,
        task.title,
        ContentType.TASK_REMINDER,
        messageExtra,
        scheduleId,
      )
    } returns message.right()

    runBlocking {
      val result = createScheduleMessageUseCaseImpl.invoke(scheduleId)
      assertTrue(result is Either.Right)
      result as Either.Right
      assertEquals(message, result.value)
    }
  }
}






