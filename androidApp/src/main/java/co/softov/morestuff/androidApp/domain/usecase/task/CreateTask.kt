package co.softov.morestuff.androidApp.domain.usecase.task

import co.softov.morestuff.androidApp.domain.enums.Priority
import co.softov.morestuff.androidApp.domain.model.Result
import co.softov.morestuff.androidApp.domain.model.SimpleResult
import co.softov.morestuff.androidApp.domain.repository.TaskRepository
import co.softov.morestuff.androidApp.domain.usecase.message.ConfirmParams
import co.softov.morestuff.androidApp.domain.usecase.message.CreateTaskConfirmationMessage
import co.softov.morestuff.androidApp.domain.usecase.message.CreateTaskMessage
import co.softov.morestuff.androidApp.domain.usecase.schedule.CreateSchedule

interface CreateTask {
    suspend operator fun invoke(params: TaskParams): SimpleResult<Boolean>
}

data class TaskParams(val title: String, val priority: Priority)

class CreateNewTaskImpl(
    private val taskRepository: TaskRepository,
    private val createTaskMessage: CreateTaskMessage,
    private val createConfirmationMessage: CreateTaskConfirmationMessage,
    private val createSchedule: CreateSchedule
) : CreateTask {

    override suspend fun invoke(params: TaskParams): SimpleResult<Boolean> {
        return when (val result = taskRepository.createTask(params.title)) {
            is Result.Failure -> result
            is Result.Success -> {
                createTaskMessage(result.value, params.title)
                createConfirmationMessage(ConfirmParams(result.value, params.priority))
                createSchedule(result.value, params.priority)
                Result.Success(true)
            }
        }
    }
} 



