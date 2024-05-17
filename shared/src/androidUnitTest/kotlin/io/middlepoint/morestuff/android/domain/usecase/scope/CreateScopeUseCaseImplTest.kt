package io.middlepoint.morestuff.android.domain.usecase.scope

import io.middlepoint.morestuff.shared.domain.repository.ScopeRepository
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test

class CreateScopeUseCaseTest {
    private val scopeRepository: ScopeRepository = mockk(relaxed = true)
    private val useCase: CreateScopeUseCase = CreateScopeUseCaseImpl(scopeRepository)

    @Test
    fun `create scope`() = runBlocking {
        val name = "Test Scope"
        useCase(name)

        coVerify(exactly = 1) { scopeRepository.createScope(name) }
    }
}
