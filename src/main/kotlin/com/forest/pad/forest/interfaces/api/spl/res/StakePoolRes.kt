package com.forest.pad.forest.interfaces.api.spl.res

data class StakePoolRes(
    val msg: String,
    val tokenAddress: String? = null,
    val metadataAddress: String? = null,
    val transactionId: String? = null
)
