package com.stevedenheyer.starwarsdestinydeckbuilder.domain.data

data class Resource<out Output>(
    val status: Status,
    val data: Output?,
    val isFromDB: Boolean,
    val message: String?
) {

    enum class Status {
        SUCCESS,
        LOADING,
        ERROR
    }

    companion object {
        fun <Output> success(data: Output?, isFromDB: Boolean = false): Resource<Output> {
            return Resource(
                Status.SUCCESS,
                data,
                isFromDB,
                null
            )
        }

        fun <Output> error(msg: String, data: Output? = null): Resource<Output> {
            return Resource(
                Status.ERROR,
                data,
                false,
                msg
            )
        }

        fun <Output> loading(data: Output? = null): Resource<Output> {
            return Resource(
                Status.LOADING,
                data,
                false,
                null
            )
        }
    }
}
