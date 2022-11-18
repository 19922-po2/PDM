package pt.ipbeja.tp21.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory

/**
 * Extension function to load an image Bitmap from an asset given its [path]
 * Consider the assetsPath function below instead.
 */
@Deprecated(
    "Not recommended",
    replaceWith = ReplaceWith("String#assetPath"),
    level = DeprecationLevel.WARNING
)
fun Context.loadBitmapAsset(path: String): Bitmap {
    val stream = this.assets.open(path)
    return BitmapFactory.decodeStream(stream)
}

/**
 * Extension function to create a full asset path given a relative path
 * Use with Coil's load function on an ImageView: myImageView.load("path/to/img.jpg".assetPath())
 */
fun String.assetPath() = "file:///android_asset/$this"