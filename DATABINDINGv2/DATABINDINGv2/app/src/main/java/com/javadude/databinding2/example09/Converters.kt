package com.javadude.databinding2.example09

import androidx.room.TypeConverter

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