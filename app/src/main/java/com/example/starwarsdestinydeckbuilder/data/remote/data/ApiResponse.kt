package com.example.starwarsdestinydeckbuilder.data.remote.data

import retrofit2.Response

sealed class ApiResponse<Output> {
    companion object {

        fun <Output> create(error: Throwable): ApiErrorResponse<Output> {
            return ApiErrorResponse(errorMessage = error.message ?: "Unknown error", 0)
        }

        fun <Input, Output> create(response: Response<Input>,  typeConverter: (Input?) -> Output): ApiResponse<Output> {
            return if (response.isSuccessful) {
                val body = typeConverter(response.body())
                val headers = response.headers()
                if (body == null || response.code() == 204) {
                    ApiEmptyResponse()
                } else {
                    ApiSuccessResponse(body, headers)
                }
            } else {
                val msg = response.errorBody()?.string()
                val errorMsg = if (msg.isNullOrEmpty()) {
                    response.message()
                } else {
                    msg
                }
                ApiErrorResponse(
                    errorMsg ?: "Unknown error",
                    response.code()
                )
            }
        }
    }
}

class ApiEmptyResponse<Output> : ApiResponse<Output>()

data class ApiSuccessResponse<Output>(
    val body: Output?,
    val headers: okhttp3.Headers
) : ApiResponse<Output>()

data class ApiErrorResponse<Output>(val errorMessage: String, val statusCode: Int) : ApiResponse<Output>()

