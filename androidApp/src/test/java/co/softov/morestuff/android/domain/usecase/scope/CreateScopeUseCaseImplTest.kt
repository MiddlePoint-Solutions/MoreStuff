package co.softov.morestuff.android.domain.usecase.scope

import co.softov.morestuff.android.domain.repository.ScopeRepository
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test

class CreateScopeUseCaseTest {
    private val scopeRepository: ScopeRepository = mockk(relaxed = true)
    private val useCase: CreateScopeUseCase = CreateScopeUseCaseImpl(scopeRepository)

    @Test
    fun `create scope`() = runBlocking {
        val scopeUid = "uniqueScopeId"
        val name = "Test Scope"

        useCase(scopeUid, name)

        coVerify(exactly = 1) { scopeRepository.createScope(scopeUid, name) }
    }
}
