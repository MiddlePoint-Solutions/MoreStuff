package io.middlepoint.morestuff.android.domain.usecase.scope

import io.middlepoint.morestuff.shared.domain.repository.ScopeRepository
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test

class DeleteScopeUseCaseTest {
    private val scopeRepository: ScopeRepository = mockk(relaxed = true)
    private val useCase: DeleteScopeUseCase = DeleteScopeUseCaseImpl(scopeRepository)

    @Test
    fun `delete scope`() = runBlocking {
        val scopeId = 1L

        useCase(scopeId)

        coVerify(exactly = 1) { scopeRepository.deleteScope(scopeId) }
    }
}
