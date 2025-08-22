package io.middlepoint.morestuff.android.domain.usecase.message

import arrow.core.right
import io.middlepoint.morestuff.android.domain.createMessageForTest
import io.middlepoint.morestuff.shared.domain.enums.ContentType
import io.middlepoint.morestuff.shared.domain.model.core.MessageExtra
import io.middlepoint.morestuff.shared.domain.model.Uuid
import io.middlepoint.morestuff.shared.domain.repository.MessageRepository
import io.middlepoint.morestuff.shared.domain.usecase.message.CheckForUrlMetadataUseCase
import io.middlepoint.morestuff.shared.domain.usecase.message.CreateMessageUseCaseImpl
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test


class CreateMessageUseCaseImplTest {
  @Test
  fun `should return created message`() {
    val messageRepository: MessageRepository = mockk()
    val checkForUrlMetadataUseCase: CheckForUrlMetadataUseCase = mockk()
    val createMessageUseCaseImpl =
      CreateMessageUseCaseImpl(messageRepository, checkForUrlMetadataUseCase)
    val taskId = Uuid("1")
    val scheduleId = Uuid("1")
    val title = "Test Message"
    val contentType = ContentType.CONFIRM_TASK
    val messageExtra: MessageExtra? = null
    val expectedMessage = createMessageForTest()

    coEvery {
      messageRepository.createMessage(
        any(),
        any(),
        contentType.value,
        any(),
        messageExtra,
        title
      )
    } returns expectedMessage.right()
    coEvery { checkForUrlMetadataUseCase(title, any()) } returns Unit.right()

    runBlocking {
      val result =
        createMessageUseCaseImpl.invoke(taskId, title, contentType, messageExtra, scheduleId)
      assertEquals(expectedMessage.right(), result)
    }
  }
}

