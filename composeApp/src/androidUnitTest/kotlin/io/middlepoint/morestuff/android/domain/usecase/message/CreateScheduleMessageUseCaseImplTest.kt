package io.middlepoint.morestuff.android.domain.usecase.message

import arrow.core.Either
import arrow.core.right
import io.middlepoint.morestuff.android.domain.createMessageForTest
import io.middlepoint.morestuff.android.domain.createTaskForTest
import io.middlepoint.morestuff.shared.domain.enums.ContentType
import io.middlepoint.morestuff.shared.domain.model.MessageExtra
import io.middlepoint.morestuff.shared.domain.model.Uuid
import io.middlepoint.morestuff.shared.domain.model.core.Message
import io.middlepoint.morestuff.shared.domain.usecase.message.CreateMessageUseCase
import io.middlepoint.morestuff.shared.domain.usecase.message.CreateScheduleMessageUseCaseImpl
import io.middlepoint.morestuff.shared.domain.usecase.task.GetTaskForScheduleUseCase
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test


class CreateScheduleMessageUseCaseImplTest {

  @Test
  fun `should return message when schedule is created`() = runTest {
    val getScheduleTaskUseCase: GetTaskForScheduleUseCase = mockk(relaxed = true)
    val createMessageUseCase: CreateMessageUseCase = mockk(relaxed = true)
    val createScheduleMessageUseCaseImpl = CreateScheduleMessageUseCaseImpl(
      getScheduleTaskUseCase,
      createMessageUseCase
    )
    val scheduleId = Uuid("1")
    val taskId = Uuid("1")
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

    createScheduleMessageUseCaseImpl(scheduleId).onRight {
      assertEquals(message, it)
    }
  }
}






