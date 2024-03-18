package ca.uqac.fogmap.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import ca.uqac.fogmap.ui.TextComponent

@Composable
fun UserInputScreen(navController: NavHostController) {
    Surface(
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(19.dp)
        )
        {
            TextComponent(textValue = "Paramètres...", textSize = 20.sp)
        }
    }
}

@Preview
@Composable
fun UserInputScreenPreview() {
    UserInputScreen(rememberNavController())
}