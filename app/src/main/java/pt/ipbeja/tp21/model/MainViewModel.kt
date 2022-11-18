package pt.ipbeja.tp21.model

import android.content.Context
import androidx.lifecycle.ViewModel
import com.google.android.gms.maps.model.LatLng
import pt.ipbeja.tp21.model.db.Insects
import pt.ipbeja.tp21.model.db.InsectsDB
import pt.ipbeja.tp21.model.db.Observations
import pt.ipbeja.tp21.utils.assetPath
import java.io.File
import java.time.OffsetDateTime

class MainViewModel : ViewModel() {

    //Instantiate keyAccessor
    private val keyAccessor = KeyAccessor

    //Instantiate cameraImage, question, answers, imageAnswers
    private lateinit var cameraImage: File

    // List to save user progress
    private var userQuestions = mutableListOf<Int>(0)
    private var endOfClassification = 0
    private var locationAsked = false

    //Location Coordinates with Default Coordinates in case of the person forguetting to put location on the map
    private var insectsCoordinates: String = LatLng(38.018178, -7.876109).toString()


    fun setInsectsCoordinates(coordinates: String) {
        this.insectsCoordinates = coordinates
        println("Coordenadas = " + this.insectsCoordinates)
    }

    fun getInsectsCoordinates(): String {
        return this.insectsCoordinates
    }

    fun setCameraImage(cameraImage: File) {
        this.cameraImage = cameraImage
        println("MAIN VIEW MODEL  = " + this.cameraImage)
    }

    fun getEndOfClassification(): Int {
        return this.endOfClassification
    }

    fun getCameraImage(): File {
        return this.cameraImage
    }

    fun getQuestion(context: Context): String {
        return keyAccessor.getKey(context).nodes[this.userQuestions.last()].question
    }

    fun getAnswers1(context: Context): String {
        return keyAccessor.getKey(context).nodes[this.userQuestions.last()].options[0].text
    }

    fun getAnswers2(context: Context): String {
        return keyAccessor.getKey(context).nodes[this.userQuestions.last()].options[1].text
    }

    fun getImageAnswers1(context: Context): String {
        return keyAccessor.getKey(context).nodes[this.userQuestions.last()].options[0].imageLocation.assetPath()
    }

    fun getImageAnswers2(context: Context): String {
        return keyAccessor.getKey(context).nodes[this.userQuestions.last()].options[1].imageLocation.assetPath()
    }

    /**
     *  Saves insect classification data in room database
     *  If it's the first time observing the insect, first it is created a new insect and after a new observation related to that insect
     *  If it's not the first time, then its created a new observation only, associated with that insect.
     */
    fun getNextAnswer(context: Context, answerId: Int): Map<String, String> {

        //Get the next Questions to be Shown
        val nextQuestion =
            keyAccessor.getKey(context).nodes[this.userQuestions.last()].options[answerId].goto

        if (nextQuestion[0] == 'R') {
            this.endOfClassification = 1

            return mapOf<String, String>(
                "ordem" to (keyAccessor.getKey(context).results[nextQuestion.substring(
                    1,
                    nextQuestion.length
                ).toInt() - 1].ordem),
                "description" to (keyAccessor.getKey(context).results[nextQuestion.substring(
                    1,
                    nextQuestion.length
                ).toInt() - 1].description),
                "image" to (keyAccessor.getKey(context).results[nextQuestion.substring(
                    1,
                    nextQuestion.length
                ).toInt() - 1].imageLocation.assetPath()),
            )
        } else {
            //Updates Users Questions List
            this.userQuestions.add(nextQuestion.substring(1, nextQuestion.length).toInt() - 1)
            println(this.userQuestions)

            return mapOf<String, String>(
                "question" to (keyAccessor.getKey(context).nodes[nextQuestion.substring(
                    1,
                    nextQuestion.length
                ).toInt() - 1].question),
                "answer1" to keyAccessor.getKey(context).nodes[nextQuestion.substring(
                    1,
                    nextQuestion.length
                ).toInt() - 1].options[0].text,
                "answer2" to keyAccessor.getKey(context).nodes[nextQuestion.substring(
                    1,
                    nextQuestion.length
                ).toInt() - 1].options[1].text,
                "image1" to keyAccessor.getKey(context).nodes[nextQuestion.substring(
                    1,
                    nextQuestion.length
                ).toInt() - 1].options[0].imageLocation,
                "image2" to keyAccessor.getKey(context).nodes[nextQuestion.substring(
                    1,
                    nextQuestion.length
                ).toInt() - 1].options[1].imageLocation,
            )
        }
    }

    /**
     * Resets the User Questions List
     */
    fun resetUserQuestionsList() {
        //reset userQuestions list
        this.userQuestions = mutableListOf<Int>(0)
        //reset classification
        this.endOfClassification = 0
    }

    /**
     *  Saves insect classification data in room database
     *  If it's the first time observing the insect, first it is created a new insect and after a new observation related to that insect
     *  If it's not the first, then its created a new observation only
     */
    fun saveDataToDatabase(
        insectsCoordinates: String?,
        insectDescription: String?,
        insectOrder: String?,
        context: Context
    ) {
        //Calls the function to do reset at the user questions list
        resetUserQuestionsList()

        //gets all type of insects
        val insectOrderList = insectOrder?.let {
            InsectsDB(context)
                .insectsDao()
                .getAllByOrder(insectOrder)
        }

        if (insectOrderList?.size!! < 1) {
            //insert new insect type
            val insect = Insects(insectOrder, insectDescription)
            val lastIdInserted = InsectsDB(context)
                .insectsDao()
                .insertData(insect)

            //insert new observation related to the new insect
            val observation =
                Observations(lastIdInserted, insectsCoordinates, this.cameraImage.path)
            val id = InsectsDB(context)
                .observationsDao()
                .insertData(observation)
        } else {
            //insert new observation
            val observation =
                Observations(insectOrderList[0].insectId, insectsCoordinates, this.cameraImage.path)
            val id = InsectsDB(context)
                .observationsDao()
                .insertData(observation)
        }
    }

    /**
     *  Removes last option selected by the user, when he presses 'back'
     */
    fun goBack() {
        if (this.userQuestions.size > 1) {
            this.userQuestions.removeLast()
            this.endOfClassification = 0
        }
    }

    /**
     * Gets the size of the current list containing the users selected options
     */
    fun getUserQuestionsListSize(): Int {
        return this.userQuestions.size
    }

    /**
     *  Gets all insects
     */
    fun getMyInsects(context: Context): List<Insects> {
        val myInsects = InsectsDB(context)
            .insectsDao()
            .getAll()

        return myInsects
    }

    /**
     *  Gets an observation picture to be used as thumbnail, given the insect ID
     */
    fun getInsectThumbnail(context: Context, insectId: Long): String {
        val thumbnail = InsectsDB(context)
            .observationsDao()
            .getObservationInsectById(insectId)
        return thumbnail.cameraPhoto!!
    }

    /**
     *  Gets an observation picture to be used as thumbnail, given the observation ID
     */
    fun getObservationThumbnail(context: Context, insectId: Long): String {
        val thumbnail = InsectsDB(context)
            .observationsDao()
            .getObservationById(insectId)
        return thumbnail.cameraPhoto!!
    }

    /**
     *  Gets the observation time, given the observation ID
     */
    fun getObservationTimebyID(context: Context, insectId: Long): OffsetDateTime {
        val time = InsectsDB(context)
            .observationsDao()
            .getObservationTimeById(insectId)
        return time.photoHour
    }

    /**
     *  Gets the observation time, given the insect ID
     */
    fun getObservationTime(context: Context, insectId: Long): OffsetDateTime {
        val time = InsectsDB(context)
            .observationsDao()
            .getObservationTime(insectId)
        return time.photoHour
    }

    /**
     *  Gets the location of a specific observation, given the insect ID
     */
    fun getObservationLocation(context: Context, insectId: Long): String {
        val location = InsectsDB(context)
            .observationsDao()
            .getObservationByForeignId(insectId)
        return location.insectCoordinates.toString()
    }

    /**
     *  Gets the location of a specific observation, given the observation ID
     */
    fun getObservationLocationByID(context: Context, insectId: Long): String {
        val location = InsectsDB(context)
            .observationsDao()
            .getObservationById(insectId)
        return location.insectCoordinates.toString()
    }

    /**
     *  Gets the order of a specific insect
     */
    fun getInsectsOrder(context: Context, insectId: Long): String {
        val order = InsectsDB(context)
            .insectsDao()
            .getInsectById(insectId)
        return order.insectOrder.toString()
    }

    /**
     *  Gets the insect order of a specific observation
     */
    fun getObservationOrder(context: Context, observationId: Long): String {
        val order = InsectsDB(context)
            .observationsDao()
            .getObservationOrderById(observationId)
        return order.insectOrder.toString()
    }

    /**
     *  Gets the insect description with a specific ID
     */
    fun getInsectsDescription(context: Context, insectId: Long): String {
        val description = InsectsDB(context)
            .insectsDao()
            .getInsectById(insectId)
        return description.insectDescription.toString()
    }

    /**
     *  Gets the insect description of a specific observation
     */
    fun getObservationDescription(context: Context, observationId: Long): String {
        val description = InsectsDB(context)
            .observationsDao()
            .getObservationOrderById(observationId)
        return description.insectDescription.toString()
    }

    /**
     *  Gets all observations of one specific type of insect
     */
    fun getMyObservationsById(context: Context, insectId: Long): List<Observations> {
        val myObservations = InsectsDB(context)
            .observationsDao()
            .getObservationListById(insectId)

        return myObservations
    }

    /**
     *  Updates de location of a certain observation with a specific ID
     */
    fun updateObservationLocation(context: Context, insectId: Long, coordinates: String) {
        InsectsDB(context)
            .observationsDao()
            .updateObservationLocation(insectId, coordinates)
    }


    /**
     *  Deletes an insect with a specific ID
     */
    fun deleteInsectById(context: Context, insectId: Long) {
        val insect = InsectsDB(context)
            .insectsDao()
            .getInsectById(insectId)

        InsectsDB(context)
            .insectsDao()
            .deleteInsectById(insect)
    }

    /**
     *  Deletes an observation with a specific ID
     */
    fun deleteObservationById(context: Context, insectId: Long) {
        InsectsDB(context)
            .observationsDao()
            .deleteObservationById(insectId)
    }

    /**
     *  Deletes all Observations of the same insect, given the insectID
     */
    fun deleteAllObservationsByInsectId(context: Context, insectId: Long) {
        InsectsDB(context)
            .observationsDao()
            .deleteAllObservationsByInsectId(insectId)
    }

    /**
     * Setter Location Asked
     */
    fun setLocationAsked(permission: Boolean) {
        this.locationAsked = permission
    }

    /**
     * Getter Location Asked
     */
    fun getLocationAsked(): Boolean {
        return this.locationAsked
    }

    /**
     * Returns a
     */
    fun getUserQuestionsList(): MutableList<String> {
        var userQuestionStringList = mutableListOf<String>()
        //this.userQuestions.removeAt(0)
        for (x in this.userQuestions) {
            userQuestionStringList.add("Question ${x + 1}")
        }
        return userQuestionStringList
    }

}