package com.myexample.customizabletextresumegenerator

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface ResumeService {
    @GET("resume")
    suspend fun getResumeData(@Query("name") name:String) : Response<ResumeData>
}