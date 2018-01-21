package de.charite.balsam.utils.camera

import android.hardware.Camera
import android.view.SurfaceHolder

class CameraPreLolipop : CameraSupport() {

    private var camera: Camera? = null

    override var parameters: Camera.Parameters?
        get() = camera?.parameters
        set(value) {
            camera?.parameters = value
        }

    override fun open(cameraId: Int): CameraSupport {
        this.camera = Camera.open(cameraId)
        return this
    }

    override fun getOrientation(cameraId: Int): @CameraOrientation Int {
        val info = Camera.CameraInfo()
        Camera.getCameraInfo(cameraId, info)
        return info.orientation
    }

    override fun setDisplayOrientation(orientation: @CameraOrientation Int) {
        camera?.setDisplayOrientation(orientation)
    }

    override fun setPreviewDisplay(holder: SurfaceHolder?) {
        camera?.setPreviewDisplay(holder)
    }

    override fun setPreviewCallback(previewCallback: Camera.PreviewCallback?) {
        camera?.setPreviewCallback(previewCallback)
    }

    override fun startPreview() {
        camera?.startPreview()
    }

    override fun stopPreview() {
        camera?.stopPreview()
    }

    override fun release() {
        camera?.release()
    }

    override fun hasFlash(): Boolean {
        if (camera == null) {
            return false
        }

        if (parameters?.flashMode == null) {
            return false
        }

        val supportedFlashModes = parameters?.supportedFlashModes
        return !(supportedFlashModes == null
                || supportedFlashModes.isEmpty()
                || supportedFlashModes.size == 1 && supportedFlashModes[0] == Camera.Parameters.FLASH_MODE_OFF)
    }

    override fun setFlash(flashMode: Int): Boolean {
        if (camera == null) {
            return false
        }

        if (parameters?.flashMode == null) {
            return false
        }

        val supportedFlashModes = parameters?.supportedFlashModes
        if (flashMode == 1 && supportedFlashModes?.get(2) == Camera.Parameters.FLASH_MODE_AUTO) {
            parameters?.flashMode = Camera.Parameters.FLASH_MODE_AUTO
        } else if (flashMode == 2 && supportedFlashModes?.get(1) == Camera.Parameters.FLASH_MODE_ON) {
            parameters?.flashMode = Camera.Parameters.FLASH_MODE_ON
        } else if (flashMode == 3 && supportedFlashModes?.get(0) == Camera.Parameters.FLASH_MODE_OFF) {
            parameters?.flashMode = Camera.Parameters.FLASH_MODE_OFF
        }
        parameters = parameters
        return true
    }
}