package ca.uqac.fogmap.ui.screens.questions

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
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
fun QuestionListPage(navController: NavController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Liste des questions",
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            textAlign = TextAlign.Center,
            style = TextStyle(fontSize = 24.sp)
        )
        questions.forEachIndexed { index, question ->
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
                    .clickable { navController.navigate("${Routes.QUESTION}/$index") }
                    .background(color = Color(0xFFBA9EDD), shape = RoundedCornerShape(50))
            ) {
                Text(
                    text = question.text,
                    modifier = Modifier.padding(8.dp).padding(start = 16.dp),
                    color = Color.Black,
                    style = TextStyle(fontSize = 18.sp)
                )
            }
        }
    }
}

