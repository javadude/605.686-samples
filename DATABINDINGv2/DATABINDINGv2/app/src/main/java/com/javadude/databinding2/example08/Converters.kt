package com.javadude.databinding2.example08

import android.arch.persistence.room.TypeConverter

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