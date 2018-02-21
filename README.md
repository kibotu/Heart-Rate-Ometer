[![Donation](https://img.shields.io/badge/donate-please-brightgreen.svg)](https://www.paypal.me/janrabe) [![About Jan Rabe](https://img.shields.io/badge/about-me-green.svg)](https://about.me/janrabe) 
# Heart-Rate-Ometer [![Android Arsenal](https://img.shields.io/badge/Android%20Arsenal-Heart--Rate--Ometer-green.svg?style=flat)](https://android-arsenal.com/details/1/6681) [![](https://jitpack.io/v/kibotu/Heart-Rate-Ometer.svg)](https://jitpack.io/#kibotu/Heart-Rate-Ometer) [![Javadoc](https://img.shields.io/badge/javadoc-SNAPSHOT-green.svg)](https://jitpack.io/com/github/kibotu/Heart-Rate-Ometer/master-SNAPSHOT/javadoc/index.html) [![Build Status](https://travis-ci.org/kibotu/Heart-Rate-Ometer.svg?branch=master)](https://travis-ci.org/kibotu/Heart-Rate-Ometer) [![API](https://img.shields.io/badge/API-15%2B-brightgreen.svg?style=flat)](https://android-arsenal.com/api?level=15)  [![Gradle Version](https://img.shields.io/badge/gradle-4.5.1-green.svg)](https://docs.gradle.org/current/release-notes) [![Retrolambda](https://img.shields.io/badge/kotlin-1.2.21-green.svg)](https://kotlinlang.org/) [![Licence](https://img.shields.io/badge/licence-Apache%202-blue.svg)](https://raw.githubusercontent.com/kibotu/Heart-Rate-Ometer/master/LICENSE)

## Introduction

Measures human heart rate using camera and flash light.

![Screenshot](https://raw.githubusercontent.com/kibotu/Heart-Rate-Ometer/master/screenshot.png) ![Screenshot](https://raw.githubusercontent.com/kibotu/Heart-Rate-Ometer/master/hand_on_phone.png) 

## How-it-works

[https://github.com/phishman3579/android-heart-rate-monitor/wiki/How-it-works.](https://github.com/phishman3579/android-heart-rate-monitor/wiki/How-it-works.)

## How to install

    repositories {
        maven {
            url "https://jitpack.io"
        }
    }

    dependencies {
        implementation 'com.github.kibotu:Heart-Rate-Ometer:-SNAPSHOT'
    }
    
## How to use

0 [Request camera permission](https://github.com/kibotu/Heart-Rate-Ometer/blob/master/app/src/main/kotlin/net/kibotu/heartrateometer/MainActivity.kt#L24-L27)

     Manifest.permission.CAMERA

1 [Add surfaceView to your layout](https://github.com/kibotu/Heart-Rate-Ometer/blob/master/app/src/main/res/layout/activity_main.xml#L18-L22) which is required for the camera preview 

    <SurfaceView
        android:id="@+id/preview"
        android:layout_width="1dp"
        android:layout_height="1dp"
        android:visibility="visible" />
        
2 [Subscribe to the beats per minute stream](https://github.com/kibotu/Heart-Rate-Ometer/blob/master/app/src/main/kotlin/net/kibotu/heartrateometer/MainActivity.kt#L29-L30)

    HeartRateOmeter()
            .bpmUpdates(context, preview)
            .subscribe(
                    { bpm: Int -> label.text = "$bpm bpm" },
                    Throwable::printStackTrace)

## How to build

    graldew clean build
    
### CI 
    
    gradlew clean assembleRelease test javadoc
    
#### Build Requirements

- JDK8
- Android Build Tools 27.0.2
- Android SDK 27

## How to use


## Contributors

- [Jan Rabe](jan.rabe@kibotu.net) ported as Kotlin Rx Library: https://github.com/phishman3579/android-heart-rate-monitor written by
- [Justin Wetherell](https://github.com/phishman3579)

###License
<pre>
Copyright 2018 Jan Rabe

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
</pre>
