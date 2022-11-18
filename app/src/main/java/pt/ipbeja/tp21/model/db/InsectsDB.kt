package pt.ipbeja.tp21.model.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(entities = [Insects::class, Observations::class], version = 1, exportSchema = false)
@TypeConverters(OffsetDateTimeConverters::class, LocalDateConverters::class)
abstract class InsectsDB : RoomDatabase() {

    abstract fun insectsDao(): InsectsDao
    abstract fun observationsDao(): ObservationsDao

    companion object {
        @Volatile
        private var instance: InsectsDB? = null
        private val LOCK = Any()

        operator fun invoke(context: Context) = instance ?: synchronized(LOCK) {
            instance ?: buildDatabase(context.applicationContext).also { instance = it }
        }

        private fun buildDatabase(context: Context) =
            Room.databaseBuilder(context.applicationContext, InsectsDB::class.java, "insects.db")
                .fallbackToDestructiveMigration()
                .allowMainThreadQueries()
                .build()
    }
}
