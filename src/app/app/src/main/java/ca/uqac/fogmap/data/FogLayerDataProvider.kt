package ca.uqac.fogmap.data

import android.content.Context
import android.util.Log
import androidx.compose.runtime.mutableIntStateOf
import ca.uqac.fogmap.data.model.TripListModel
import ca.uqac.fogmap.ui.screens.map.argGiPolygonToMapBox
import ca.uqac.fogmap.ui.screens.map.polylineToDistanceInFormattedString
import com.esri.arcgisruntime.geometry.GeometryEngine
import com.esri.arcgisruntime.geometry.PointCollection
import com.esri.arcgisruntime.geometry.Polygon
import com.esri.arcgisruntime.geometry.Polyline
import com.esri.arcgisruntime.geometry.SpatialReferences
import com.mapbox.geojson.LineString
import com.mapbox.geojson.Point
import com.mapbox.geojson.Geometry as GeometryMapBox

val WHOLE_WORLD = Polygon(PointCollection(SpatialReferences.getWgs84()).apply {
    add(90.0, -180.0)
    add(90.0, 180.0)
    add(-90.0, 180.0)
    add(-90.0, -180.0)
    add(90.0, -180.0)
})

class FogLayerDataProvider private constructor(
) {
    val currentTrip: PointCollection = PointCollection(SpatialReferences.getWgs84())
    var currentTripUpdateCount = mutableIntStateOf(0)
    lateinit var tripListModel: TripListModel

    companion object {
        @Volatile
        private var instance: FogLayerDataProvider? = null

        fun getInstance() =
            instance ?: synchronized(this) {
                instance ?: FogLayerDataProvider().also { instance = it }
            }
    }

    fun initTracksData(context: Context) {
        tripListModel = TripListModel.getInstance()
        tripListModel.initTripListModel(context)

    }

    fun getFogPolygon(): GeometryMapBox {
        Log.d("FOGMAP", "getFogPolygon call with ${currentTrip.size} points")
        val polylines = ArrayList(tripListModel.trips
            .filter { it.isVisible }
            .mapNotNull { it.polyline })
        if (currentTrip.size != 0) polylines.add(Polyline(currentTrip))

        if(polylines.isEmpty()) return argGiPolygonToMapBox(WHOLE_WORLD)

        val bufferedPolygons = polylines.map {
            GeometryEngine.generalize(
                GeometryEngine.buffer(it, .0015),
                .00005,
                true
            )
        }
        if (currentTrip.size != 0) polylines.removeAt(polylines.size - 1)

        var union = bufferedPolygons[0]
        if (tripListModel.trips.isNotEmpty()) {
            for (i in 1 until bufferedPolygons.size) {
                union = GeometryEngine.union(
                    union,
                    bufferedPolygons[i]
                )
            }
        }
        return argGiPolygonToMapBox(GeometryEngine.difference(WHOLE_WORLD, union))
    }

    fun getAllTripLines(): List<LineString> {
        return tripListModel.trips
            .filter { it.isVisible }
            .mapNotNull { it.polyline }
            .mapNotNull { polyline ->
                LineString.fromLngLats(
                    ArrayList(polyline.parts[0].map { part ->
                        Point.fromLngLat(part.startPoint.y, part.startPoint.x)
                    }).apply {
                        add(
                            Point.fromLngLat(
                                polyline.parts[0].last().endPoint.y,
                                polyline.parts[0].last().endPoint.x
                            )
                        )
                    }
                )
            }
    }

    fun getCurrentTripLine(): LineString {
        return LineString.fromLngLats(
            currentTrip.map {
                Point.fromLngLat(it.y, it.x)
            }
        )
    }

    fun updateCurrentTrip(lat: Double, long: Double) {
        currentTrip.add(com.esri.arcgisruntime.geometry.Point(lat, long))
        currentTripUpdateCount.intValue++
    }

    fun getCurrentTripDistance(): String {
        return polylineToDistanceInFormattedString(Polyline(currentTrip))
    }

    fun notifyTripListUpdate() {
        currentTripUpdateCount.intValue++
    }

}







