package com.forest.pad.forest.interfaces.api.sol.req

data class SolMintReq(
    val name: String,
    val symbol: String,
    val uri: String,
    val sellerFeeBasisPoints: String,
    val creators: String? = null
)