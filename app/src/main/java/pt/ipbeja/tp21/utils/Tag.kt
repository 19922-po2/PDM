package pt.ipbeja.tp21.utils

/**
 * Extension property to easily obtain a String tag for logging purposes
 */
val Any.TAG: String
    get() = this::class.java.simpleName.let { if (it.length > 23) it.substring(0, 23) else it }