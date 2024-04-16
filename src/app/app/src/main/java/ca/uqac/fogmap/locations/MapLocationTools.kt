package ca.uqac.fogmap.locations

import android.content.Context
import ca.uqac.fogmap.data.FogLayerDataProvider
import com.esri.arcgisruntime.geometry.Polyline
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.google.gson.JsonPrimitive
import java.lang.System.currentTimeMillis
import java.util.concurrent.TimeUnit

fun saveCurrentTrip(context: Context) {
    if (FogLayerDataProvider.getInstance().currentTrip.size == 0) return

    val fogProvider = FogLayerDataProvider.getInstance()

    val trip = Polyline(fogProvider.currentTrip).toJson()
    val timeStamp = TimeUnit.MILLISECONDS.toSeconds(currentTimeMillis())

    val jsontree = JsonParser.parseString(trip)
    val tripObject = JsonObject().apply {
        add("date", JsonPrimitive(timeStamp))
        add("distance", JsonPrimitive(fogProvider.getCurrentTripDistance().toDouble()))
        add("surface", JsonPrimitive(0.0))
        add("isVisible", JsonPrimitive(true))
    }
    jsontree.asJsonObject.add("fogmap_trip", tripObject)

    context.openFileOutput("$timeStamp.arcgis.json", Context.MODE_PRIVATE).use {
        it.write(
            Gson().toJson(jsontree).toByteArray()
        )
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
        if (it.name.endsWith(".arcgis.json")
            || it.name.endsWith(".geojson")
        ) {
            it.delete()
        }
    }
}






