package ca.uqac.fogmap.ui.screens.map

import android.annotation.SuppressLint
import android.graphics.drawable.BitmapDrawable
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
import androidx.core.content.ContextCompat
import ca.uqac.fogmap.data.FogLayerDataProvider
import com.mapbox.geojson.Feature
import com.mapbox.geojson.Point.fromLngLat
import com.mapbox.maps.ImageStretches
import com.mapbox.maps.MapboxExperimental
import com.mapbox.maps.extension.compose.DefaultSettingsProvider
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
import com.mapbox.maps.plugin.locationcomponent.createDefault2DPuck
import com.mapbox.maps.plugin.viewport.ViewportStatus
import com.mapbox.maps.toMapboxImage


@Composable
fun MapScreen_EntryPoint() {
    MapPermissionScreen {
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
            center(fromLngLat(5.670663602650166, 48.5382684))
            zoom(15.0)
            pitch(0.0)
            style { }
        }
    }

    val drawable = ContextCompat.getDrawable(context, ca.uqac.fogmap.R.drawable.fog_bg5)
    val fog_pattern = (drawable as BitmapDrawable).bitmap

    val newPolygon = FogLayerDataProvider.getInstance(context).getFogPolygon()

    var lines = ArrayList<Feature>().apply {
        FogLayerDataProvider.getInstance(context).getAllTripLines().forEach { line ->
            add(Feature.fromGeometry(line))
        }
    }

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
                            data = GeoJSONData(newPolygon),
                        )

                        LineLayer(layerId = "line_layer", sourceId = "line_source")
                        GeoJsonSource(
                            sourceId = "line_source",
                            data = GeoJSONData(lines),
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
            LaunchedEffect(Unit) {
                mapViewportState.transitionToFollowPuckState()
            }

            MapEffect { map ->
                map.mapboxMap.addStyleImage(
                    imageId = "fog",
                    scale = 8F,
                    image = fog_pattern.toMapboxImage(),
                    sdf = false,
                    stretchX = listOf(ImageStretches(0.0F, 0.0F), ImageStretches(1.0F, 1.0F)),
                    stretchY = listOf(ImageStretches(0.0F, 0.0F), ImageStretches(1.0F, 1.0F)),
                    content = null
                )
            }
        }
    }
}
