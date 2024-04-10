package ca.uqac.fogmap.data

import android.content.Context
import android.util.Log
import ca.uqac.fogmap.ui.screens.map.argGiPolygonToMapBox
import ca.uqac.fogmap.ui.screens.map.geoJsonTripToPolyline
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

class FogLayerDataProvider private constructor() {
    private var allTripLines: ArrayList<Polyline> = ArrayList()
    val currentTrip: PointCollection = PointCollection(SpatialReferences.getWgs84())

    companion object {
        @Volatile
        private var instance: FogLayerDataProvider? = null

        fun getInstance() =
            instance ?: synchronized(this) {
                instance ?: FogLayerDataProvider().also { instance = it }
            }
    }

    fun initTracksData(context: Context) {
        val files: Array<String> = context.fileList()

        for (file in files) {
            if (file.contains(".geojson")) {
                allTripLines.add(geoJsonTripToPolyline(context, file))
            } else if (file.contains(".arcgis.json")) {
                allTripLines.add(Polyline.fromJson(
                    context.openFileInput(file).bufferedReader().use { it.readText() }
                ) as Polyline)
            }
        }
    }

    fun getFogPolygon(): GeometryMapBox {
        Log.d("FOGMAP", "getFogPolygon call with ${currentTrip.size} points")
        if (currentTrip.size != 0) allTripLines.add(Polyline(currentTrip))
        val bufferedPolygons = allTripLines.map {
            GeometryEngine.generalize(
                GeometryEngine.buffer(it, .0015),
                .00005,
                true
            )
        }
        if (currentTrip.size != 0) allTripLines.removeAt(allTripLines.size - 1)

        if (allTripLines.isEmpty()) return argGiPolygonToMapBox(bufferedPolygons[0])

        var union = bufferedPolygons[0]
        for (i in 1 until bufferedPolygons.size) {
            union = GeometryEngine.union(
                union,
                bufferedPolygons[i]
            )
        }
        return argGiPolygonToMapBox(GeometryEngine.difference(WHOLE_WORLD, union))
    }

    fun getAllTripLines(): List<LineString> {
        return allTripLines.map { polyline ->
            LineString.fromLngLats(
                polyline.parts[0].map { part ->
                    Point.fromLngLat(part.startPoint.y, part.startPoint.x)
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
}







