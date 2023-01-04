package eu.jobernas.demoshareapp

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.app.ShareCompat
import androidx.lifecycle.ViewModel
import eu.jobernas.androidextensions.convertToJpegStringBase64
import eu.jobernas.androidextensions.copyToClipboard
import eu.jobernas.androidextensions.createTemporaryImage

/**
 * Share view model
 *
 * @constructor Create empty Share view model
 */
class ShareViewModel: ViewModel(),
    View.OnClickListener {

    val onErrorThrowable: SingleEventLiveData<Throwable> = SingleEventLiveData()

    companion object {
        private const val TAG = "SHARE_VM"
    }

    override fun onClick(v: View?) {
        val context = v?.context
        val bitmap = R.drawable.img_sample_passport.idToBitmap(v?.context)
        when(v?.id) {
            R.id.share_with_native_share_button -> shareVia(context, bitmap)
            R.id.share_with_clip_board_button -> shareClipboard(context, bitmap)
            R.id.share_with_universal_link_button -> shareUniversalLink(context, bitmap)
            R.id.share_with_native_share_to_target_button -> shareViaToDemoReceiveApp(context, bitmap)
        }
    }

    /**
     * Share via
     *
     */
    private fun shareVia(context: Context?, bitmap: Bitmap) {
        val contextNotNull = context ?: return
        val stream = bitmap.createTemporaryImage(contextNotNull, "jpg", "images")
        Log.d(TAG, "Stream is: $stream")
        ShareCompat.IntentBuilder(contextNotNull)
            .setSubject("Demo Share App")
            .setText("Sharing an Bitmap of a JPEG image.")
            .setType("image/jpeg")
            .setStream(stream)
            .startChooser()
    }

    /**
     * Share via to demo receive app
     * Note: for setClassName, use the package name and the MainActivity path
     *
     * @param context
     * @param bitmap
     */
    private fun shareViaToDemoReceiveApp(context: Context?, bitmap: Bitmap) {
        val contextNotNull = context ?: return
        val streamUri = bitmap.createTemporaryImage(contextNotNull, "jpg", "images")
        val sharingIntent = Intent(Intent.ACTION_SEND).apply {
            setClassName("eu.jobernas.demoreceiveapp","eu.jobernas.demoreceiveapp.MainActivity")
            putExtra(Intent.EXTRA_STREAM, streamUri)
            type = "image/jpeg"
        }
        contextNotNull.startActivity(sharingIntent)
    }

    /**
     * Share universal link
     * NOTE: Base64 Images will not work because it crashes the app, with the error
     * Caused by: android.os.TransactionTooLargeException: data parcel size 12424068 bytes ...
     *
     * NOTE: To share things like and Big image is not possible
     */
    private fun shareUniversalLink(context: Context?, bitmap: Bitmap) {
        val imageBase64 = bitmap.convertToJpegStringBase64()
        Log.d(TAG, "Image Length::${imageBase64.length}")
        val contextNotNull = context ?: return
        val uri = Uri.parse("demo_receive_app://share_image")
                    .buildUpon()
                    .appendQueryParameter("imageUri", imageBase64)
                    .build()
        val intent = Intent(Intent.ACTION_VIEW, uri)
        try {
            contextNotNull.startActivity(intent)
        } catch (t: Throwable) {
            Log.e(TAG, "Error", t)
            onErrorThrowable.postValue(t)
        }
    }

    /**
     * Share clipboard
     *
     */
    private fun shareClipboard(context: Context?, bitmap: Bitmap) {
        val contextNotNull = context ?: return
        bitmap.copyToClipboard(contextNotNull, "jpg", "images")
        // Give Feedback to the User
        Toast.makeText(contextNotNull, R.string.lb_image_copied, Toast.LENGTH_SHORT).show()
    }

}