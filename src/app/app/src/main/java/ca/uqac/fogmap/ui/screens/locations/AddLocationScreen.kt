package ca.uqac.fogmap.ui.screens.locations

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import ca.uqac.fogmap.locations.MapLocations
import ca.uqac.fogmap.ui.screens.Routes


@Composable
fun AddLocation(navController: NavController) {
    var title by remember { mutableStateOf(TextFieldValue("")) }
    var description by remember { mutableStateOf(TextFieldValue("")) }


    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        TextField(
            value = title,
            onValueChange = { newText ->
                title = newText
            },
            label = { Text(text = "Titre") },
            modifier = Modifier.padding(bottom = 40.dp),
        )
        TextField(
            value = description,
            onValueChange = { newText ->
                description = newText
            },
            label = { Text(text = "Description") },
            modifier = Modifier.fillMaxWidth().height(400.dp)
        )
        Button(
            onClick = {
                MapLocations.map[title.text] = description.text
                navController.navigate(Routes.VISITED_LOCATION_SCREEN)
            },
            modifier = Modifier.padding(top = 40.dp),
        ) {
            Text(text = "Valider")
        }
    }
}
