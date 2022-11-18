package pt.ipbeja.tp21.model.db

import androidx.room.*
import java.time.OffsetDateTime

@Entity
data class Insects(
    val insectOrder: String?,
    val insectDescription: String?,
    @PrimaryKey(autoGenerate = true) val insectId: Long = 0,
)

@Entity//(foreignKeys = @ForeignKey(entity = Observations.class, parentColumns = "insectIdForeingKey", childColumns = "insectId"))
data class Observations(
    val insectIdForeingKey: Long,
    val insectCoordinates: String?,
    val cameraPhoto: String?,
    val photoHour: OffsetDateTime = OffsetDateTime.now(),
    @PrimaryKey(autoGenerate = true) val insectObservationsId: Long = 0,
)

data class InsectsObservations(
    @Embedded val insect: Insects,
    @Relation(
        parentColumn = "insectId",
        entityColumn = "insectIdForeingKey"
    )
    val insectObservationList: List<Observations>
)