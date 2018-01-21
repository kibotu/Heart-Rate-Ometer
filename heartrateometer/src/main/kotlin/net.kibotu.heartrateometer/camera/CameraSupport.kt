package de.charite.balsam.utils.camera

import android.hardware.Camera
import android.util.Log
import android.view.SurfaceHolder
import net.kibotu.heartrateometer.HeartRateOmeter

abstract class CameraSupport {

    abstract fun open(cameraId: Int): CameraSupport

    /**
     * [Camera.CameraInfo.html#orientation](https://developer.android.com/reference/android/hardware/Camera.CameraInfo.html#orientation)
     *
     * 0, 90, 180, 270
     */
    abstract fun getOrientation(cameraId: Int): @CameraOrientation Int

    abstract fun setDisplayOrientation(orientation: @CameraOrientation Int)

    open var parameters: Camera.Parameters? = null

    abstract fun setPreviewDisplay(holder: SurfaceHolder?)

    abstract fun setPreviewCallback(previewCallback: Camera.PreviewCallback?)

    abstract fun startPreview()

    abstract fun stopPreview()

    abstract fun release()

    abstract fun hasFlash(): Boolean

    abstract fun setFlash(flashMode: Int): Boolean

    protected fun log(throwable: Throwable) {
        if (HeartRateOmeter.enableLogging)
            throwable.printStackTrace()
    }

    protected fun log(message: String?) {
        if (HeartRateOmeter.enableLogging)
            Log.d("HeartRateOmeter", "" + message)
    }
}