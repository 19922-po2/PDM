package pt.ipbeja.tp21

import android.app.Application
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.util.Log
import pt.ipbeja.tp21.remote.AppAPI
import pt.ipbeja.tp21.utils.TAG


/**
 * @author Diogo Pina Manique
 * @version 27/11/2021
 */

class TPApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        initApi()
    }


    /**
     * Initialize the http client. Do not modify this function.
     * Não modificar esta função
     */
    private fun initApi() {
        // Não mexer / Do not touch :)
        val ai: ApplicationInfo = applicationContext.packageManager
            .getApplicationInfo(applicationContext.packageName, PackageManager.GET_META_DATA)
        val token = ai.metaData["accessToken"] as String

        if (token.count { it == '.' } != 2) {
            Log.w(
                TAG, "Missing or invalid API KEY. Check local.properties file. " +
                        "API KEY inválida ou em falta. Verifique o ficheiro local.properties"
            )
        }
        AppAPI.init(token) // Não apagar esta linha. Do not delete this line.
    }
}