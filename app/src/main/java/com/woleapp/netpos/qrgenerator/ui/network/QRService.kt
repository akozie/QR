package com.woleapp.netpos.qrgenerator.ui.network

import com.woleapp.netpos.qrgenerator.ui.model.*
import io.reactivex.Single
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface QRService {

    @POST("auth/login")
    fun login(
        @Body loginRequest: LoginRequest
    ): Single<LoginResponse>

    @POST("auth/register")
    fun register(
        @Body registerRequest: RegisterRequest
    ): Single<GeneralResponse>

    @POST("qr")
    fun generateQR(
        @Body generateQR: QrModelRequest
    ): Single<GenerateQRResponse>

    @GET("qr/cardschemes")
    fun getCardSchemes(
    ): Single<CardSchemeResponse>

    @GET("qr/banks")
    fun getCardBanks(
    ): Single<BankCardResponse>

}