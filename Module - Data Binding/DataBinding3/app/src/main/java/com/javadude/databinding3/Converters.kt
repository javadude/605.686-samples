package com.javadude.databinding3

import androidx.room.TypeConverter

/**
 * Room type conversions for the State enumeration (storing values as Strings)
 */
class Converters {
    companion object {
        @JvmStatic
        @TypeConverter
        fun fromString(value: String): State {
            return State.valueOf(value)
        }

        @JvmStatic
        @TypeConverter
        fun stateToString(state: State): String {
            return state.name
        }
    }
}