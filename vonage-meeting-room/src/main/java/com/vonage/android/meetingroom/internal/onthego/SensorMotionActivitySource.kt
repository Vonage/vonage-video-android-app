package com.vonage.android.meetingroom.internal.onthego

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Handler
import android.os.Looper
import android.os.SystemClock

private const val AXIS_COUNT = 3

/**
 * Motion source built from the linear accelerometer, or the accelerometer with
 * gravity removed. No runtime permission and no Play Services dependency.
 *
 * Samples are classified once per second and delivered on the main thread.
 */
internal class SensorMotionActivitySource(
    context: Context,
    private val sensorManager: SensorManager = context.applicationContext
        .getSystemService(Context.SENSOR_SERVICE) as SensorManager,
    private val classifier: MotionWindowClassifier = MotionWindowClassifier(),
    private val extractor: LinearAccelerationExtractor = LinearAccelerationExtractor(),
) : MotionActivitySource {
    private val sensor: Sensor? = sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION)
        ?: sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)

    override val isAvailable: Boolean = sensor != null

    private var handler: ((MotionSample) -> Unit)? = null

    private val listener = object : SensorEventListener {
        override fun onSensorChanged(event: SensorEvent) {
            val sample = sampleFrom(event) ?: return
            handler?.invoke(sample)
        }

        override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) = Unit
    }

    override fun start(handler: (MotionSample) -> Unit) {
        val activeSensor = sensor ?: return
        this.handler = handler
        sensorManager.registerListener(
            listener,
            activeSensor,
            SensorManager.SENSOR_DELAY_NORMAL,
            Handler(Looper.getMainLooper()),
        )
    }

    override fun stop() {
        handler = null
        sensorManager.unregisterListener(listener)
    }

    private fun sampleFrom(event: SensorEvent): MotionSample? {
        val values = event.values
        if (values.size < AXIS_COUNT) return null
        val includesGravity = event.sensor.type != Sensor.TYPE_LINEAR_ACCELERATION
        val magnitude = extractor.linearMagnitude(
            x = values[AXIS_X],
            y = values[AXIS_Y],
            z = values[AXIS_Z],
            includesGravity = includesGravity,
        )
        return classifier.onMagnitude(SystemClock.elapsedRealtime(), magnitude)
    }

    private companion object {
        const val AXIS_X = 0
        const val AXIS_Y = 1
        const val AXIS_Z = 2
    }
}
