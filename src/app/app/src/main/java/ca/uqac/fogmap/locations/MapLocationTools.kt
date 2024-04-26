package ca.uqac.fogmap.locations

import android.content.Context
import ca.uqac.fogmap.data.FogLayerDataProvider
import ca.uqac.fogmap.data.model.TripListModel
import ca.uqac.fogmap.data.model.TripState
import ca.uqac.fogmap.ui.screens.map.polylineToDistanceInMeters
import com.esri.arcgisruntime.geometry.Polyline
import java.lang.System.currentTimeMillis
import java.util.concurrent.TimeUnit

fun saveCurrentTrip(context: Context) {
    if (FogLayerDataProvider.getInstance().currentTrip.size == 0) return

    val fogProvider = FogLayerDataProvider.getInstance()
    val tripPolyline = Polyline(fogProvider.currentTrip)

    val timeStamp = TimeUnit.MILLISECONDS.toSeconds(currentTimeMillis())


    val tripState = TripState(
        filename = "$timeStamp.arcgis.json",
        polyline = tripPolyline,
        date = timeStamp.toInt(),
        distance = polylineToDistanceInMeters(tripPolyline),
        surface = 0.0
    )
    TripListModel.getInstance().registerNewTrip(tripState, context)

    clearCurrentTrip()
}

fun clearCurrentTrip() {
    FogLayerDataProvider.getInstance().currentTrip.clear()
    FogLayerDataProvider.getInstance().currentTripUpdateCount.intValue = 0
}

fun deleteTripHistory(context: Context) {
    val files = context.filesDir.listFiles()
    files?.forEach {
        if (it.name.endsWith(".arcgis.json")
            || it.name.endsWith(".geojson")
        ) {
            it.delete()
        }
    }
    TripListModel.getInstance().clearTrips()
}






