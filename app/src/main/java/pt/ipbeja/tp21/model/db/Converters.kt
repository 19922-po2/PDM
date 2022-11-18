package pt.ipbeja.tp21.model.db

import androidx.room.TypeConverter
import java.time.LocalDate
import java.time.OffsetDateTime
import java.util.*

/**
 * Just as an example. You shouldn't use the util.Date API
 * Consider using the new java.time API (check the other two converters)
 */
class DateConverters {
    @TypeConverter
    fun fromTimestamp(value: Long): Date {

        return Date(value)
    }

    @TypeConverter
    fun dateToTimestamp(date: Date): Long {
        return date.time
    }
}

class LocalDateConverters {
    @TypeConverter
    fun fromTimestamp(value: String): LocalDate {
        return LocalDate.parse(value)
    }

    @TypeConverter
    fun dateToTimestamp(date: LocalDate): String {
        return date.toString()
    }
}

class OffsetDateTimeConverters {
    @TypeConverter
    fun fromTimestamp(value: String): OffsetDateTime {
        return OffsetDateTime.parse(value)
    }

    @TypeConverter
    fun dateToTimestamp(time: OffsetDateTime): String {
        return time.toString()
    }

}