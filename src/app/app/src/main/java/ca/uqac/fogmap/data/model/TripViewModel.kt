package ca.uqac.fogmap.data.model

import android.content.Context
import ca.uqac.fogmap.ui.screens.map.geoJsonTripToPolyline
import com.esri.arcgisruntime.geometry.Polyline
import com.google.gson.JsonParser

data class TripState(
    val isVisible: Boolean = false,
    val date: Int = 0,
    val distance: Int = 0,
    val surface: Int = 0,

    val polyline: Polyline? = null,
)

class TripListModel(
    var trips: ArrayList<TripState> = ArrayList(),
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
        if (trips.isNotEmpty()) return

        val files: Array<String> = context.fileList()
        for (file in files) {
            if (file.contains(".geojson")) {
                trips.add(
                    TripState(
                        polyline = geoJsonTripToPolyline(context, file)
                    )
                )
            } else if (file.contains(".arcgis.json")) {
                trips.add(importArcJson(file, context))
            }
        }
    }

    fun importArcJson(filename: String, context: Context): TripState {
        val text = context.openFileInput(filename).bufferedReader().use { it.readText() }
        val tripValues = JsonParser.parseString(text).asJsonObject.get("fogmap_trip").asJsonObject

        val polyline = Polyline.fromJson(
            context.openFileInput(filename).bufferedReader().use { it.readText() }
        ) as Polyline

        return TripState(
            date = tripValues.get("date").asInt,
            distance = tripValues.get("distance").asInt,
            surface = tripValues.get("surface").asInt,
            isVisible = tripValues.get("isVisible").asBoolean,
            polyline = polyline,
        )
    }
}