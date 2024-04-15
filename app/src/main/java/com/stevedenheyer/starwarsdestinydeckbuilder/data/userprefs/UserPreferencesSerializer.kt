package com.stevedenheyer.starwarsdestinydeckbuilder.data.userprefs

import androidx.datastore.core.CorruptionException
import androidx.datastore.core.Serializer
import com.google.protobuf.InvalidProtocolBufferException
import com.stevedenheyer.starwarsdestinydeckbuilder.UserSettings
import java.io.InputStream
import java.io.OutputStream


object UserPreferencesSerializer : Serializer<UserSettings>   {
    override val defaultValue: UserSettings = UserSettings.getDefaultInstance()
    override suspend fun readFrom(input: InputStream): UserSettings {
        try {
            return UserSettings.parseFrom(input)
        } catch (e: InvalidProtocolBufferException) {
            throw CorruptionException("Cannot read proto.", e)
        }
    }

    override suspend fun writeTo(t: UserSettings, output: OutputStream) = t.writeTo(output)
}
