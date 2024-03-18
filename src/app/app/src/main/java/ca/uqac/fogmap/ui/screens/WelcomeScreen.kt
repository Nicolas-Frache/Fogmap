package ca.uqac.fogmap.ui.screens

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import ca.uqac.fogmap.ui.TextComponent

@Composable
fun WelcomeScreen(){
    Surface(
        modifier = Modifier.fillMaxSize()
    ) {
        TextComponent(textValue = "Bienvenue sur Fogmap !", textSize = 20.sp)
    }
}

@Preview
@Composable
fun WelcomeScreenPreview(){
    WelcomeScreen()
}