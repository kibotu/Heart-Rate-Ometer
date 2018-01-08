package net.kibotu.heartrateometer

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.activity_main.*
import net.kibotu.heartrateometer.app.R

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

        subscription?.add(HeartRateOmeter().bpmUpdates(this, preview)
                .subscribe(::onBpm, Throwable::printStackTrace))
    }

    @SuppressLint("SetTextI18n")
    private fun onBpm(bpm: Int) {
        label.text = "$bpm bpm"
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