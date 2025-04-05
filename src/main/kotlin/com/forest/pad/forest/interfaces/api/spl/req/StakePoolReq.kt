package com.forest.pad.forest.interfaces.api.spl.req

data class StakePoolReq(
    val name: String,
    val symbol: String,
    val uri: String,
    val originTokenAddress: String,
    val sellerFeeBasisPoints: Long,
    val creators: String? = null
)
