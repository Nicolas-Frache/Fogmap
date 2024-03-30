package ca.uqac.fogmap.ui.screens.map

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionStatus
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.shouldShowRationale
import com.mapbox.geojson.Point
import com.mapbox.maps.MapboxExperimental
import com.mapbox.maps.extension.compose.MapboxMap
import com.mapbox.maps.extension.compose.animation.viewport.MapViewportState

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun MapScreenEntryPoint(navController: NavHostController) {
    val localizationPermissionState = rememberPermissionState(
        android.Manifest.permission.ACCESS_FINE_LOCATION
    )

    when (localizationPermissionState.status) {
        // If the camera permission is granted, then show screen with the feature enabled
        PermissionStatus.Granted -> {
            MapScreen_EntryPoint(navController)
        }

        is PermissionStatus.Denied -> {
            Column {
                val textToShow = if (localizationPermissionState.status.shouldShowRationale) {
                    "Cette application nécessite de connaître votre localisation pour fonctionner."
                } else {
                    "Cette application nécessite de connaître votre localisation pour fonctionner. " +
                            "Merci d'accorder l'autorisation à l'application"
                }
                Text(textToShow)
                Button(onClick = { localizationPermissionState.launchPermissionRequest() }) {
                    Text("Demander la permission")
                }
            }
        }
    }
}


@OptIn(MapboxExperimental::class)
@Composable
private fun MapScreen_EntryPoint(navController: NavHostController) {
    Surface(
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(0.dp)
        ) {



            MapboxMap(
                Modifier.fillMaxSize(),
                mapViewportState = MapViewportState().apply {
                    setCameraOptions {
                        zoom(2.0)
                        center(Point.fromLngLat(-98.0, 39.5))
                        pitch(0.0)
                        bearing(0.0)
                    }
                },
            )

        }
    }
}
