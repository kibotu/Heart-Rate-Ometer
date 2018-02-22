package net.kibotu.heartrateometer

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.util.Log
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.activity_main.*
import net.kibotu.heartrateometer.app.R
import net.kibotu.kalmanrx.jama.Matrix
import net.kibotu.kalmanrx.jkalman.JKalman

class MainActivity : AppCompatActivity() {

    var subscription: CompositeDisposable? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

    }

    private fun startWithPermissionCheck() {
        if (!hasPermission(Manifest.permission.CAMERA)) {
            checkPermissions(REQUEST_CAMERA_PERMISSION, Manifest.permission.CAMERA)
            return
        }

        val kalman = JKalman(2, 1)

        // measurement [x]
        val m = Matrix(1, 1)

        // transitions for x, dx
        val tr = arrayOf(doubleArrayOf(1.0, 0.0), doubleArrayOf(0.0, 1.0))
        kalman.transition_matrix = Matrix(tr)

        // 1s somewhere?
        kalman.error_cov_post = kalman.error_cov_post.identity()


        val bpmUpdates = HeartRateOmeter()
                .withAverageAfterSeconds(3)
                .setFingerDetectionListener(this::onFingerChange)
                .bpmUpdates(preview)
                .subscribe({

                    if (it.value == 0)
                        return@subscribe

                    m.set(0, 0, it.value.toDouble())

                    // state [x, dx]
                    val s = kalman.Predict()

                    // corrected state [x, dx]
                    val c = kalman.Correct(m)

                    val bpm = it.copy(value = c.get(0, 0).toInt())
                    Log.v("HeartRateOmeter", "[onBpm] ${it.value} => ${bpm.value}")
                    onBpm(bpm)
                }, Throwable::printStackTrace)

        subscription?.add(bpmUpdates)
    }

    @SuppressLint("SetTextI18n")
    private fun onBpm(bpm: HeartRateOmeter.Bpm) {
        // Log.v("HeartRateOmeter", "[onBpm] $bpm")
        label.text = "$bpm bpm"
    }

    private fun onFingerChange(fingerDetected: Boolean){
         finger.text = "$fingerDetected"
    }

// region lifecycle

    override fun onResume() {
        super.onResume()

        dispose()
        subscription = CompositeDisposable()

        startWithPermissionCheck()
    }

    override fun onPause() {
        dispose()
        super.onPause()
    }

    private fun dispose() {
        if (subscription?.isDisposed == false)
            subscription?.dispose()
    }

// endregion

// region permission

    companion object {
        private val REQUEST_CAMERA_PERMISSION = 123
    }

    private fun checkPermissions(callbackId: Int, vararg permissionsId: String) {
        when {
            !hasPermission(*permissionsId) -> try {
                ActivityCompat.requestPermissions(this, permissionsId, callbackId)
            } catch (ex: Exception) {
                ex.printStackTrace()
            }
        }
    }

    private fun hasPermission(vararg permissionsId: String): Boolean {
        var hasPermission = true

        permissionsId.forEach { permission ->
            hasPermission = hasPermission
                    && ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED
        }

        return hasPermission
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            REQUEST_CAMERA_PERMISSION -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    startWithPermissionCheck()
                }
            }
        }
    }

// endregion
}