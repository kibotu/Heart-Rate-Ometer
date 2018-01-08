package net.kibotu.heartrateometer

import android.content.Context
import android.hardware.Camera
import android.os.PowerManager
import android.util.Log
import android.view.SurfaceHolder
import android.view.SurfaceView
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject
import org.apache.commons.collections4.queue.CircularFifoQueue
import java.lang.ref.WeakReference
import java.util.concurrent.atomic.AtomicBoolean

/**
 * Created by <a href="https://about.me/janrabe">Jan Rabe</a>.
 */
open class HeartRateOmeter {

    private val TAG: String = javaClass.simpleName

    var enableDebug: Boolean = false

    var wakeLockTimeOut: Long = 10_000

    protected var surfaceHolder: SurfaceHolder? = null

    protected var camera: Camera? = null

    protected var wakelock: PowerManager.WakeLock? = null

    protected var previewCallback: Camera.PreviewCallback? = null

    protected var surfaceCallback: SurfaceHolder.Callback? = null

    protected val publishSubject: PublishSubject<Int>

    protected var context: WeakReference<Context>? = null

    init {
        previewCallback = createCameraPreviewCallback()
        surfaceCallback = createSurfaceHolderCallback()
        publishSubject = PublishSubject.create<Int>()
    }

    fun bpmUpdates(context: Context, surfaceView: SurfaceView): Observable<Int> {
        return bpmUpdates(context, surfaceView.holder)
    }

    fun bpmUpdates(context: Context, surfaceHolder: SurfaceHolder): Observable<Int> {
        this.context = WeakReference(context)
        this.surfaceHolder = surfaceHolder
        return publishSubject
                .doOnSubscribe {
                    publishSubject.onNext(-1)
                    start()
                }
                .doOnDispose { cleanUp() }
    }

    protected fun start() {
        log("start")
        wakelock = (context?.get()?.getSystemService(Context.POWER_SERVICE) as PowerManager)
                .newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, TAG)

        wakelock?.acquire(wakeLockTimeOut)
        camera = Camera.open()

        surfaceHolder?.addCallback(surfaceCallback)
        surfaceHolder?.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS)
    }

    protected fun setCameraParameter(width: Int, height: Int) {
        val parameters = camera?.parameters
        parameters?.flashMode = Camera.Parameters.FLASH_MODE_TORCH

        if (parameters?.maxExposureCompensation != parameters?.minExposureCompensation) {
            parameters?.exposureCompensation = 0
        }
        if (parameters?.isAutoExposureLockSupported == true) {
            parameters.autoExposureLock = true
        }
        if (parameters?.isAutoWhiteBalanceLockSupported == true) {
            parameters.autoWhiteBalanceLock = true
        }

        camera?.parameters = parameters

        getSmallestPreviewSize(width, height, parameters)?.let {
            parameters?.setPreviewSize(it.width, it.height)
            log("Using width ${it.width} and height ${it.height}")
        }
        camera?.parameters = parameters
    }

    protected fun createSurfaceHolderCallback(): SurfaceHolder.Callback {
        return object : SurfaceHolder.Callback {

            override fun surfaceCreated(holder: SurfaceHolder) {
                try {
                    camera?.setPreviewDisplay(surfaceHolder)
                    camera?.setPreviewCallback(previewCallback)
                } catch (throwable: Throwable) {
                    if (enableDebug)
                        throwable.printStackTrace()
                }
            }

            override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {
                setCameraParameter(width, height)
                camera?.startPreview()
            }

            override fun surfaceDestroyed(holder: SurfaceHolder) {}
        }
    }

    protected fun createCameraPreviewCallback(): Camera.PreviewCallback {
        return object : Camera.PreviewCallback {

            val PROCESSING = AtomicBoolean(false)
            val sampleSize = 256
            var counter = 0
            var bpm: Int = -1

            val fft = FFT(sampleSize)

            val sampleQueue = CircularFifoQueue<Double>(sampleSize)
            val timeQueue = CircularFifoQueue<Long>(sampleSize)
            val bpmQueue = CircularFifoQueue<Int>(40)

            override fun onPreviewFrame(data: ByteArray?, camera: Camera?) {

                if (data == null) {
                    log("Data is null!")
                    return
                }

                if (camera == null) {
                    log("Camera is null!")
                    return
                }

                val size = camera.parameters.previewSize
                if (size == null) {
                    log("Size is null!")
                    return
                }

                if (!PROCESSING.compareAndSet(false, true)) {
                    log("Have to return...")
                    return
                }

                val width = size.width
                val height = size.height


                // todo check if height/width is mixed up on purpose, might be landscape/portrait issue
                val imgAvg = ImageProcessing.decodeYUV420SPtoRedAvg(data.clone(), width, height)

                sampleQueue.add(imgAvg.toDouble())
                timeQueue.add(System.currentTimeMillis())

                val y = DoubleArray(sampleSize)
                val x = toPrimitive(sampleQueue.toArray(arrayOfNulls<Double>(0)) as Array<Double>)
                val time = toPrimitive(timeQueue.toArray(arrayOfNulls<Long>(0)) as Array<Long>)

                if (timeQueue.size < sampleSize) {
                    PROCESSING.set(false)

                    return
                }

                val Fs = timeQueue.size.toDouble() / (time!![timeQueue.size - 1] - time[0]).toDouble() * 1000

                fft.fft(x!!, y)

                val low = Math.round(((sampleSize * 40).toDouble() / 60.0 / Fs).toFloat())
                val high = Math.round(((sampleSize * 160).toDouble() / 60.0 / Fs).toFloat())

                var bestI = 0
                var bestV = 0.0
                for (i in low until high) {
                    val value = Math.sqrt(x[i] * x[i] + y[i] * y[i])

                    if (value > bestV) {
                        bestV = value
                        bestI = i
                    }
                }

                bpm = Math.round((bestI.toDouble() * Fs * 60.0 / sampleSize).toFloat())
                bpmQueue.add(bpm)

                // log("bpm=$bpm")

                publishSubject.onNext(bpm)

                counter++

                PROCESSING.set(false)
            }
        }
    }

    /**
     * An empty immutable `long` array.
     */
    protected val EMPTY_LONG_ARRAY = LongArray(0)

    /**
     *
     * Converts an array of object Longs to primitives.
     *
     *
     * This method returns `null` for a `null` input array.
     *
     * @param array  a `Long` array, may be `null`
     * @return a `long` array, `null` if null array input
     * @throws NullPointerException if array content is `null`
     */
    protected fun toPrimitive(array: Array<Long>?): LongArray? {
        if (array == null) {
            return null
        } else if (array.isEmpty()) {
            return EMPTY_LONG_ARRAY
        }
        val result = LongArray(array.size)
        for (i in array.indices) {
            result[i] = array[i]
        }
        return result
    }

    /**
     * An empty immutable `double` array.
     */
    protected val EMPTY_DOUBLE_ARRAY = DoubleArray(0)

    /**
     *
     * Converts an array of object Doubles to primitives.
     *
     *
     * This method returns `null` for a `null` input array.
     *
     * @param array  a `Double` array, may be `null`
     * @return a `double` array, `null` if null array input
     * @throws NullPointerException if array content is `null`
     */
    protected fun toPrimitive(array: Array<Double>?): DoubleArray? {
        if (array == null) {
            return null
        } else if (array.isEmpty()) {
            return EMPTY_DOUBLE_ARRAY
        }
        val result = DoubleArray(array.size)
        for (i in array.indices) {
            result[i] = array[i]
        }
        return result
    }

    protected fun getSmallestPreviewSize(width: Int, height: Int, parameters: Camera.Parameters?): Camera.Size? {

        var result: Camera.Size? = null

        parameters?.supportedPreviewSizes?.let {
            it
                    .asSequence()
                    .filter { it.width <= width && it.height <= height }
                    .forEach {
                        if (result == null) {
                            result = it
                        } else {
                            if (it.width * it.height < result!!.width * result!!.height)
                                result = it
                        }
                    }
        }

        return result
    }

    protected fun cleanUp() {
        log("cleanUp")

        if (wakelock?.isHeld == true) {
            wakelock?.release()
        }

        camera?.apply {
            setPreviewCallback(null)
            stopPreview()
            release()
        }

        camera = null
    }

    private fun log(message: String?) {
        if (enableDebug)
            Log.d(TAG, "" + message)
    }
}