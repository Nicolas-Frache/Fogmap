package ca.uqac.fogmap.ui.screens.map

import android.annotation.SuppressLint
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.viewinterop.NoOpUpdate
import androidx.core.graphics.drawable.toBitmap
import ca.uqac.fogmap.R
import com.mapbox.geojson.LineString
import com.mapbox.geojson.Point
import com.mapbox.geojson.Polygon
import com.mapbox.maps.MapView
import com.mapbox.maps.MapboxExperimental
import com.mapbox.maps.Style
import com.mapbox.maps.extension.compose.DefaultSettingsProvider
import com.mapbox.maps.extension.compose.MapboxMap
import com.mapbox.maps.extension.compose.animation.viewport.rememberMapViewportState
import com.mapbox.maps.extension.compose.style.MapboxStandardStyle
import com.mapbox.maps.extension.compose.style.layers.generated.FillColor
import com.mapbox.maps.extension.compose.style.layers.generated.FillEmissiveStrength
import com.mapbox.maps.extension.compose.style.layers.generated.FillLayer
import com.mapbox.maps.extension.compose.style.layers.generated.FillOpacity
import com.mapbox.maps.extension.compose.style.sources.generated.GeoJSONData
import com.mapbox.maps.extension.compose.style.sources.generated.GeoJsonSource
import com.mapbox.maps.extension.compose.style.sources.generated.LineMetrics
import com.mapbox.maps.extension.style.style
import com.mapbox.maps.plugin.PuckBearing
import com.mapbox.maps.plugin.annotation.annotations
import com.mapbox.maps.plugin.gestures.addOnMapClickListener
import com.mapbox.maps.plugin.locationcomponent.createDefault2DPuck
import com.mapbox.maps.plugin.viewport.ViewportStatus

@Composable
fun MapScreen_EntryPoint() {
    MapPermissionScreen {
        //CustomMapBox()
        MapScreen_Map()
    }
}

@OptIn(MapboxExperimental::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
private fun MapScreen_Map() {
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }
    val mapViewportState = rememberMapViewportState {
        setCameraOptions {
            center(com.mapbox.geojson.Point.fromLngLat(80.24585723876953, 13.071117736521623))
            zoom(0.0)
            pitch(0.0)
            style { }
        }
    }

    val inner = LineString.fromLngLats(
        listOf(
            Point.fromLngLat(-71.1235261, 48.337025),
            Point.fromLngLat(-70.96889, 48.37777902245179),
            Point.fromLngLat(-71.019248, 48.433092773753465),
            Point.fromLngLat(-71.113396, 48.409884),
            Point.fromLngLat(-71.1235261, 48.337025),
        )
    )

    val inner2 = LineString.fromLngLats(
        listOf(
            Point.fromLngLat(-71.0448, 48.4208),
            Point.fromLngLat(-70.9468, 48.5242),
            Point.fromLngLat(-70.5183, 48.3043),
            Point.fromLngLat(-71.0448, 48.4208),
        )
    )

    val outer = LineString.fromLngLats(
        listOf(
            Point.fromLngLat(-180.0, -90.0),
            Point.fromLngLat(-180.0, 90.0),
            Point.fromLngLat(180.0, 90.0),
            Point.fromLngLat(180.0, -90.0),
            Point.fromLngLat(-180.0, -90.0),
        )
    )
    val fogPolygon = Polygon.fromOuterInner(outer, listOf(inner, inner2))


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
                    middleSlot = {
                        //BackgroundLayer(
                        //    layerId = "background-layer-at-position",
                        //    backgroundColor = BackgroundColor(Color(0xFF000000)),
                        //    backgroundOpacity = BackgroundOpacity(0.3),
                        //)
                        FillLayer(
                            layerId = "fog_layer",
                            sourceId = "fog_polygon_source",
                            fillColor = FillColor(Color(0xFF31478B)),
                            fillOpacity = FillOpacity(0.35),
                            fillEmissiveStrength = FillEmissiveStrength(.6),
                        )

                        GeoJsonSource(
                            sourceId = "fog_polygon_source",
                            data = GeoJSONData(fogPolygon),
                            lineMetrics = LineMetrics(true)
                        )
                        //GeoJsonSource(
                        //    sourceId = "line_source",
                        //    data = GeoJSONData(line),
                        //    lineMetrics = LineMetrics(true)
                        //)
                    },
                    bottomSlot = {

                    }

                )
            },
            mapViewportState = mapViewportState,
            locationComponentSettings = DefaultSettingsProvider.defaultLocationComponentSettings(
                context
            ).toBuilder()
                .setLocationPuck(createDefault2DPuck(withBearing = true))
                .setPuckBearingEnabled(true)
                .setPuckBearing(PuckBearing.HEADING)
                .setEnabled(true)
                .build(),

            ) {
            LaunchedEffect(Unit) {
                mapViewportState.transitionToFollowPuckState()
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
        context.getDrawable(R.drawable.jetpack_compose_logo)!!.toBitmap()
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



