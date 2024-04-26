package ca.uqac.fogmap.ui.screens.map

import android.content.Context
import com.esri.arcgisruntime.geometry.Geometry
import com.esri.arcgisruntime.geometry.GeometryEngine
import com.esri.arcgisruntime.geometry.PointCollection
import com.esri.arcgisruntime.geometry.Polyline
import com.esri.arcgisruntime.geometry.SpatialReferences
import com.google.gson.Gson
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.mapbox.geojson.FeatureCollection
import com.mapbox.geojson.LineString
import com.mapbox.geojson.Point
import com.mapbox.geojson.Polygon

fun argGiPolygonToMapBox(arcPolygon: Geometry): Polygon {
    val jsonObject = Gson().fromJson(arcPolygon.toJson(), JsonObject::class.java)
    val rings = jsonObject.getAsJsonArray("rings") as JsonArray

    var newOuter = LineString.fromLngLats(
        ArrayList<Point?>().apply {
            (rings[0] as JsonArray).forEach {
                val point = it as JsonArray
                add(Point.fromLngLat(point[1].asDouble, point[0].asDouble))
            }
        }
    )
    if (rings.size() == 1) return Polygon.fromLngLats(arrayListOf(newOuter.coordinates()))
    val inners = ArrayList<LineString>()
    for (i in 1 until rings.size()) {
        val inner = LineString.fromLngLats(
            ArrayList<Point?>().apply {
                (rings[i] as JsonArray).forEach {
                    val point = it as JsonArray
                    add(Point.fromLngLat(point[1].asDouble, point[0].asDouble))
                }
            }
        )
        inners.add(inner)
    }
    return Polygon.fromOuterInner(newOuter, inners)
}

fun geoJsonTripToPolyline(context: Context, filename: String): Polyline {
    val text = context.openFileInput(filename).bufferedReader().use { it.readText() }

    val points = FeatureCollection.fromJson(text).features()!!.map { it.geometry() as Point }
    return Polyline(PointCollection(SpatialReferences.getWgs84()).apply {
        points.forEach {
            add(com.esri.arcgisruntime.geometry.Point(it.latitude(), it.longitude()))
        }
    })
}

fun polylineToDistanceInFormattedString(polyline: Polyline): String {
    return String.format(
        "%.2f",
        GeometryEngine.length(
            polyline
        ) * 100
    )
}

fun polylineToDistanceInMeters(polyline: Polyline): Double {
    return GeometryEngine.length(
        polyline
    ) * 100
}