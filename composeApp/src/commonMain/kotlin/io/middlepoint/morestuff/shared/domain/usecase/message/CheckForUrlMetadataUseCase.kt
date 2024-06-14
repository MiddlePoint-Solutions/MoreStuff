package io.middlepoint.morestuff.shared.domain.usecase.message

import arrow.core.Either
import io.middlepoint.morestuff.shared.domain.model.Failure
import io.middlepoint.morestuff.shared.domain.model.OpenGraphMetadataFetchFailure
import io.middlepoint.morestuff.shared.domain.repository.MessageRepository

interface CheckForUrlMetadataUseCase {
  suspend operator fun invoke(
    title: String,
    messageId: Long
  ): Either<Failure, Unit>
}

class CheckForUrlMetadataUseCaseImpl(
  private val messageRepository: MessageRepository,
  private val fetchOpenGraphMetadataUseCase: FetchOpenGraphMetadataUseCase,
) : CheckForUrlMetadataUseCase {

  private val urlPattern =
    "(https?://|www\\.|[a-zA-Z0-9_-]+\\.)?[a-zA-Z0-9_-]+(\\.[a-zA-Z]+)+([\\w.,@?^=%&:/~+#-]*[\\w@?^=%&/~+#-])?"
      .toRegex(setOf(RegexOption.IGNORE_CASE, RegexOption.MULTILINE))

  override suspend fun invoke(
    title: String,
    messageId: Long
  ): Either<Failure, Unit> {

    val firstUrl = findFirstUrl(title, urlPattern)
    return if (firstUrl != null) {
      val (url, isSecureUrl) = firstUrl
      if (isSecureUrl) {
        val openGraphResult = fetchOpenGraphMetadataUseCase(url)
        if (openGraphResult != null) {
          messageRepository.insertUrlMetadata(url, openGraphResult, messageId)
          Either.Right(Unit)
        } else {
          Either.Left(OpenGraphMetadataFetchFailure("Failure to fetch OpenGraph metadata"))
        }
      } else {
        Either.Right(Unit)
      }
    } else {
      Either.Right(Unit)
    }
  }

  private fun findFirstUrl(content: String, urlPattern: Regex): Pair<String, Boolean>? =
    urlPattern.find(content)?.let { result ->
      var url = result.value
      val isComplete = url.startsWith("http://") || url.startsWith("https://")
      if (!isComplete) {
        url = "https://$url"
      }
      Pair(url, isComplete)
    }
}
