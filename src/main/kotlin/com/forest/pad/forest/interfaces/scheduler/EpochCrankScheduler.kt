package com.forest.pad.forest.interfaces.scheduler

import com.forest.pad.forest.domain.spl.StakePoolService
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import java.util.concurrent.TimeUnit

@Component
class EpochCrankScheduler(
    private val stakePoolService: StakePoolService
) {
    @Scheduled(fixedDelay = 1L, timeUnit = TimeUnit.DAYS)
    fun epochCrank() {
        stakePoolService.update()
    }
}
