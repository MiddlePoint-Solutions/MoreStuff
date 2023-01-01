package co.softov.morestuff.android.domain.redux.middleware

import co.softov.morestuff.android.domain.enums.Priority
import co.softov.morestuff.android.domain.model.Task
import co.softov.morestuff.android.domain.redux.AppState
import co.softov.morestuff.android.domain.redux.middleware.ReminderAction.CreateTask
import co.softov.morestuff.android.domain.testActionDispatch
import kotlinx.coroutines.runBlocking
import org.junit.Test
import org.koin.test.KoinTest
import org.koin.test.get

class TaskMiddlewareTest : KoinTest {

    /*@get:Rule
    val koinTestRule = KoinTestRule.create {

        val driver = JdbcSqliteDriver(JdbcSqliteDriver.IN_MEMORY)
        StuffDb.Schema.create(driver)

        modules(
            storeModule,
            module {
                single(override = true) { StuffDb(driver) }
                single<TaskRepository> { TaskRepositoryImpl(get(), makeTaskDbMapper()) }
            }
        )
    }

    @get:Rule
    val mockProvider = MockProviderRule.create { clazz ->
        Mockito.mock(clazz.java)
    }*/

    private val title = "Test"
    private val testTask = Task(0, title, "")

    @Test
    fun `should inject my components`() {
        runBlocking {

            // TODO: Test if task repository can be used with sqldelight in memory database
//            val mockRepository = declareMock<TaskRepository>()
//            whenever(mockRepository.createTask(title)).thenReturn(FailureResult(TaskDoesNotExist))


//            val createTaskMock = declareMock<CreateTaskUseCase>()
//            given(createTaskMock.invoke(TaskParams(title))).will { SuccessResult(testTask) }

            val middleware = get<TaskMiddleware>()

            val priority = Priority.Today()
            val action = CreateTask(title)
            val expected = ReminderAction.TaskCreatedAction(testTask, priority)
            middleware.testActionDispatch(AppState(), action, expected)
        }


    }
}