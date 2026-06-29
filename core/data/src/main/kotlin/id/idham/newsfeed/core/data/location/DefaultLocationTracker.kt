package id.idham.newsfeed.core.data.location

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Geocoder
import android.os.Build
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import kotlinx.coroutines.suspendCancellableCoroutine
import java.util.Locale
import kotlin.coroutines.resume

class DefaultLocationTracker(
    private val fusedLocationProviderClient: FusedLocationProviderClient,
    private val context: Context
) : LocationTracker {


    override suspend fun getCountryCodeFromLocation(latitude: Double, longitude: Double): String? {
        return suspendCancellableCoroutine { cont ->
            val geocoder = Geocoder(context, Locale.getDefault())
            try {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    geocoder.getFromLocation(latitude, longitude, 1) { addresses ->
                        val countryCode = addresses.firstOrNull()?.countryCode?.lowercase()
                        cont.resume(countryCode)
                    }
                } else {
                    @Suppress("DEPRECATION")
                    val addresses = geocoder.getFromLocation(latitude, longitude, 1)
                    val countryCode = addresses?.firstOrNull()?.countryCode?.lowercase()
                    cont.resume(countryCode)
                }
            } catch (e: Exception) {
                cont.resume(null)
            }
        }
    }

    @SuppressLint("MissingPermission")
    override suspend fun getCurrentLocationCoordinates(): Pair<Double, Double>? {
        val hasAccessFineLocationPermission = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
        val hasAccessCoarseLocationPermission = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
        if (!hasAccessCoarseLocationPermission && !hasAccessFineLocationPermission) return null

        return suspendCancellableCoroutine { cont ->
            fusedLocationProviderClient.lastLocation.addOnSuccessListener { location ->
                if (location != null) {
                    cont.resume(Pair(location.latitude, location.longitude))
                } else {
                    cont.resume(null)
                }
            }.addOnFailureListener {
                cont.resume(null)
            }.addOnCanceledListener {
                cont.resume(null)
            }
        }
    }
}
