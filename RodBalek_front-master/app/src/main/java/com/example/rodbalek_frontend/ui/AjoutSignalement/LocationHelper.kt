package com.example.rodbalek_frontend.ui.AjoutSignalement

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay

class LocationHelper(
    private val context: Context,
    private val map: MapView,
    private val marker: Marker
) {

    private lateinit var locationOverlay: MyLocationNewOverlay

    fun enableLocation(onUpdate: (Double, Double) -> Unit) {

        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(context, "Permission GPS manquante", Toast.LENGTH_SHORT).show()
            return
        }

        locationOverlay = MyLocationNewOverlay(GpsMyLocationProvider(context), map)
        locationOverlay.enableMyLocation()
        map.overlays.add(locationOverlay)

        locationOverlay.runOnFirstFix {
            val loc = locationOverlay.myLocation
            loc?.let {
                onUpdate(it.latitude, it.longitude)

                marker.position = GeoPoint(it.latitude, it.longitude)
                Handler(Looper.getMainLooper()).post {
                    map.controller.animateTo(marker.position)
                }

                Log.d("GPS", "Lat=${it.latitude}, Lon=${it.longitude}")
            }
        }
    }

        fun moveToMyPosition() {
            val loc = locationOverlay.myLocation ?: return
            Handler(Looper.getMainLooper()).post {
                val point = GeoPoint(loc.latitude, loc.longitude)
                marker.position = point
                map.controller.setZoom(17.0)
                map.controller.animateTo(point)
            }
        }


}
