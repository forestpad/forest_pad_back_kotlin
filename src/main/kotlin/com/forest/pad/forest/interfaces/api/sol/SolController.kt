package com.forest.pad.forest.interfaces.api.sol

import com.forest.pad.forest.interfaces.api.sol.req.SolMintReq
import com.forest.pad.forest.interfaces.api.sol.res.SolMinRes
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping(value = ["v1/api/sol"])
class SolController {
    @PostMapping("/mint")
    fun tokenMint(@RequestBody req: SolMintReq) : SolMinRes {
        return SolMinRes("mock")
    }
}