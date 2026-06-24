# Mobile Hardware Sensors

This project utilizes the device's hardware sensors to trigger specific application actions.

## 1. "Shake to Refresh" Feature
The application allows users to refresh the news feed by physically shaking the device. 

## 2. Sensor Details
- **Sensor Type**: `Sensor.TYPE_ACCELEROMETER`
- **Function**: Measures acceleration force along the three spatial axes (X, Y, and Z), including Earth's gravitational pull.

## 3. Algorithm Implementation (`ShakeDetector.kt`)
The feature is implemented within a Lifecycle-Aware component named `ShakeDetector`. The logic proceeds as follows:

1. The sensor outputs a force vector in m/s². This value is normalized by dividing it by Earth's gravity constant (approximately 9.81 m/s²).
2. The normalization results in a g-Force value for each axis: gX, gY, and gZ.
3. The total resultant force is calculated using the Pythagorean theorem:
   `gForce = √(gX² + gY² + gZ²)`
4. **Threshold Trigger**: A sensitivity threshold of `2.7F` is defined. If the total gForce exceeds this threshold, the application invokes the `refresh()` method on the Paging data stream.
5. **Debouncing**: A 500ms debounce interval is implemented to prevent multiple refresh calls from a single continuous motion.

*(Note: The sensor listener is automatically unregistered when the user navigates away from the Home Screen to optimize battery consumption).*

### Code Sample
Here is the core logic calculating the gravitational force threshold:

```kotlin
override fun onSensorChanged(event: SensorEvent?) {
    event?.let {
        val gX = it.values[0] / SensorManager.GRAVITY_EARTH
        val gY = it.values[1] / SensorManager.GRAVITY_EARTH
        val gZ = it.values[2] / SensorManager.GRAVITY_EARTH

        // Calculate total gForce (Pythagorean 3D theorem)
        val gForce = kotlin.math.sqrt((gX * gX + gY * gY + gZ * gZ).toDouble()).toFloat()

        if (gForce > SHAKE_THRESHOLD_GRAVITY) {
            val now = System.currentTimeMillis()
            if (now - lastShakeTime > SHAKE_SLOP_TIME_MS) {
                lastShakeTime = now
                onShake()
            }
        }
    }
}
```
