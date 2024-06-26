package com.example.studymentor.network

import com.example.studymentor.model.Tutor
import retrofit2.Call
import retrofit2.http.GET

interface TutorService {

    @GET("/api/tutors")
    fun getTutors(): Call<List<Tutor>>
}
