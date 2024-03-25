package ca.uqac.fogmap.ui.screens

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ca.uqac.fogmap.common.customComposableViews.NormalButton
import ca.uqac.fogmap.common.customComposableViews.TextComponent
import ca.uqac.fogmap.ui.theme.AppTheme
import com.stevdzasan.onetap.OneTapGoogleButton
import com.stevdzasan.onetap.OneTapSignInWithGoogle
import com.stevdzasan.onetap.rememberOneTapSignInState


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WelcomeScreen() {
    val state = rememberOneTapSignInState()

    Surface(
        modifier = Modifier.fillMaxSize()
    ) {
        TextComponent(textValue = "Bienvenue sur Fogmap !", textSize = 20.sp)
        Log.d("LOG", "coucou")

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            NormalButton(
                text = "Se connecter",
                onClick = { state.open() },
                modifier = Modifier.width(300.dp)
            )
        }

        OneTapSignInWithGoogle(
            state = state,
            clientId = "235159561550-no10pkg5sueimsfl0c5ts8c92mna907q.apps.googleusercontent.com",
            onTokenIdReceived = { tokenId ->
                Log.d("LOG", tokenId)
            },
            onDialogDismissed = { message ->
                Log.d("LOG", message)
            }
        )
    }
}

@Preview
@Composable
fun WelcomeScreenPreview() {
    WelcomeScreen()

    OneTapGoogleButton(
        clientId = "235159561550-no10pkg5sueimsfl0c5ts8c92mna907q.apps.googleusercontent.com",
        onTokenIdReceived = { tokenId ->
            Log.d("LOG", tokenId)
        },
        onDialogDismissed = { message ->
            Log.d("LOG", message)
        }
    )
}