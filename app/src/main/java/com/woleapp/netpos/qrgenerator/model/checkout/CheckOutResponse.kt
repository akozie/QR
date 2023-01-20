package com.woleapp.netpos.qrgenerator.model.checkout

data class CheckOutResponse(
    val amount: String,
    val customerId: String,
    val domain: String,
    val merchantId: String,
    val status: String,
    val transId: String
)