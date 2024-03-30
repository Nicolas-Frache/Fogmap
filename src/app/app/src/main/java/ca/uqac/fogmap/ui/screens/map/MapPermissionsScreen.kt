package ca.uqac.fogmap.ui.screens.map

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import ca.uqac.fogmap.utils.RequestLocationPermission
import kotlinx.coroutines.launch

/**
 * Example to showcase usage of Location Component.
 */
@Composable
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
fun MapPermissionScreen(mapScreenContent :  @Composable () -> Unit) {
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    var permissionRequestCount by remember {
        mutableStateOf(1)
    }
    var showMap by remember {
        mutableStateOf(false)
    }
    var showRequestPermissionButton by remember {
        mutableStateOf(false)
    }

    Scaffold(
        snackbarHost = {
            SnackbarHost(snackbarHostState)
        }
    ) {
        RequestLocationPermission(
            requestCount = permissionRequestCount,
            onPermissionDenied = {
                scope.launch {
                    snackbarHostState.showSnackbar("Vous devez accepter les permissions.")
                }
                showRequestPermissionButton = true
            },
            onPermissionReady = {
                showRequestPermissionButton = false
                showMap = true
            }
        )
        if (showMap) {
            mapScreenContent()
        }
        if (showRequestPermissionButton) {
            Box(modifier = Modifier.fillMaxSize()) {
                Column(modifier = Modifier.align(Alignment.Center)) {
                    Button(
                        modifier = Modifier.align(Alignment.CenterHorizontally),
                        onClick = {
                            permissionRequestCount += 1
                        }
                    ) {
                        Text("Demander les permissions une nouvelle fois ($permissionRequestCount)")
                    }
                    Button(
                        modifier = Modifier.align(Alignment.CenterHorizontally),
                        onClick = {
                            context.startActivity(
                                Intent(
                                    android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                                    Uri.fromParts("package", "MainActivity", null)
                                )
                            )
                        }
                    ) {
                        Text("Afficher les paramètres de l'application")
                    }
                }
            }
        }
    }
}