package co.softov.morestuff.android.domain.usecase.schedule

/*
class CreateScheduleUseCaseImplTest {
    private val scheduleRepository = mockk<ScheduleRepository>()
    private val timeManager = mockk<TimeManager>()
    private val createScheduleUseCaseImpl = CreateScheduleUseCaseImpl(
        scheduleRepository,
        timeManager
    )

    @Test
    fun `creates schedule `() = runBlocking {
        val taskId = 1L
        val priority = Priority.today
        val scheduleTimeLocal = ""
        //val scheduleTimeUtc: String = ""
        val schedule = createScheduleForTest()

        coEvery { timeManager.getPriorityTime(priority) } returns scheduleTimeLocal
        coEvery { scheduleRepository.createSchedule(taskId, scheduleTimeLocal ) } returns Either.Right(
            schedule)

        val result = createScheduleUseCaseImpl.invoke(taskId, priority)

        assertEquals(Either.Right(schedule), result)
        coVerify { timeManager.getPriorityTime(priority) }
        coVerify { scheduleRepository.createSchedule(taskId, scheduleTimeLocal) }
    }

}*/
