package ca.uqac.fogmap.data

import android.content.Context
import android.util.Log
import ca.uqac.fogmap.R
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

var WholeWorld = Polygon(PointCollection(SpatialReferences.getWgs84()).apply {
    add(90.0, -180.0)
    add(90.0, 180.0)
    add(-90.0, 180.0)
    add(-90.0, -180.0)
    add(90.0, -180.0)
})
var AllTripLines: ArrayList<Polyline> = ArrayList()

class FogLayerDataProvider  {
    companion object {
        @Volatile
        private var instance: FogLayerDataProvider? = null // Volatile modifier is necessary

        fun getInstance() =
            instance ?: synchronized(this) { // synchronized to avoid concurrency problem
                instance ?: FogLayerDataProvider().also { instance = it }
            }
    }

    fun initWithMockData(context: Context) {
        AllTripLines.apply {
            add(geoJsonTripToPolyline(context, R.raw.sample_track_1))
            add(geoJsonTripToPolyline(context, R.raw.sample_track_2))
            add(geoJsonTripToPolyline(context, R.raw.sample_track_3))
        }
    }

    fun getFogPolygon(): GeometryMapBox {
        var bufferedPolygons = AllTripLines.map {
            GeometryEngine.generalize(
                GeometryEngine.buffer(it, .0015),
                .00005,
                true
            )
        }
        if (AllTripLines.isEmpty()) return argGiPolygonToMapBox(bufferedPolygons[0])

        var union = bufferedPolygons[0]
        for (i in 1 until bufferedPolygons.size) {
            union = GeometryEngine.union(
                union,
                bufferedPolygons[i]
            )
            Log.d("FOGMAP", "Union $i: ${union.toJson()} ")
        }
        Log.d("FOGMAP", "New Polygon 1: ${GeometryEngine.difference(WholeWorld, union).toJson()}")
        return argGiPolygonToMapBox(GeometryEngine.difference(WholeWorld, union))
    }

    fun getAllTripLines(): List<LineString> {
        return AllTripLines.map { polyline ->
            LineString.fromLngLats(
                polyline.parts[0].map { part ->
                    Point.fromLngLat(part.startPoint.y, part.startPoint.x)
                }
            )
        }
    }
}






