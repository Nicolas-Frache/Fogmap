package ca.uqac.fogmap.data.model

import android.content.Context
import android.util.Log
import androidx.compose.runtime.MutableIntState
import androidx.compose.runtime.mutableIntStateOf
import ca.uqac.fogmap.data.FogLayerDataProvider
import ca.uqac.fogmap.ui.screens.map.geoJsonTripToPolyline
import ca.uqac.fogmap.ui.screens.map.polylineToDistanceInMeters
import com.esri.arcgisruntime.geometry.Polyline
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.google.gson.JsonPrimitive
import java.lang.Math.random
import java.nio.file.Files

class TripState(
    val filename: String = "",
    var isVisible: Boolean = true,
    val date: Int = 0,
    val distance: Number = 0.0,
    val surface: Number = 0.0,

    val polyline: Polyline? = null,
) {
    fun saveTrip(context: Context) {
        if (polyline == null) return

        val trip = polyline.toJson()
        val jsontree = JsonParser.parseString(trip)

        val tripObject = JsonObject().apply {
            add("date", JsonPrimitive(date))
            add("distance", JsonPrimitive(distance))
            add("surface", JsonPrimitive(surface))
            add("isVisible", JsonPrimitive(isVisible))
        }
        jsontree.asJsonObject.add("fogmap_trip", tripObject)

        Files.deleteIfExists(context.getFileStreamPath(filename).toPath())
        context.openFileOutput(filename, Context.MODE_PRIVATE).use {
            it.write(Gson().toJson(jsontree).toByteArray())
        }
    }

    fun deleteTrip(context: Context) {
        context.deleteFile(filename)
        TripListModel.getInstance().removeTrip(this)
        FogLayerDataProvider.getInstance().notifyTripListUpdate()
    }
}

class TripListModel(
    var trips: ArrayList<TripState> = ArrayList(),
    var tripsUpdateCount: MutableIntState = mutableIntStateOf(0),
) {
    companion object {
        @Volatile
        private var instance: TripListModel? = null

        fun getInstance() =
            instance ?: synchronized(this) {
                instance ?: TripListModel().also { instance = it }
            }
    }

    fun initTripListModel(context: Context) {
        Log.d("FOGMAP", "Nb trips in model : ${trips.size}")
        if (trips.isNotEmpty()) return

        val files: Array<String> = context.fileList()
        Log.d("FOGMAP", "files in trip list model : ${files.size}")
        for (file in files) {
            if (file.contains(".geojson")) {
                val polyline = geoJsonTripToPolyline(context, file)

                trips.add(
                    TripState(
                        filename = file.replace(".geojson", ".arcgis.json"),
                        polyline = polyline,
                        distance = polylineToDistanceInMeters(polyline),
                        date = (random() * 1000).toInt(),
                    )
                )
                context.deleteFile(file)
                trips.last().saveTrip(context)

            } else if (file.contains(".arcgis.json")) {
                trips.add(importArcJson(file, context))
            }
        }
    }

    private fun importArcJson(filename: String, context: Context): TripState {
        val text = context.openFileInput(filename).bufferedReader().use { it.readText() }
        val tripValues =
            JsonParser.parseString(text).asJsonObject.get("fogmap_trip").asJsonObject

        val polyline = Polyline.fromJson(
            context.openFileInput(filename).bufferedReader().use { it.readText() }
        ) as Polyline

        return TripState(
            filename = filename,
            date = tripValues.get("date").asInt,
            distance = tripValues.get("distance").asNumber,
            surface = tripValues.get("surface").asNumber,
            isVisible = tripValues.get("isVisible").asBoolean,
            polyline = polyline,
        )
    }

    fun registerNewTrip(tripState: TripState, context: Context) {
        trips.add(tripState)
        tripState.saveTrip(context)
        tripsUpdateCount = mutableIntStateOf(tripsUpdateCount.intValue + 1)
        FogLayerDataProvider.getInstance().notifyTripListUpdate()
    }

    fun removeTrip(tripState: TripState) {
        trips.remove(tripState)
        tripsUpdateCount = mutableIntStateOf(tripsUpdateCount.intValue + 1)
        FogLayerDataProvider.getInstance().notifyTripListUpdate()
    }

    fun clearTrips() {
        trips.clear()
        tripsUpdateCount = mutableIntStateOf(0)
        FogLayerDataProvider.getInstance().notifyTripListUpdate()
    }
}