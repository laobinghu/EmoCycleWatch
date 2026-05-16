package top.eaverse.emocycle.watch.data.local

import androidx.room.TypeConverter
import top.eaverse.emocycle.watch.model.Trigger

class Converters {
    @TypeConverter
    fun fromTriggers(triggers: List<Trigger>): String {
        return triggers.joinToString(separator = ",") { it.name }
    }

    @TypeConverter
    fun toTriggers(value: String): List<Trigger> {
        if (value.isBlank()) return emptyList()
        return value.split(',').mapNotNull { name ->
            runCatching { Trigger.valueOf(name) }.getOrNull()
        }
    }
}
