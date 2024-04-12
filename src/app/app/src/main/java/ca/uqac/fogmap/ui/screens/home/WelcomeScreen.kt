package ca.uqac.fogmap.ui.screens.home

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ca.uqac.fogmap.MainActivity
import ca.uqac.fogmap.R
import ca.uqac.fogmap.common.customComposableViews.AlertDialogError
import ca.uqac.fogmap.common.customComposableViews.MediumTitleText
import ca.uqac.fogmap.common.customComposableViews.NormalButton
import ca.uqac.fogmap.data.FogLayerDataProvider
import ca.uqac.fogmap.locations.LocationService
import ca.uqac.fogmap.locations.saveCurrentTrip
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.Firebase
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.auth
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await


@Composable
fun WelcomeScreen(mainActivity: MainActivity) {
    val context = LocalContext.current
    val token: String = stringResource(id = R.string.default_web_client_id)

    var user by remember { mutableStateOf(Firebase.auth.currentUser) }
    val openAlertDialog = remember { mutableStateOf(false) }
    val openSaveTripDialog = remember { mutableStateOf(false) }
    val launcher = rememberFirebaseAuthLauncher(
        onAuthComplete = { result ->
            user = result.user
        },
        onAuthError = {
            Log.d("FOGMAP", it.toString())
            user = null
            openAlertDialog.value = !openAlertDialog.value
        }
    )
    val isServiceRunning = remember {
        mutableStateOf(FogLayerDataProvider.getInstance().currentTrip.size != 0)
    }
    val keyUpdateCurrentTrip by remember {
        FogLayerDataProvider.getInstance().currentTripUpdateCount
    }
    val distance = remember { mutableStateOf("0") }
    LaunchedEffect(key1 = keyUpdateCurrentTrip) {
        distance.value = FogLayerDataProvider.getInstance().getCurrentTripDistance()
    }

    when {
        openAlertDialog.value -> {
            AlertDialogError(
                onDismissRequest = {
                    openAlertDialog.value = false
                    tryFirebaseLog(context, token, launcher)
                },
                onConfirmation = {
                    openAlertDialog.value = false
                },
                dialogText = "Erreur lors de la connexion.",
            )

        }
    }

    fun stopServiceAndCloseDialog(saveTrip: Boolean) {
        Intent(context, LocationService::class.java).apply {
            action = LocationService.ACTION_STOP
            mainActivity.stopService(this)
        }
        openSaveTripDialog.value = false
        isServiceRunning.value = false
        if (saveTrip) saveCurrentTrip(context)
    }

    when {
        openSaveTripDialog.value -> {
            StopTripDialog(
                onDontSaveTrip = {
                    stopServiceAndCloseDialog(false)
                },
                onSaveTrip = {
                    stopServiceAndCloseDialog(true)
                },
                onDismissDialog = {
                    openSaveTripDialog.value = false
                }
            )
        }
    }

    Surface(
        modifier = Modifier.fillMaxSize()
    ) {

        Text(text = "Bienvenue sur Fogmap !", style = typography.bodyLarge)
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterVertically),
        ) {
            if (user == null) {
                Text("Non connecté")
                Spacer(modifier = Modifier.padding(15.dp))
                NormalButton(
                    text = "Se connecter avec Google",
                    modifier = Modifier.width(300.dp),
                    onClick = {
                        tryFirebaseLog(context, token, launcher)
                    })
            } else {
                Text("Bienvenue ${user!!.displayName}")
                Text("User UID:  ${user!!.uid}")
                NormalButton(
                    text = "Se déconnecter",
                    modifier = Modifier.width(300.dp),
                    onClick = {
                        Firebase.auth.signOut()
                        user = null
                    }
                )
            }
            Spacer(modifier = Modifier.padding(15.dp))

            Card(
                modifier = Modifier
                    .padding(10.dp)
                    .fillMaxWidth(),
                shape = MaterialTheme.shapes.medium,
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant,
                ),
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.Bottom),
                    modifier = Modifier
                        .padding(10.dp)
                        .fillMaxWidth()
                ) {
                    MediumTitleText(
                        text = "Trajet actuel",
                        modifier = Modifier.padding(16.dp),
                        textAlign = TextAlign.Center,
                    )

                    Text(
                        text = "Distance parcourue: ${distance.value} km",
                        modifier = Modifier.padding(16.dp),
                    )

                    when {
                        !isServiceRunning.value -> {
                            Button(
                                modifier = Modifier.align(Alignment.CenterHorizontally),
                                onClick = {
                                    Intent(context, LocationService::class.java).apply {
                                        action = LocationService.ACTION_START
                                        mainActivity.startService(this)
                                    }
                                    isServiceRunning.value = true
                                }) {
                                Text(text = "Démarrer l'enregistrement de la position")
                            }
                        }

                        else -> {
                            Button(
                                modifier = Modifier.align(Alignment.CenterHorizontally),
                                onClick = {
                                    openSaveTripDialog.value = true
                                }) {
                                Text(text = "Terminer le trajet en cours")
                            }
                        }
                    }
                }
            }

        }
    }
}

fun tryFirebaseLog(
    context: Context,
    token: String,
    launcher: ManagedActivityResultLauncher<Intent, ActivityResult>,
) {
    val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
        .requestIdToken(token)
        .requestEmail()
        .build()

    val googleSignInClient = GoogleSignIn.getClient(context, gso)
    googleSignInClient.signOut()
    launcher.launch(googleSignInClient.signInIntent)
}

@Composable
fun rememberFirebaseAuthLauncher(
    onAuthComplete: (AuthResult) -> Unit,
    onAuthError: (ApiException) -> Unit,
): ManagedActivityResultLauncher<Intent, ActivityResult> {

    val scope = rememberCoroutineScope()

    return rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult())
    { result ->
        val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
        try {
            val account = task.getResult(ApiException::class.java)!!
            val credential = GoogleAuthProvider.getCredential(account.idToken!!, null)
            scope.launch {
                val authResult = Firebase.auth.signInWithCredential(credential).await()
                onAuthComplete(authResult)
            }
        } catch (e: ApiException) {
            onAuthError(e)
        }
    }
}

@Preview
@Composable
fun WelcomeScreenPreview() {
    //WelcomeScreen(mainActivity, )
}