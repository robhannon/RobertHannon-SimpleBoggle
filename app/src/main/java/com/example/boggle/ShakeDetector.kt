package com.example.boggle

import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager

class ShakeDetector(private val onShake: () -> Unit) : SensorEventListener {

    private var shakeTimestamp: Long = 0
    private val shakeThreshold = 500
    private val shakeCountResetTime = 3000
    private var shakeCount = 0

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (event?.sensor?.type != Sensor.TYPE_ACCELEROMETER) return

        val gX = event.values[0] / SensorManager.GRAVITY_EARTH
        val gY = event.values[1] / SensorManager.GRAVITY_EARTH
        val gZ = event.values[2] / SensorManager.GRAVITY_EARTH

        val gForce = Math.sqrt((gX * gX + gY * gY + gZ * gZ).toDouble()).toFloat()
        if (gForce > SHAKE_THRESHOLD_GRAVITY) {
            val now = System.currentTimeMillis()
            if (shakeTimestamp + shakeThreshold > now) {
                return
            }

            if (shakeTimestamp + shakeCountResetTime < now) {
                shakeCount = 0
            }

            shakeTimestamp = now
            shakeCount++

            onShake()
        }
    }

    companion object {
        private const val SHAKE_THRESHOLD_GRAVITY = 2.7f
    }
}
