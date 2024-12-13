package com.example.driverattentiveness.data.api.response

import com.google.gson.annotations.SerializedName

data class CreateTripResponse(

	@field:SerializedName("data")
	val data: Data,

	@field:SerializedName("message")
	val message: String
)

data class Data(

	@field:SerializedName("start_location")
	val startLocation: String,

	@field:SerializedName("start_time")
	val startTime: String,

	@field:SerializedName("trip_status")
	val tripStatus: String,

	@field:SerializedName("user_id")
	val userId: Int,

	@field:SerializedName("id")
	val id: Int
)
