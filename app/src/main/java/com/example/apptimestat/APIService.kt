package com.example.apptimestat

import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface APIService {
    @POST("/")
    suspend fun sendData(@Body requestBody: RequestBody): Response<ResponseBody>

    @GET("/")
    suspend fun getData(
        @Query("user_id") userId: String?,
        @Query("date_range") dateRange: Int?
    ): Response<ResponseBody>
}