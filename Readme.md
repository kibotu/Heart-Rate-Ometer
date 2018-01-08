Heart-Rate-Ometer



https://github.com/phishman3579/android-heart-rate-monitor/wiki/How-it-works.

[![Donation](https://img.shields.io/badge/donate-please-brightgreen.svg)](https://www.paypal.me/janrabe) [![About Jan Rabe](https://img.shields.io/badge/about-me-green.svg)](https://about.me/janrabe) 
# KalmanRx [![Android Arsenal](https://img.shields.io/badge/Android%20Arsenal-KalmanRx-brightgreen.svg?style=flat)](http://android-arsenal.com/details/1/4539) [![](https://jitpack.io/v/kibotu/KalmanRx.svg)](https://jitpack.io/#kibotu/KalmanRx) [![Javadoc](https://img.shields.io/badge/javadoc-SNAPSHOT-green.svg)](https://jitpack.io/com/github/kibotu/KalmanRx/master-SNAPSHOT/javadoc/index.html) [![Build Status](https://travis-ci.org/kibotu/KalmanRx.svg?branch=master)](https://travis-ci.org/kibotu/KalmanRx) [![API](https://img.shields.io/badge/API-15%2B-brightgreen.svg?style=flat)](https://android-arsenal.com/api?level=15)  [![Gradle Version](https://img.shields.io/badge/gradle-3.1-green.svg)](https://docs.gradle.org/current/release-notes) [![Retrolambda](https://img.shields.io/badge/java-8-green.svg)](https://github.com/evant/gradle-retrolambda) [![Licence](https://img.shields.io/badge/licence-Apache%202-blue.svg)](https://raw.githubusercontent.com/kibotu/KalmanRx/master/LICENSE)

## Introduction

Removes the noise from float streams using [Kalman Filter](https://en.wikipedia.org/wiki/Kalman_filter). Useful to smoothen sensory data e.g.: [gps location](https://github.com/villoren/KalmanLocationManager), or [Accelerometer](https://developer.android.com/guide/topics/sensors/sensors_motion.html#sensors-motion-accel). 

![Screenshot](https://raw.githubusercontent.com/kibotu/KalmanRx/master/screenshot.png) ![Screenshot](https://raw.githubusercontent.com/kibotu/KalmanRx/master/screenshot2.png)

## [How to use](https://github.com/kibotu/KalmanRx/blob/master/app/src/main/java/net/kibotu/kalmanrx/app/ui/AccelerationSensorKalmanFragment.java#L16-L19)

Library is supporting up to 3 values smoothened from a stream.

(float) stream

  
    KalmanRx.createFrom1D(floatObservable..map(e -> e.value))
            .subscribe(value->{}, Throwable::printStackTrace);

(float, float) stream

  
    KalmanRx.createFrom2D(floatObservable..map(e -> e.values))
            .subscribe(values->{}, Throwable::printStackTrace);

(float, float, float) stream

    KalmanRx.createFrom3D(floatObservable..map(e -> e.values))
            .subscribe(value->{}, Throwable::printStackTrace);

## How to install

    compile 'com.github.kibotu:KalmanRx:-SNAPSHOT'

## How to build

    graldew clean build
    
### CI 
    
    gradlew clean assembleRelease test javadoc
    
#### Build Requirements

- JDK7, JDK8
- Android Build Tools 24.0.3
- Android SDK 24 

## How to use


## Contributors

[Jan Rabe](jan.rabe@kibotu.net)

###License
<pre>
Copyright 2016 Jan Rabe

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
