package ca.uqac.fogmap.ui.screens.map

import android.content.Context
import android.util.Log
import ca.uqac.fogmap.data.FogLayerDataProvider
import com.esri.arcgisruntime.geometry.Polyline
import com.mapbox.common.location.AccuracyLevel
import com.mapbox.common.location.DeviceLocationProvider
import com.mapbox.common.location.IntervalSettings
import com.mapbox.common.location.Location
import com.mapbox.common.location.LocationObserver
import com.mapbox.common.location.LocationProviderRequest
import com.mapbox.common.location.LocationService
import com.mapbox.common.location.LocationServiceFactory
import java.lang.System.currentTimeMillis
import java.util.concurrent.TimeUnit


fun initLocation(onFogUpdate: () -> Unit) {
    val locationService: LocationService = LocationServiceFactory.getOrCreate()
    var locationProvider: DeviceLocationProvider? = null

    val request = LocationProviderRequest.Builder()
        .interval(
            IntervalSettings.Builder()
                .interval(5000L).minimumInterval(5000L).maximumInterval(5000L)
                .build()
        )
        .displacement(10F)
        .accuracy(AccuracyLevel.HIGHEST)
        .build();

    val result = locationService.getDeviceLocationProvider(request)
    if (result.isValue) {
        locationProvider = result.value!!
    } else {
        Log.e("FOGMAP", "Failed to get device location provider")
    }

    val locationObserver = object : LocationObserver {
        override fun onLocationUpdateReceived(locations: MutableList<Location>) {
            updateLocation(locations[0], onFogUpdate)
        }
    }
    locationProvider?.addLocationObserver(locationObserver)
}

fun updateLocation(location: Location, onFogUpdate: () -> Unit) {
    Log.d("FOGMAP", "Location update received: $location")
    FogLayerDataProvider.getInstance().currentTrip.add(
        com.esri.arcgisruntime.geometry.Point(location.latitude, location.longitude)
    )
    onFogUpdate()
}

fun saveCurrentTrip(context: Context) {
    if (FogLayerDataProvider.getInstance().currentTrip.size == 0) return

    val trip = Polyline(FogLayerDataProvider.getInstance().currentTrip).toJson()
    val timeStamp: String = TimeUnit.MILLISECONDS.toSeconds(currentTimeMillis()).toString()

    context.openFileOutput("$timeStamp.arcgis.json", Context.MODE_PRIVATE).use {
        it.write(trip.toByteArray())
    }

    FogLayerDataProvider.getInstance().currentTrip.clear()
}








