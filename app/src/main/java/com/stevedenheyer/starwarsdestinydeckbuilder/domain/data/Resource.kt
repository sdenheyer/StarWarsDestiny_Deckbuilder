package com.stevedenheyer.starwarsdestinydeckbuilder.domain.data

data class Resource<out Output>(
    val status: Status,
    val data: Output?,
    val message: String?
) {

    enum class Status {
        SUCCESS,
        LOADING,
        ERROR
    }

    companion object {
        fun <Output> success(data: Output?): Resource<Output> {
            return Resource(
                Status.SUCCESS,
                data,
                null
            )
        }

        fun <Output> error(msg: String, data: Output? = null): Resource<Output> {
            return Resource(
                Status.ERROR,
                data,
                msg
            )
        }

        fun <Output> loading(data: Output? = null): Resource<Output> {
            return Resource(
                Status.LOADING,
                data,
                null
            )
        }
    }
}
