package eu.jobernas.demoreceiveapp

import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Parcelable
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import eu.jobernas.androidextensions.convertToImageJPEG
import eu.jobernas.androidextensions.pastImageFromClipboard

class MainViewModel: ViewModel(),
        View.OnClickListener {

    companion object {
        private const val TAG = "MAIN_VIEW_MODEL"
    }

    val onImageReceived: MutableLiveData<Uri> = MutableLiveData()

    /**
     * Handle image
     *
     * @param context
     * @param intent
     */
    fun handleImage(context: Context?, intent: Intent?) {
        when(intent?.action) {
            Intent.ACTION_SEND -> {
                if ("text/plain" == intent.type) {
                    handleSendText(intent) // Handle text being sent
                } else if (intent.type?.startsWith("image/") == true) {
                    handleSendImage(intent) // Handle single image being sent
                    val name = intent.getStringExtra("Name")
                    val age = intent.getIntExtra("Age", 0)
                    val nationality = intent.getStringExtra("Nationality")
                    Log.d(TAG, "name: $name, age: $age, nationality: $nationality")
                }
            }
            Intent.ACTION_SEND_MULTIPLE -> {
                if (intent.type?.startsWith("image/") == true)
                    handleSendMultipleImages(intent) // Handle multiple images being sent
                val name = intent.getStringExtra("Name")
                val age = intent.getStringExtra("Age")
                val nationality = intent.getStringExtra("Nationality")
                Log.d(TAG, "name: $name, age: $age, nationality: $nationality")
            }
            Intent.ACTION_VIEW -> {
                // Add Schema here
                handleAppSchemaImage(context, intent)
            }
            else -> {
                // Handle other intents, such as being started from the home screen
            }
        }
    }

    /**
     * Handle send text
     *
     * @param intent
     */
    private fun handleSendText(intent: Intent) {
        intent.getStringExtra(Intent.EXTRA_TEXT)?.let {
            // Update UI to reflect text being shared
        }
    }

    /**
     * Handle send image
     *
     * @param intent
     */
    private fun handleSendImage(intent: Intent) {
        val uri = intent.parcelable<Parcelable>(Intent.EXTRA_STREAM) as? Uri
        if (uri != null)
            onImageReceived.postValue(uri)
    }

    /**
     * Handle send multiple images
     *
     * @param intent
     */
    private fun handleSendMultipleImages(intent: Intent) {
        val uris = intent.parcelableArrayList<Parcelable>(Intent.EXTRA_STREAM)?.mapNotNull { it as? Uri }
        val firstImage = uris?.firstOrNull()
        if (firstImage != null)
            onImageReceived.postValue(firstImage)
        // Ignore the remaining images
    }

    /**
     * Handle app schema image
     *
     * @param intent
     */
    private fun handleAppSchemaImage(context: Context?, intent: Intent) {
        val dataUri: Uri? = intent.data
        Log.d(TAG, "dataUri::scheme:${dataUri?.scheme}::host:${dataUri?.host}")
        if (dataUri != null && dataUri.scheme == "demo_receive_app" && dataUri.host == "share_image") {
            // Note: Check for schema, host and after for Image Uri to see if everything matchs up
            val imageBitmap = dataUri.getQueryParameter("imageUri")?.convertToImageJPEG(context, "share_image")
            Log.d(TAG, "imageUri::${imageBitmap}")
            // TODO: Post Bitmap Image
//            if (imageBitmap != null)
//                onImageReceived.postValue(imageUri)
        }
    }

    override fun onClick(v: View?) {
        val context = v?.context
        when(v?.id) {
            R.id.demo_receive_button -> {
                try {
                    val imageUri = context?.pastImageFromClipboard()
                    Log.d(TAG, "Image in Clipboard: ${imageUri?.path}")
                    if (imageUri != null)
                        onImageReceived.postValue(imageUri)
                } catch (t: Throwable) {
                    Log.e(TAG, "Error", t)
                    Toast.makeText(context, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

}