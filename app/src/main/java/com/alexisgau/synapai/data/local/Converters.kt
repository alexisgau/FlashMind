package com.alexisgau.synapai.data.local

import androidx.room.TypeConverter
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class Converters {

    private val json = Json { ignoreUnknownKeys = true }

    @TypeConverter
    fun fromStringList(options: List<String>): String {
        // Converts List<String> to a JSON String
        return json.encodeToString(options)
    }

    @TypeConverter
    fun toStringList(optionsJson: String): List<String> {
        // Converts a JSON String back to List<String>
        return json.decodeFromString(optionsJson)
    }
}