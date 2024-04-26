package ca.uqac.fogmap.ui.screens.locations

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
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
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavController
import ca.uqac.fogmap.ui.screens.Routes


@Composable
fun LocationInformation(name: String, navController: NavController) {
    val items = MapLocations.map.keys.toList()
    Column(
        modifier = Modifier
            .verticalScroll(rememberScrollState())
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box {
            Text(
                modifier = Modifier
                    .padding(horizontal = 20.dp, vertical = 10.dp)
                    .padding(bottom = 40.dp),
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
                    text = it.description,
                    modifier = Modifier
                        .padding(vertical = 20.dp, horizontal = 20.dp)
                        .fillMaxWidth(),
                    textAlign = TextAlign.Start,
                    color = Color.White
                )
            }
        }
        Box {
            Text(
                modifier = Modifier.padding(top = 40.dp, bottom = 40.dp),
                text = "Questions",
                textAlign = TextAlign.Center,
                style = TextStyle(fontSize = 24.sp),
                color = Color.Black
            )
        }
        Box (
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
        ){
            LazyColumn(
                horizontalAlignment = Alignment.CenterHorizontally,
                userScrollEnabled = false
            ) {
                MapLocations.questions[name]?.let {
                    items(it) { item ->
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp)
                                .background(MaterialTheme.colorScheme.primary, RoundedCornerShape(50))
                                .clickable { navController.navigate("${Routes.QUESTION}/${item.index}") }
                        ) {
                            Text(
                                text = item.text,
                                modifier = Modifier
                                    .padding(8.dp)
                                    .padding(start = 16.dp),
                                color = Color.White
                            )
                        }
                    }
                }
            }
        }
    }
}
