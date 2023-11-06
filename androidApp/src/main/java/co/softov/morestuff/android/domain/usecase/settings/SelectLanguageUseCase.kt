package co.softov.morestuff.android.domain.usecase.settings

import arrow.core.Either
import co.softov.morestuff.android.domain.model.Failure
import co.softov.morestuff.android.domain.service.VoiceToTextParser

interface SelectLanguageUseCase {
    operator fun invoke(language: String): Either<Failure, Boolean>
}

class SelectLanguageUseCaseImpl(
    private val voiceToTextParser: VoiceToTextParser
) : SelectLanguageUseCase {

    override fun invoke(language: String): Either<Failure, Boolean> {
        voiceToTextParser.selectLanguage(language)
        return Either.Right(true)
    }
}