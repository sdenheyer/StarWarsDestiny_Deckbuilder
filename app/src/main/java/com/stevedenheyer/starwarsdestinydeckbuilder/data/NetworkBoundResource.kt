package com.stevedenheyer.starwarsdestinydeckbuilder.data

import android.util.Log
import com.stevedenheyer.starwarsdestinydeckbuilder.data.remote.data.ApiErrorResponse
import com.stevedenheyer.starwarsdestinydeckbuilder.data.remote.data.ApiResponse
import com.stevedenheyer.starwarsdestinydeckbuilder.data.remote.data.ApiSuccessResponse
import com.stevedenheyer.starwarsdestinydeckbuilder.domain.data.Resource
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

inline fun <DB, REMOTE> networkBoundResource(
    crossinline fetchFromLocal: () -> Flow<DB>,
    crossinline shouldFetchFromRemote: (DB?) -> Boolean = { true },
   // crossinline getTimestamp: (DB?) -> Long = { 0L },   Leaving this off for now - 304 not sent?
    crossinline fetchFromRemote: () -> Flow<ApiResponse<REMOTE>>,
    crossinline processRemoteResponse:(response: ApiSuccessResponse<REMOTE>) -> Unit,
    crossinline saveRemoteData: (REMOTE) -> Unit,
    crossinline onFetchFailed: (errorBody: String?, statusCode: Int) -> Unit
    ) = flow<Resource<DB>> {
       // Log.d("SWD", "Network resource flows initializing...")

        emit(Resource.loading(null))

    val localData = fetchFromLocal().first()

    if (shouldFetchFromRemote(localData)) {
        Log.d("SWD", "Fetching from remote...")
        emit(Resource.loading(localData))

        fetchFromRemote().collect { apiResponse ->
            when (apiResponse) {
                is ApiSuccessResponse -> {
                    processRemoteResponse(apiResponse)
                    apiResponse.body?.let {
                      //  Log.d("SWD", "Saving to db: $it.size")
                        saveRemoteData(it) }
                    emitAll(fetchFromLocal().map { dbData ->
                      //  Log.d("SWD", "Getting from db: $dbData.size")
                        Resource.success(dbData)
                    })
                }

                is ApiErrorResponse -> {
                    onFetchFailed(apiResponse.errorMessage, apiResponse.statusCode)
                    emitAll(fetchFromLocal().map {
                        Resource.error(
                            apiResponse.errorMessage,
                            it
                        )
                    })
                }
                else -> {

                }
            }

        }
    } else {
        Log.d("SWD", "Fetch from local")
        emitAll(fetchFromLocal().map {
          //  Log.d("SWD", "Fetch local output, $it")
            Resource.success(isFromDB = true, data = it) })
    }
}