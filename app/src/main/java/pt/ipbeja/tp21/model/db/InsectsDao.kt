package pt.ipbeja.tp21.model.db

import androidx.room.*

@Dao
abstract class InsectsDao {

    @Query("Select * from insects")
    abstract fun getAll(): List<Insects>

    @Query("Select * from insects where insectOrder = :order")
    abstract fun getAllByOrder(order: String): List<Insects>

    @Insert
    abstract fun insertData(insects: Insects): Long //The Last Id Inserted

    @Transaction
    @Query("SELECT * FROM insects, observations")
    abstract fun getInsectsObservationslists(): List<InsectsObservations>

    @Query("Select * from insects where insectId = :id")
    abstract fun getInsectById(id: Long): Insects

    //@Query("Delete From insects Where insectId = :id")
    //abstract fun deleteInsectById(id: Long): Int
    @Delete
    abstract fun deleteInsectById(insect: Insects)
}