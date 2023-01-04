package eu.jobernas.demoshareapp

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory

/**
 * TODO: Move to Core Module;
 * Id to bitmap
 *
 * @param context
 * @return
 */
fun Int.idToBitmap(context: Context?): Bitmap =
        BitmapFactory.decodeResource(context?.resources, this)