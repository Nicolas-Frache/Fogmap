package ca.uqac.fogmap.ui.screens

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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Surface
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ca.uqac.fogmap.R
import ca.uqac.fogmap.common.customComposableViews.AlertDialogError
import ca.uqac.fogmap.common.customComposableViews.NormalButton
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
fun WelcomeScreen() {
    val context = LocalContext.current
    val token: String = stringResource(id = R.string.default_web_client_id)

    var user by remember { mutableStateOf(Firebase.auth.currentUser) }
    val openAlertDialog = remember { mutableStateOf(false) }
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

    Surface(
        modifier = Modifier.fillMaxSize()
    ) {

        Text(text = "Bienvenue sur Fogmap !", style = typography.bodyLarge)
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
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
                Spacer(modifier = Modifier.padding(15.dp))
                NormalButton(
                    text = "Se déconnecter",
                    modifier = Modifier.width(300.dp),
                    onClick = {
                        Firebase.auth.signOut()
                        user = null
                    }
                )
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
    WelcomeScreen()
}