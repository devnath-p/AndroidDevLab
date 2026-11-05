package com.example.labexam2

data class Trip(
    val id: Int,
    val title: String,
    val date: String,
    val notes: String,
    val latitude: Double,
    val longitude: Double
)

data class TripPhoto(
    val id: Int,
    val tripId: Int,
    val uri: String
)