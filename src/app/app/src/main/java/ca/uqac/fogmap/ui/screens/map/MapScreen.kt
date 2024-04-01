package ca.uqac.fogmap.ui.screens.map

import android.annotation.SuppressLint
import android.graphics.drawable.BitmapDrawable
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.viewinterop.NoOpUpdate
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import com.esri.arcgisruntime.geometry.Geometry
import com.esri.arcgisruntime.geometry.GeometryEngine
import com.esri.arcgisruntime.geometry.PointCollection
import com.esri.arcgisruntime.geometry.Polyline
import com.esri.arcgisruntime.geometry.SpatialReferences
import com.google.gson.Gson
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.mapbox.bindgen.Value
import com.mapbox.geojson.FeatureCollection
import com.mapbox.geojson.LineString
import com.mapbox.geojson.Point
import com.mapbox.geojson.Point.fromLngLat
import com.mapbox.geojson.Polygon
import com.mapbox.maps.ImageStretches
import com.mapbox.maps.MapView
import com.mapbox.maps.MapboxExperimental
import com.mapbox.maps.Style
import com.mapbox.maps.extension.compose.DefaultSettingsProvider
import com.mapbox.maps.extension.compose.DisposableMapEffect
import com.mapbox.maps.extension.compose.MapEffect
import com.mapbox.maps.extension.compose.MapboxMap
import com.mapbox.maps.extension.compose.animation.viewport.rememberMapViewportState
import com.mapbox.maps.extension.compose.style.MapboxStandardStyle
import com.mapbox.maps.extension.compose.style.layers.generated.FillLayer
import com.mapbox.maps.extension.compose.style.layers.generated.FillOpacity
import com.mapbox.maps.extension.compose.style.layers.generated.FillPattern
import com.mapbox.maps.extension.compose.style.layers.generated.LineLayer
import com.mapbox.maps.extension.compose.style.sources.generated.GeoJSONData
import com.mapbox.maps.extension.compose.style.sources.generated.GeoJsonSource
import com.mapbox.maps.extension.style.style
import com.mapbox.maps.plugin.PuckBearing
import com.mapbox.maps.plugin.annotation.annotations
import com.mapbox.maps.plugin.gestures.addOnMapClickListener
import com.mapbox.maps.plugin.locationcomponent.createDefault2DPuck
import com.mapbox.maps.plugin.viewport.ViewportStatus
import com.mapbox.maps.toMapboxImage
import com.esri.arcgisruntime.geometry.Point as ArcGiPoint
import com.esri.arcgisruntime.geometry.Polygon as ArcGiPolygon


@Composable
fun MapScreen_EntryPoint() {
    MapPermissionScreen {
        //CustomMapBox()
        MapScreen_Map()
    }
}

fun ArgGiPolygonToMapBox(arcPolygon: Geometry): Polygon {
    val jsonObject = Gson().fromJson(arcPolygon.toJson(), JsonObject::class.java)
    val rings = jsonObject.getAsJsonArray("rings") as JsonArray

    var newOuter = LineString.fromLngLats(
        ArrayList<Point?>().apply {
            (rings[0] as JsonArray).forEach {
                val point = it as JsonArray
                add(fromLngLat(point[1].asDouble, point[0].asDouble))
            }
        }
    )
    if (rings.size() == 1) return Polygon.fromLngLats(arrayListOf(newOuter.coordinates()))
    val inners = ArrayList<LineString>()
    for(i in 1 until rings.size()) {
        val inner = LineString.fromLngLats(
            ArrayList<Point?>().apply {
                (rings[i] as JsonArray).forEach {
                    val point = it as JsonArray
                    add(fromLngLat(point[1].asDouble, point[0].asDouble))
                }
            }
        )
        inners.add(inner)
    }
    return Polygon.fromOuterInner(newOuter, inners)
}

@OptIn(MapboxExperimental::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
private fun MapScreen_Map() {
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }
    val mapViewportState = rememberMapViewportState {
        setCameraOptions {
            center(fromLngLat(5.670663602650166, 48.5382684))
            zoom(15.0)
            pitch(0.0)
            style { }
        }
    }

    val inner = LineString.fromLngLats(
        listOf(
            fromLngLat(-71.1235261, 48.337025),
            fromLngLat(-70.96889, 48.37777902245179),
            fromLngLat(-71.019248, 48.433092773753465),
            fromLngLat(-71.113396, 48.409884),
            fromLngLat(-71.1235261, 48.337025),
        )
    )
    var polygonPoints = PointCollection(SpatialReferences.getWgs84()).apply {
        inner.coordinates().forEach {
            add(ArcGiPoint(it.latitude(), it.longitude()))
        }
    }
    val _inner1 = (ArcGiPolygon(polygonPoints))

    val inner2 = LineString.fromLngLats(
        listOf(
            fromLngLat(-71.0448, 48.4208),
            fromLngLat(-70.9468, 48.5242),
            fromLngLat(-70.5183, 48.3043),
            fromLngLat(-71.0448, 48.4208),
        )
    )
    polygonPoints = PointCollection(SpatialReferences.getWgs84()).apply {
        inner2.coordinates().forEach {
            add(ArcGiPoint(it.latitude(), it.longitude()))
        }
    }
    val _inner2 = ArcGiPolygon(polygonPoints)


    val outer = LineString.fromLngLats(
        listOf(
            fromLngLat(-180.0, -90.0),
            fromLngLat(-180.0, 90.0),
            fromLngLat(180.0, 90.0),
            fromLngLat(180.0, -90.0),
            fromLngLat(-180.0, -90.0),
        )
    )
    polygonPoints = PointCollection(SpatialReferences.getWgs84()).apply {
        outer.coordinates().forEach {
            add(ArcGiPoint(it.latitude(), it.longitude()))
        }
    }
    val _outer = ArcGiPolygon(polygonPoints)

    val drawable = ContextCompat.getDrawable(context, ca.uqac.fogmap.R.drawable.fog_bg5)
    val bitmap = (drawable as BitmapDrawable).bitmap

    val text = context.resources.openRawResource(ca.uqac.fogmap.R.raw.track_points)
        .bufferedReader().use { it.readText() }

    val points = FeatureCollection.fromJson(text).features()!!.map { it.geometry() as Point }
    val line = LineString.fromLngLats(points)

    var arcPoints = PointCollection(SpatialReferences.getWgs84()).apply {
        line.coordinates().forEach {
            add(ArcGiPoint(it.latitude(), it.longitude()))
        }
    }
    val bufferedPolygon =  	GeometryEngine.generalize(GeometryEngine.buffer(Polyline(arcPoints), .003), .0001, true)

    var innerPolygon = (GeometryEngine.union(_inner1, _inner2))
    innerPolygon = (GeometryEngine.union(innerPolygon, bufferedPolygon))
    val polygon = (GeometryEngine.difference(_outer, innerPolygon))
    var fogPolygon = ArgGiPolygonToMapBox(polygon)


    Scaffold(
        floatingActionButton = {
            // Show locate button when viewport is in Idle state, e.g. camera is controlled by gestures.
            if (mapViewportState.mapViewportStatus == ViewportStatus.Idle) {
                FloatingActionButton(
                    onClick = {
                        mapViewportState.transitionToFollowPuckState()
                    }
                ) {
                    Image(
                        painter = painterResource(id = android.R.drawable.ic_menu_mylocation),
                        contentDescription = "Bouton Localisation"
                    )
                }
            }
        },
        floatingActionButtonPosition = FabPosition.End,
        snackbarHost = {
            SnackbarHost(snackbarHostState)
        }
    ) {
        MapboxMap(
            Modifier.fillMaxSize(),
            style = {
                MapboxStandardStyle(
                    topSlot = {
                        FillLayer(
                            layerId = "fog_layer",
                            sourceId = "fog_polygon_source",
                            fillOpacity = FillOpacity(1.0),
                            fillPattern = FillPattern("fog")
                        )
                        GeoJsonSource(
                            sourceId = "fog_polygon_source",
                            data = GeoJSONData(fogPolygon),
                        )

                        //FillLayer(
                        //    layerId = "debug_layer",
                        //    sourceId = "debug_source",
                        //    fillColor = FillColor(Color(0xFFFDF900)),
                        //    fillOpacity = FillOpacity(0.0)
                        //)
                        //GeoJsonSource(
                        //    sourceId = "debug_source",
                        //    data = GeoJSONData(ArgGiPolygonToMapBox(bufferedPolygon)),
                        //)

                        LineLayer(layerId = "line_layer", sourceId = "line_source")
                        GeoJsonSource(
                            sourceId = "line_source",
                            data = GeoJSONData(line),
                        )
                    },
                )
            },
            mapViewportState = mapViewportState,
            locationComponentSettings = DefaultSettingsProvider.defaultLocationComponentSettings(
                context
            )
                .toBuilder()
                .setLocationPuck(createDefault2DPuck(withBearing = true))
                .setPuckBearingEnabled(true)
                .setPuckBearing(PuckBearing.HEADING)
                .setEnabled(true)
                .build(),

            )
        {
            Log.d("FOGMAP", "MapboxMap block")
            LaunchedEffect(Unit) {
                //mapViewportState.transitionToFollowPuckState()
            }

            MapEffect { map ->
                //map.mapboxMap.addImage("fog", bitmap, false)
                val error = map.mapboxMap.addStyleImage(
                    imageId = "fog",
                    scale = 8F,
                    image = bitmap.toMapboxImage(),
                    sdf = false,
                    stretchX = listOf(ImageStretches(0.0F, 0.0F), ImageStretches(1.0F, 1.0F)),
                    stretchY = listOf(ImageStretches(0.0F, 0.0F), ImageStretches(1.0F, 1.0F)),
                    content = null
                )
                if (error != null) {
                    Log.d("FOGMAP", "Error: $error")
                }
            }


            DisposableMapEffect { map ->
                onDispose {
                    Log.d("FOGMAP", "DisposableMapEffect dispose")
                }
            }

        }
    }
}

@Composable
private fun CustomMapBox(
    //modifier: Modifier = Modifier,
    //onPointChange: (Point) -> Unit,
    //point: Point?,
) {
    val context = LocalContext.current
    val marker = remember(context) {
        context.getDrawable(ca.uqac.fogmap.R.drawable.jetpack_compose_logo)!!.toBitmap()
    }
    AndroidView(
        factory = {
            MapView(it).also { mapView ->
                mapView.mapboxMap.loadStyle(Style.SATELLITE)
                val annotationApi = mapView.annotations

                mapView.mapboxMap.addOnMapClickListener { p ->
                    //onPointChange(p)
                    true
                }
            }
        },
        update = { mapView ->
            NoOpUpdate
        },
    )
}



