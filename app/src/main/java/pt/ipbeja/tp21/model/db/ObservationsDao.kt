package pt.ipbeja.tp21.model.db

import androidx.room.*

@Dao
abstract class ObservationsDao {

    @Query("Select * from observations")
    abstract fun getAll(): List<Observations>

    @Query("Select * from observations where insectObservationsId = :id")
    abstract fun getObservationById(id: Long): Observations

    @Query("Select * from observations where insectIdForeingKey = :id")
    abstract fun getObservationInsectById(id: Long): Observations

    @Query("Select * from observations where insectIdForeingKey = :id")
    abstract fun getObservationByForeignId(id: Long): Observations

    @Query("Select * from insects, observations where insectId = insectIdForeingKey and insectObservationsId = :id")
    abstract fun getObservationOrderById(id: Long): Insects

    @Query("Select * from observations where insectIdForeingKey = :id")
    abstract fun getObservationListById(id: Long): List<Observations>

    @Query("Select * from observations where insectObservationsId = :id")
    abstract fun getObservationTimeById(id: Long): Observations

    @Query("Select * from observations where insectIdForeingKey = :id")
    abstract fun getObservationTime(id: Long): Observations

    @Insert
    abstract fun insertData(observation: Observations): Long //The Last Id Inserted

    @Query("Select * from observations where insectObservationsId = :id")
    abstract fun getObservationThumbnail(id: Long): Observations

    @Query("Update observations SET insectCoordinates = :coordinates Where insectObservationsId = :id")
    abstract fun updateObservationLocation(id: Long, coordinates: String)

    @Query("Delete From Observations Where insectObservationsId = :id")
    abstract fun deleteObservationById(id: Long): Int
    //@Delete
    //abstract fun deleteObservationById(observation: Observations)

    @Query("Delete From Observations Where insectIdForeingKey = :id")
    abstract fun deleteAllObservationsByInsectId(id: Long): Int
}
