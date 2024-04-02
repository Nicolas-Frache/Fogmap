import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import ca.uqac.fogmap.locations.MapLocations
import ca.uqac.fogmap.ui.screens.Routes

@Composable
fun TitledBubbleListPage(navController: NavController) {
    val items = MapLocations.map.keys.toList()
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Lieux visités",
            modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 16.dp),
            textAlign = TextAlign.Center,
            style = TextStyle(fontSize = 24.sp)
        )
        Spacer(modifier = Modifier.height(16.dp))
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            items(items) { item ->
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                        .background(Color(0xFFBA9EDD), RoundedCornerShape(50))
                        .clickable { navController.navigate("${Routes.LOCATION_INFORMATION}/" + item) }
                ) {
                    Text(
                        text = item,
                        modifier = Modifier.padding(8.dp).padding(start = 16.dp),
                        color = Color.Black
                    )
                }
            }
        }
    }
}