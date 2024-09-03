package com.stevedenheyer.starwarsdestinydeckbuilder.data

import android.util.Log
import com.stevedenheyer.starwarsdestinydeckbuilder.data.remote.data.ApiEmptyResponse
import com.stevedenheyer.starwarsdestinydeckbuilder.data.remote.data.ApiErrorResponse
import com.stevedenheyer.starwarsdestinydeckbuilder.data.remote.data.ApiResponse
import com.stevedenheyer.starwarsdestinydeckbuilder.data.remote.data.ApiSuccessResponse
import com.stevedenheyer.starwarsdestinydeckbuilder.domain.data.Resource
import com.stevedenheyer.starwarsdestinydeckbuilder.utils.DEFAULT_EXPIRY
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

inline fun <DB, REMOTE> networkBoundResource(
    crossinline fetchFromLocal: suspend () -> Flow<DB>,
    crossinline shouldFetchFromRemote: suspend (DB?) -> Boolean = { true },
    crossinline fetchFromRemote: suspend (DB?) -> Flow<ApiResponse<REMOTE>>,
    crossinline updateTimestamp: suspend (DB?) -> Unit,
    crossinline saveRemoteData: suspend (REMOTE, maxAge: Long?) -> Unit,
    crossinline onFetchFailed: (errorBody: String?, statusCode: Int) -> Unit
) = flow<Resource<DB>> {
    // Log.d("SWD", "Network resource flows initializing...")

    emit(Resource.loading(null))

    val localData = try { fetchFromLocal().first() } catch (e: NoSuchElementException) { null }

    if (shouldFetchFromRemote(localData)) {
      //  Log.d("SWD", "Fetching from remote...")
        emit(Resource.loading(localData))

        fetchFromRemote(localData).collect { apiResponse ->
            when (val state = apiResponse) {
                is ApiSuccessResponse -> {

                    val maxAge = state.headers.get("Cache-Control")?.split("=")?.last()?.toLong()

                    val expiry = if (maxAge != null) { maxAge * 1000 } else { DEFAULT_EXPIRY }
                  //  Log.d("SWD", "Max-age header: $maxAge Names: $map")
                    state.body?.let {
                        //  Log.d("SWD", "Saving to db: $it.size")
                        saveRemoteData(it, expiry)
                    }
                    emitAll(fetchFromLocal().map { dbData ->
                        if (state.body is Collection<*> && dbData is Collection<*>) {
                            if (state.body.size <= dbData.size) {
                                Resource.success(dbData)
                            } else {
                                Resource.loading(dbData)
                            }
                        } else {
                            //  Log.d("SWD", "Getting from db: $dbData.size")
                            Resource.success(dbData)
                        }
                    })
                }

                is ApiErrorResponse -> {
                  //  Log.d("SWD", "Headers: ${state.statusCode} ${state.errorMessage}")
                    onFetchFailed(state.errorMessage, state.statusCode)
                    emitAll(fetchFromLocal().map {
                        Resource.error(
                            state.errorMessage,
                            it
                        )
                    })
                }

                is ApiEmptyResponse -> {
                 //   Log.d("SWD", "Api Empty response....")
                    updateTimestamp(localData)
                    emitAll(fetchFromLocal().map {
                        Resource.success(it, true)
                    })
                }
            }

        }
    } else {
      //  Log.d("SWD", "Fetch from local")
        emitAll(fetchFromLocal().map {
            //  Log.d("SWD", "Fetch local output, $it")
            Resource.success(isFromDB = true, data = it)
        })
    }
}