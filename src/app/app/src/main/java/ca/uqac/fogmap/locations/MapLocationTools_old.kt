package ca.uqac.fogmap.locations

import android.content.Context
import ca.uqac.fogmap.data.FogLayerDataProvider
import com.esri.arcgisruntime.geometry.Polyline
import java.lang.System.currentTimeMillis
import java.util.concurrent.TimeUnit

fun saveCurrentTrip(context: Context) {
    if (FogLayerDataProvider.getInstance().currentTrip.size == 0) return

    val trip = Polyline(FogLayerDataProvider.getInstance().currentTrip).toJson()
    val timeStamp = TimeUnit.MILLISECONDS.toSeconds(currentTimeMillis()).toString()

    context.openFileOutput("$timeStamp.arcgis.json", Context.MODE_PRIVATE).use {
        it.write(trip.toByteArray())
    }
    clearCurrentTrip()
}

fun clearCurrentTrip() {
    FogLayerDataProvider.getInstance().currentTrip.clear()
    FogLayerDataProvider.getInstance().currentTripUpdateCount.intValue = 0
}

fun deleteTripHistory(context: Context) {
    val files = context.filesDir.listFiles()
    files?.forEach {
        if (it.name.endsWith(".arcgis.json")) {
            it.delete()
        }
    }
}






