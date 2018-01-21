package de.charite.balsam.utils.camera

import android.annotation.SuppressLint
import android.content.Context
import android.hardware.Camera
import android.hardware.camera2.CameraAccessException
import android.hardware.camera2.CameraCharacteristics
import android.hardware.camera2.CameraDevice
import android.hardware.camera2.CameraManager
import android.os.Build
import android.support.annotation.RequiresApi
import android.view.SurfaceHolder

@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
class CameraPostLolipop internal constructor(context: Context) : CameraSupport() {

    private var camera: CameraDevice? = null

    private var cameraIndex: Int = 0

    private var cameraId: String? = null

    private val manager: CameraManager = context.getSystemService(Context.CAMERA_SERVICE) as CameraManager

    @SuppressLint("MissingPermission")
    override fun open(cameraId: Int): CameraSupport {
        try {
            val cameraIds = manager.cameraIdList
            this.cameraIndex = cameraId
            this.cameraId = cameraIds[cameraId]
            manager.openCamera(this.cameraId, object : CameraDevice.StateCallback() {
                override fun onOpened(camera: CameraDevice) {
                    this@CameraPostLolipop.camera = camera
                }

                override fun onDisconnected(camera: CameraDevice) {
                    this@CameraPostLolipop.camera = camera
                }

                override fun onError(camera: CameraDevice, error: Int) {
                    this@CameraPostLolipop.camera = camera
                    log(error.toString())
                }
            }, null)
        } catch (e: Exception) {
            log(e)
        }

        return this
    }

    override fun getOrientation(cameraId: Int): @CameraOrientation Int {
        return try {
            val cameraIds = manager.cameraIdList
            val characteristics = manager.getCameraCharacteristics(cameraIds[cameraId])
            characteristics.get(CameraCharacteristics.SENSOR_ORIENTATION)!!
        } catch (e: CameraAccessException) {
            log(e)
            0
        }
    }

    override fun setDisplayOrientation(orientation: @CameraOrientation Int) {
    }

    override fun setPreviewDisplay(holder: SurfaceHolder?) {

    }

    override fun setPreviewCallback(previewCallback: Camera.PreviewCallback?) {
    }

    override fun startPreview() {
    }

    override fun stopPreview() {
    }

    override fun release() {
    }

    override fun hasFlash(): Boolean {
        return false
    }

    var flashLightEnabled: Boolean = false

    override fun setFlash(flashMode: Int): Boolean {

        val enabled = !flashLightEnabled

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                manager.setTorchMode(cameraId, enabled)
                flashLightEnabled = enabled
            }
        } catch (e: CameraAccessException) {
            log(e)
        }

        return flashLightEnabled
    }
}