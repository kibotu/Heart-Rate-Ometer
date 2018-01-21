package de.charite.balsam.utils.camera

import android.support.annotation.IntDef

@Target(AnnotationTarget.TYPE)
@IntDef(0, 90, 180, 270)
@Retention(AnnotationRetention.SOURCE)
annotation class CameraOrientation