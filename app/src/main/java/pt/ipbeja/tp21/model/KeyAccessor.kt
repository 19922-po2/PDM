package pt.ipbeja.tp21.model

import android.content.Context
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream
import pt.ipbeja.tp21.R

/**
 * @author Diogo Pina Manique
 * @version 27/11/2021
 */

class KeyAccessor {

    companion object {

        private var keyFile: String? = null

        @Volatile
        private var INSTANCE: Key? = null

        fun getKey(context: Context): Key = synchronized(this) {
            val file = context.resources.getString(R.string.key_file)
            //val file = context.resources.getString(R.string.key_file)
            if (file != keyFile) {
                keyFile = file
                INSTANCE = parse(context)
            }
            return@synchronized INSTANCE!!
        }

        private fun parse(context: Context): Key {
            val inputStream = context.assets.open(keyFile!!)
            return Json.decodeFromStream(inputStream)
        }
    }
}