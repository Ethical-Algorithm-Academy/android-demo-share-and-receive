package eu.jobernas.demoshareapp

import android.util.Log
import androidx.annotation.MainThread
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import java.util.concurrent.atomic.AtomicBoolean

/**
 * Single event live data
 *
 * @param T
 * @constructor Create empty Single event live data
 */
class SingleEventLiveData<T>:
        MutableLiveData<T?>(){

    companion object {
        const val TAG = "SEI_DATA"
    }

    private val hasBeenHandled = AtomicBoolean(true)

    override fun setValue(value: T?) {
        Log.d(TAG, "setValue::executed")
        hasBeenHandled.set(false)
        super.setValue(value)
    }

    @MainThread
    override fun observe(owner: LifecycleOwner, observer: Observer<in T?>) {
        Log.d(TAG, "observe1::executed")
        if (hasActiveObservers())
            Log.w(TAG, "Has Multiple registered but only one will be Notified of Changes")

        super.observe(owner) { t ->
            if (hasBeenHandled.compareAndSet(false, true)) {
                Log.d(TAG, "observe2::executed")
                observer.onChanged(t)
            }
        }
    }

    /**
     * Public Methods
     */

    /**
     * Used for cases where T is Void, to make calls cleaner.
     */
    @MainThread
    fun call() {
        Log.d(TAG, "call::executed")
        value = null
    }
}