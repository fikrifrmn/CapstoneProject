package com.example.driverattentiveness.data.api.response

import com.google.gson.annotations.SerializedName

data class TripResponse(

	@field:SerializedName("data")
	val data: Data,

	@field:SerializedName("message")
	val message: String
)

data class DataTrip(

	@field:SerializedName("start_location")
	val startLocation: String,

	@field:SerializedName("start_time")
	val startTime: String,

	@field:SerializedName("trip_status")
	val tripStatus: String,

	@field:SerializedName("user_id")
	val userId: Int,

	@field:SerializedName("end_time")
	val endTime: String,

	@field:SerializedName("end_location")
	val endLocation: String,

	@field:SerializedName("id")
	val id: Int
)
