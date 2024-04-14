package ca.uqac.fogmap.ui.screens.map

import android.R
import android.annotation.SuppressLint
import android.graphics.drawable.BitmapDrawable
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import ca.uqac.fogmap.data.FogLayerDataProvider
import ca.uqac.fogmap.locations.saveCurrentTrip
import com.mapbox.geojson.Feature
import com.mapbox.geojson.Point.fromLngLat
import com.mapbox.maps.ImageStretches
import com.mapbox.maps.MapboxExperimental
import com.mapbox.maps.Style
import com.mapbox.maps.extension.compose.DefaultSettingsProvider
import com.mapbox.maps.extension.compose.MapEffect
import com.mapbox.maps.extension.compose.MapboxMap
import com.mapbox.maps.extension.compose.animation.viewport.rememberMapViewportState
import com.mapbox.maps.extension.compose.style.MapboxStandardStyle
import com.mapbox.maps.extension.compose.style.layers.generated.FillLayer
import com.mapbox.maps.extension.compose.style.layers.generated.FillOpacity
import com.mapbox.maps.extension.compose.style.layers.generated.FillPattern
import com.mapbox.maps.extension.compose.style.layers.generated.LineColor
import com.mapbox.maps.extension.compose.style.layers.generated.LineLayer
import com.mapbox.maps.extension.compose.style.layers.generated.LineWidth
import com.mapbox.maps.extension.compose.style.sources.generated.GeoJSONData
import com.mapbox.maps.extension.compose.style.sources.generated.GeoJsonSource
import com.mapbox.maps.extension.style.expressions.dsl.generated.get
import com.mapbox.maps.extension.style.expressions.dsl.generated.literal
import com.mapbox.maps.extension.style.expressions.generated.Expression.Companion.rgba
import com.mapbox.maps.extension.style.style
import com.mapbox.maps.plugin.PuckBearing
import com.mapbox.maps.plugin.locationcomponent.createDefault2DPuck
import com.mapbox.maps.plugin.viewport.ViewportStatus
import com.mapbox.maps.toMapboxImage
import kotlin.random.Random


@Composable
fun MapScreen_EntryPoint() {
    MapPermissionScreen {
        MapScreen_Map()
    }
}

@OptIn(MapboxExperimental::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter", "IncorrectNumberOfArgumentsInExpression")
@Composable
private fun MapScreen_Map() {
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }
    val mapViewportState = rememberMapViewportState {
        setCameraOptions {
            center(fromLngLat(5.670663602650166, 48.5382684))
            zoom(10.0)
            pitch(0.0)
            style { }
        }
    }

    val drawable = ContextCompat.getDrawable(context, ca.uqac.fogmap.R.drawable.fog_bg5)
    val fogPattern = (drawable as BitmapDrawable).bitmap

    FogLayerDataProvider.getInstance().initTracksData(context)
    var fogPolygon by remember {
        mutableStateOf(
            FogLayerDataProvider.getInstance().getFogPolygon()
        )
    }
    val onFogUpdate = {
        fogPolygon = FogLayerDataProvider.getInstance().getFogPolygon()
    }

    val lines = ArrayList<Feature>().apply {
        FogLayerDataProvider.getInstance().getAllTripLines().forEach { line ->
            add(Feature.fromGeometry(line).apply {
                addNumberProperty("color_r", Random.nextInt(0, 255))
                addNumberProperty("color_g", Random.nextInt(0, 255))
                addNumberProperty("color_b", Random.nextInt(0, 255))
            })
        }
    }

    val keyUpdateCurrentTrip by remember {
        FogLayerDataProvider.getInstance().currentTripUpdateCount
    }

    Scaffold(
        floatingActionButton = {
            // Show locate button when viewport is in Idle state, e.g. camera is controlled by gestures.
            Column {
                if (mapViewportState.mapViewportStatus == ViewportStatus.Idle) {
                    FloatingActionButton(
                        onClick = {
                            mapViewportState.transitionToFollowPuckState()
                        },
                        modifier = Modifier
                            .padding(bottom = 10.dp)
                            .align(Alignment.End),
                        shape = CircleShape
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.ic_menu_mylocation),
                            contentDescription = "Bouton Localisation"
                        )
                    }
                }
                FloatingActionButton(
                    onClick = {
                        Log.d("FOGMAP", "Save current trip")
                        saveCurrentTrip(context)
                    },
                    modifier = Modifier
                        .padding(bottom = 10.dp)
                        .align(Alignment.End),
                    shape = CircleShape
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_menu_save),
                        contentDescription = "Bouton sauvegarde"
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
                MapboxStandardStyle()
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
            // ================= LAYERS ======================

            LineLayer(
                layerId = "line_layer",
                sourceId = "line_source",
                lineColor = LineColor(
                    rgba(
                        get("color_r"),
                        get("color_g"),
                        get("color_b"),
                        literal(1.0)
                    )
                ),
                lineWidth = LineWidth(4.0)
            )
            GeoJsonSource(
                sourceId = "line_source",
                data = GeoJSONData(lines),
            )

            FillLayer(
                layerId = "fog_layer",
                sourceId = "fog_polygon_source",
                fillOpacity = FillOpacity(.7),
                fillPattern = FillPattern("fog"),
            )
            GeoJsonSource(
                sourceId = "fog_polygon_source",
                data = GeoJSONData(fogPolygon),
            )


            // ================= EFFECT ======================

            LaunchedEffect(Unit) {
                mapViewportState.transitionToFollowPuckState()
            }

            DisposableEffect(keyUpdateCurrentTrip) {
                Log.d("FOGMAP", "Current trip updated")
                onFogUpdate()

                //val onRemoveLocationProvider = initLocationProvider(onFogUpdate)
                onDispose {
                    //onRemoveLocationProvider()
                    //Log.d("FOGMAP", "Location observer removed")
                }
            }


            MapEffect { map ->
                map.mapboxMap.addStyleImage(
                    imageId = "fog",
                    scale = 8F,
                    image = fogPattern.toMapboxImage(),
                    sdf = false,
                    stretchX = listOf(ImageStretches(0.0F, 0.0F), ImageStretches(1.0F, 1.0F)),
                    stretchY = listOf(ImageStretches(0.0F, 0.0F), ImageStretches(1.0F, 1.0F)),
                    content = null
                )
                map.mapboxMap.loadStyle(Style.SATELLITE_STREETS)
            }
        }
    }
}

