package io.middlepoint.morestuff.shared.data.repository

import com.aallam.openai.api.chat.ChatCompletion
import com.aallam.openai.api.chat.ChatCompletionRequest
import com.aallam.openai.api.chat.ChatMessage
import com.aallam.openai.api.chat.ChatRole
import com.aallam.openai.api.chat.StreamOptions
import com.aallam.openai.api.http.Timeout
import com.aallam.openai.api.image.ImageCreation
import com.aallam.openai.api.image.ImageSize
import com.aallam.openai.api.logging.LogLevel
import com.aallam.openai.api.model.ModelId
import com.aallam.openai.client.LoggingConfig
import com.aallam.openai.client.OpenAI
import com.aallam.openai.client.OpenAIConfig
import com.aallam.openai.client.OpenAIHost
import com.russhwolf.settings.Settings
import com.russhwolf.settings.get
import com.russhwolf.settings.set
import io.middlepoint.morestuff.android.data.Constants.IA_MODEL_DEEPSEEK
import io.middlepoint.morestuff.android.data.Constants.KEY_API_KEY
import io.middlepoint.morestuff.shared.domain.repository.LlmRepository
import io.middlepoint.morestuff.shared.domain.service.logger
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlin.time.Duration.Companion.seconds

class LlmRepositoryImpl(
  private val settings: Settings,
) : LlmRepository {

  private var storedApiKey: String?
    get() = settings[KEY_API_KEY]
    set(value) {
      settings[KEY_API_KEY] = value
    }

  override fun saveApiKey(apiKey: String) {
    this.storedApiKey = apiKey
  }

  override fun getApiKey(): String? {
    return storedApiKey
  }


/*  private val openAI by lazy {
    val config = OpenAIConfig(
      token = storedApiKey ?: throw IllegalStateException("API key not found"),
      timeout = Timeout(socket = 60.seconds),
    )
    OpenAI(config)
  }*/

  private val openAI by lazy {
    val config = OpenAIConfig(
      token = storedApiKey ?: throw IllegalStateException("API key not found"),
      logging = LoggingConfig(LogLevel.Headers), timeout = Timeout(socket = 60.seconds),
      host = OpenAIHost(baseUrl = "https://api.deepseek.com/v1/")
    )
    OpenAI(config)
  }


  //TODO: test assistants
/*  @OptIn(BetaOpenAI::class)
  val assistant = openAI.assistant(
    request = AssistantRequest(
      name = "Math Tutor",
      tools = listOf(AssistantTool.CodeInterpreter),
      model = ModelId(IA_MODEL)
    )
  )*/

  override suspend fun generateChatCompletion(prompt: String): String {
    val chatCompletionRequest = ChatCompletionRequest(
      model = ModelId(IA_MODEL_DEEPSEEK),
      messages = listOf(
        ChatMessage(
          role = ChatRole.Assistant,
          content = "You are a helpful assistant!"
        ),
        ChatMessage(
          role = ChatRole.User,
          content = prompt
        )
      )
    )

    val completion: ChatCompletion = openAI.chatCompletion(chatCompletionRequest)
    return completion.choices.first().message.content ?: "No response"
  }

  override suspend fun generateImage(prompt: String): String {
    val images = openAI.imageURL(
      creation = ImageCreation(
        prompt = prompt,
        n = 1,
        size = ImageSize.is1024x1024
      )
    )

    return images.first().url
  }

  override fun streamChatCompletion(prompt: String): Flow<String> {
    val streamOptions = StreamOptions()
    val chatCompletionRequest = ChatCompletionRequest(
      model = ModelId(IA_MODEL_DEEPSEEK),
      messages = listOf(
        ChatMessage(
          role = ChatRole.User,
          content = prompt
        )
      ),
      streamOptions = streamOptions
    )

    return openAI.chatCompletions(chatCompletionRequest)
      .map { completion ->
        completion.choices.first().delta?.content ?: ""
      }
  }

  companion object {
    const val ENCRYPTED_DATABASE_NAME = "ENCRYPTED_SETTINGS"
    const val encryptedSettingsName = "encryptedSettings"
  }
}