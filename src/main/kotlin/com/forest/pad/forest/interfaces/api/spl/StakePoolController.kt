package com.forest.pad.forest.interfaces.api.spl

import com.forest.pad.forest.domain.spl.StakePoolService
import com.forest.pad.forest.interfaces.api.spl.req.StakePoolReq
import com.forest.pad.forest.interfaces.api.spl.res.StakePoolRes
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping(value = ["v1/api/spl"])
class StakePoolController(
    private val stakePoolService: StakePoolService
) {
    @PostMapping("/stake-pool/mint")
    fun tokenMint(@RequestBody req: StakePoolReq) : StakePoolRes {
        return stakePoolService.tokenMint(req)
    }
}