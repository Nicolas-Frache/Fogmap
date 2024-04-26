package ca.uqac.fogmap.ui.screens.locations

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ca.uqac.fogmap.locations.MapLocations
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.graphics.Color


@Composable
fun LocationInformation(name: String) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box {
            Text(
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 10.dp).padding(bottom = 40.dp),
                text = name,
                textAlign = TextAlign.Center,
                style = TextStyle(fontSize = 24.sp),
                color = Color.Black
            )
        }
        MapLocations.map[name]?.let {
            Box (
                modifier = Modifier.background(MaterialTheme.colorScheme.primary, RoundedCornerShape(10))
            ) {
                Text(
                    text = it,
                    modifier = Modifier
                        .padding(vertical = 20.dp, horizontal = 20.dp)
                        .fillMaxWidth(),
                    textAlign = TextAlign.Start,
                    color = Color.White
                )
            }
        }
    }
}
